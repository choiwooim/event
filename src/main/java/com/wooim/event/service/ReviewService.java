package com.wooim.event.service;


import com.wooim.event.domain.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sound.sampled.ReverbType;
import javax.transaction.Transactional;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReviewService {
    @Autowired
    ReviewRepository reviewRepository;
    @Autowired
    ReviewPointRepository reviewPointRepository;

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
        saveReviewPoint(r, data.isEmpty() ? Review.builder().build() : data.get());

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

    private void saveReviewPoint(Review r, Review data){

        //리뷰내용 존재할경우
        if(!r.getContent().isBlank()){
            if(data.getContent().isBlank()) {
                ReviewPoint point = ReviewPoint.builder()
                        .reviewId(r.getReviewId())
                        .reviewType("CONTENT")
                        .reviewPoint(1)
                        .build();
                reviewPointRepository.save(point);
            }
        }else{
            if(!data.getContent().isBlank()){
                ReviewPoint point = ReviewPoint.builder()
                        .reviewId(r.getReviewId())
                        .reviewType("CONTENT")
                        .reviewPoint(-1)
                        .build();
                reviewPointRepository.save(point);
            }
        }

        if(r.getAttachedPhotoIds() != null && r.getAttachedPhotoIds().length > 0){
            if(data.getFileList().isEmpty()) {
                ReviewPoint point = ReviewPoint.builder()
                        .reviewId(r.getReviewId())
                        .reviewType("FILE")
                        .reviewPoint(1)
                        .build();
                reviewPointRepository.save(point);
            }
        }else{
            if(data.getAttachedPhotoIds() != null && data.getAttachedPhotoIds().length > 0){
                ReviewPoint point = ReviewPoint.builder()
                        .reviewId(r.getReviewId())
                        .reviewType("FILE")
                        .reviewPoint(-1)
                        .build();
                reviewPointRepository.save(point);
            }
        }

        Optional<Review> place = reviewRepository.findByPlaceId(r.getPlaceId());
        if(place.isEmpty()){
            ReviewPoint point = ReviewPoint.builder()
                    .reviewId(r.getReviewId())
                    .reviewType("PLACE")
                    .reviewPoint(1)
                    .build();
            reviewPointRepository.save(point);
        }
    }

    private void deleteReviewPoint(String reviewId){
        Review data = reviewRepository.findByReviewId(reviewId).get();
        if(!data.getContent().isBlank()){
            ReviewPoint point = ReviewPoint.builder()
                    .reviewId(data.getReviewId())
                    .reviewType("CONTENT")
                    .reviewPoint(-1)
                    .build();
            reviewPointRepository.save(point);
        }

        if(!data.getFileList().isEmpty()){
            ReviewPoint point = ReviewPoint.builder()
                    .reviewId(data.getReviewId())
                    .reviewType("FILE")
                    .reviewPoint(-1)
                    .build();
            reviewPointRepository.save(point);
        }

        Integer place_point = reviewPointRepository.getPlacePoint(data.getReviewId());
        if(place_point!= null && place_point > 0 ){
            ReviewPoint point = ReviewPoint.builder()
                    .reviewId(data.getReviewId())
                    .reviewType("PLACE")
                    .reviewPoint(-1)
                    .build();
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
        Optional<Review> data = reviewRepository.findByPlaceId(r.getPlaceId());
        if(data.isEmpty()){
            point ++;
        }
        return point;
    }



}