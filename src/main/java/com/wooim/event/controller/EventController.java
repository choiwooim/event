package com.wooim.event.controller;

import com.wooim.event.domain.Review;
import com.wooim.event.service.ReviewService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController("/events")
public class EventController {

    @Autowired
    ReviewService reviewService;

    @GetMapping("/{id}")
    @ApiOperation(value = "리뷰이벤트 조회")
    public ResponseEntity<Map> getEvent(
            @PathVariable("id") String reviewId) {
        Map<String, Object> retMap = new HashMap<>();
        try{
            retMap.put("ret",1);
            retMap.put("info", reviewService.getReview(reviewId));
        }catch(Exception e){
            retMap.put("ret",-9999);
            retMap.put("msg",e.getMessage());
        }

        return ResponseEntity.ok(retMap);
    }

    @GetMapping("/point")
    @ApiOperation(value = "포인트 조회")
    public ResponseEntity<Map> getPointList(){
        Map<String, Object> retMap = new HashMap<>();
        try{
            retMap.put("ret",1);
            retMap.put("info", reviewService.getReviewPointList(null));
        }catch(Exception e){
            retMap.put("ret",-9999);
            retMap.put("msg",e.getMessage());
        }

        return ResponseEntity.ok(retMap);
    }

    @PostMapping("")
    @ApiOperation(value = "리뷰저장" , notes = "ACTION값에 따라 저장, 수정, 삭제를 진행합니다.")
    public ResponseEntity<Map> saveEvent(
            @RequestBody Review review) {
        Map<String, Object> retMap = new HashMap<>();
        try{
            if(review.getType() == null || review.getType().equals("")){
                throw new RuntimeException("이벤트 타입이 존재하지 않습니다.");
            }else if(!review.getType().equals("REVIEW")){
                throw new RuntimeException("리뷰타입이 아닙니다.");
            }

            String action = review.getAction();

            if(action.equals("ADD") || action.equals("MODIFY"))
                reviewService.saveReview(review);
            else if(action.equals("DELETE"))
                reviewService.deleteReview(review.getReviewId());
            else
                throw new RuntimeException("액션은 ADD, MODIFY, DELETE만 존재합니다.");

            retMap.put("ret",1);
            retMap.put("info",review.toString());
        }catch(Exception e){
            retMap.put("ret",-9999);
            retMap.put("msg",e.getMessage());
            System.out.println(e.getMessage());
        }

        return ResponseEntity.ok(retMap);
    }

    @PutMapping("")
    @ApiOperation(value = "리뷰이벤트 수정")
    public ResponseEntity<Map> modifyEvent(
            @RequestBody Review review) {
        Map<String, Object> retMap = new HashMap<>();
        try{
            if(review.getType() == null || review.getType().equals("")){
                throw new RuntimeException("이벤트 타입이 존재하지 않습니다.");
            }else if(!review.getType().equals("REVIEW")){
                throw new RuntimeException("리뷰타입이 아닙니다.");
            }

            reviewService.saveReview(review);

            retMap.put("ret",1);
            retMap.put("info",review.toString());
        }catch(Exception e){
            retMap.put("ret",-9999);
            retMap.put("msg",e.getMessage());
        }

        return ResponseEntity.ok(retMap);
    }

    @DeleteMapping("/{id}")
    @ApiOperation(value = "리뷰이벤트 삭제")
    public ResponseEntity<Map> deleteEvent(
            @PathVariable("id") String reviewId) {
        Map<String, Object> retMap = new HashMap<>();
        try{
            reviewService.deleteReview(reviewId);

            retMap.put("ret",1);
        }catch(Exception e){
            retMap.put("ret",-9999);
            retMap.put("msg",e.getMessage());
        }

        return ResponseEntity.ok(retMap);
    }
}