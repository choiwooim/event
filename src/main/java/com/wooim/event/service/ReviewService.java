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

    /** 리뷰 단건 조회 */
    public Review getReview(String reviewId){
        Optional<Review> review = reviewRepository.findByReviewId(reviewId);

        return review.isEmpty() ? Review.builder().build() : review.get();
    }

    /** 리뷰 포인트 사용자ID별 조회 */
    public List<Map<String, Object>> getReviewPointList (String reviewId){
        List<Map<String, Object>> list = reviewPointRepository.getUserPoint();

        return list;
    }

    /** 리뷰 및 리뷰파일 등록 */
    @Transactional
    public void saveReview(Review r) throws Exception
    {
        //기존 기뷰 조회
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

    /** 리뷰 및 리뷰포인트 삭제 */
    @Transactional
    public void deleteReview(String reviewId){
        deleteReviewPoint(reviewId);
        reviewRepository.deleteByReviewId(reviewId);
    }

    /** 리뷰 포인트 저장 */
    private void saveReviewPoint(Review r, Optional<Review> data){

        //리뷰내용 존재할경우
        if(!r.getContent().isBlank()){
            //이전에 리뷰내용 등록한 적 없는경우
            if(!data.isPresent() || data.get().getContent().isBlank()) {
                ReviewPoint point = new ReviewPoint(r.getReviewId(),"CONTENT",1);
                reviewPointRepository.save(point);
            }
        }else{
            //리뷰를 등록후 빈값으로 수정한경우 포인트 회수
            if(data.isPresent() && !data.get().getContent().isBlank()){
                ReviewPoint point = new ReviewPoint(r.getReviewId(),"CONTENT",-1);
                reviewPointRepository.save(point);
            }
        }

        //첨부파일 존재할경우
        if(r.getAttachedPhotoIds() != null && r.getAttachedPhotoIds().length > 0){
            if(!data.isPresent()) { //이전에 첨부파일을 등록하지 않은경우
                ReviewPoint point = new ReviewPoint(r.getReviewId(),"FILE",1);
                reviewPointRepository.save(point);
            }
        }else{
            //사진 첨부 후 제거하여 수정한경우 포인트 회수
            if(data.isPresent() && data.get().getFileList() != null && data.get().getFileList().size() > 0){
                ReviewPoint point = new ReviewPoint(r.getReviewId(),"FILE",-1);
                reviewPointRepository.save(point);
            }
        }

        //첫 방문 리뷰인지 체크하여 첫 방문 일경우 포인트 지급
        Integer cnt = reviewRepository.getPlaceCnt(r.getPlaceId());
        if(cnt == 0 ){
            ReviewPoint point = new ReviewPoint(r.getReviewId(),"PLACE",1);
            reviewPointRepository.save(point);
        }
    }

    /** 리뷰 포인트 제거 */
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

    /** 리뷰 포인트 합계 계산 */
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