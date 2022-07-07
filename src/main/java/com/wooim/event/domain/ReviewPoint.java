package com.wooim.event.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "REV_POINT")
@Table(schema = "REV_POINT")
public class ReviewPoint {

    @Id
    @Column(name = "REVIEW_POINT_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY) //MySQL의 AUTO_INCREMENT를 사용
    private Integer reviewPointId;

    @Column(name = "REVIEW_ID")
    private String reviewId;

    @Column(name = "REVIEW_POINT")
    private Integer reviewPoint;

    @Column(name = "REVIEW_TYPE")
    private String reviewType;

    @CreatedDate
    @CreationTimestamp
    @Column(name="REG_DTT", nullable = false)
    private LocalDateTime regDtt;

    @LastModifiedDate
    @CreationTimestamp
    @Column(name="MOD_DTT", nullable = false)
    private LocalDateTime modDtt;

    @Builder
    public ReviewPoint(String reviewId, String reviewType, Integer reviewPoint){
        this.reviewId = reviewId;
        this.reviewPoint = reviewPoint;
        this.reviewType = reviewType;
    }
}