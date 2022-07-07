package com.wooim.event.service;


import com.wooim.event.domain.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sound.sampled.ReverbType;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReviewService {
    @Autowired
    ReviewRepository reviewRepository;
    @Autowired
    ReviewPointRepository reviewPointRepository;

    public Review getReview(String reviewId){
        Optional<Review> review = reviewRepository.findByReviewId(reviewId);

        return review.isEmpty() ? Review.builder().build() : review.get();
    }

    public List<Map<String, Object>> getReviewPointList (String reviewId){
        List<Map<String, Object>> list = reviewPointRepository.getUserPoint();

        return list;
    }

    /*
    * 리뷰 및 리뷰파일 등록
    */
    @Transactional
    public void saveReview(Review r) throws Exception
    {

        Optional<Review> data = reviewRepository.findByReviewId(r.getReviewId());
        if(r.getAction().equals("MODIFY")){
            if(data.isEmpty())
                throw new RuntimeException("등록 된 리뷰가 없습니다.");
        }

        //이력 저장
        saveReviewPoint(r, data);

        Review review = Review.builder()
                .reviewId(r.getReviewId())
                .type(r.getType())
                .action(r.getAction())
                .content(r.getContent())
                .userId(r.getUserId())
                .placeId(r.getPlaceId())
                .point(calPoint(r))
                .build();

        for(String photoId : r.getAttachedPhotoIds()){
            ReviewFile file = ReviewFile.builder()
                    .review(review)
                    .attachedPhotoId(photoId)
                    .build();
            review.getFileList().add(file);
        }

        reviewRepository.save(review);
    }

    @Transactional
    public void deleteReview(String reviewId){
        deleteReviewPoint(reviewId);
        reviewRepository.deleteByReviewId(reviewId);
    }

    private void saveReviewPoint(Review r, Optional<Review> data){

        //리뷰내용 존재할경우
        if(!r.getContent().isBlank()){
            if(!data.isPresent() || data.get().getContent().isBlank()) {
                ReviewPoint point = new ReviewPoint(r.getReviewId(),"CONTENT",1);
                reviewPointRepository.save(point);
            }
        }else{
            if(data.isPresent() && !data.get().getContent().isBlank()){
                ReviewPoint point = new ReviewPoint(r.getReviewId(),"CONTENT",-1);
                reviewPointRepository.save(point);
            }
        }

        if(r.getAttachedPhotoIds() != null && r.getAttachedPhotoIds().length > 0){
            if(!data.isPresent()) {
                ReviewPoint point = new ReviewPoint(r.getReviewId(),"FILE",1);
                reviewPointRepository.save(point);
            }
        }else{
            if(data.isPresent() && data.get().getFileList() != null && data.get().getFileList().size() > 0){
                ReviewPoint point = new ReviewPoint(r.getReviewId(),"FILE",-1);
                reviewPointRepository.save(point);
            }
        }

        Integer cnt = reviewRepository.getPlaceCnt(r.getPlaceId());
        if(cnt == 0 ){
            ReviewPoint point = new ReviewPoint(r.getReviewId(),"PLACE",1);
            reviewPointRepository.save(point);
        }
    }

    private void deleteReviewPoint(String reviewId){
        Review data = reviewRepository.findByReviewId(reviewId).get();
        if(!data.getContent().isBlank()){
            ReviewPoint point = new ReviewPoint(data.getReviewId(),"CONTENT",-1);
            reviewPointRepository.save(point);
        }

        if(data.getFileList() != null && !data.getFileList().isEmpty()){
            ReviewPoint point = new ReviewPoint(data.getReviewId(),"FILE",-1);
            reviewPointRepository.save(point);
        }

        Integer place_point = reviewPointRepository.getPlacePoint(data.getReviewId());
        if(place_point!= null && place_point > 0 ){
            ReviewPoint point = new ReviewPoint(data.getReviewId(),"PLACE",-1);
            reviewPointRepository.save(point);
        }

    }

    private int calPoint(Review r){
        int point = 0;

        //리뷰 내용이 존재할경우 1점
        if(!r.getContent().isBlank()){
            point ++;
        }

        //리뷰 첨부파일이 존재할경우 1점
        if(r.getAttachedPhotoIds() != null && r.getAttachedPhotoIds().length > 0){
            point ++;
        }

        //최초 장소에서 작성 된 리뷰인지 체크
        Integer cnt = reviewRepository.getPlaceCnt(r.getPlaceId());
        if(cnt == 0){
            point ++;
        }
        return point;
    }



}