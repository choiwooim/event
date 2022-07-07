package com.wooim.event.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import springfox.documentation.annotations.ApiIgnore;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "REV")
@Entity(name = "REV")
public class Review {

	@Id
	@GenericGenerator(name = "reviewId", strategy = "com.wooim.event.domain.ReviewIdGenerator")
	@GeneratedValue(generator = "reviewId")
	@Column(name = "REVIEW_ID")
	@ApiModelProperty(value="리뷰ID", example = "240a0658-dc5f-4878-9381-ebb7b2667772")
	private String reviewId;

	@Column(name = "TYPE")
	@ApiModelProperty(value="타입", example = "REVIEW")
	private String type;

	@Column(name = "ACTION")
	@ApiModelProperty(value="액션", example = "ADD", notes = "ADD, MODIFY, DELETE 값이 존재할 수 있습니다.")
	private String action;

	@Column(name = "CONTENT")
	@ApiModelProperty(value="리뷰내용", example = "좋아요!")
	private String content;

	@Column(name = "USER_ID")
	@ApiModelProperty(value="사용자ID", example = "3ede0ef2-92b7-4817-a5f3-0c575361f745")
	private String userId;

	@Column(name = "PLACE_ID")
	@ApiModelProperty(value="장소ID", example = "2e4baf1c-5acb-4efb-a1af-eddada31b00f")
	private String placeId;

	@Column(name = "POINT")
	private Integer point;

	@Transient
	@JsonIgnoreProperties(ignoreUnknown = true)
	private String[] attachedPhotoIds;

	@JsonIgnore
	@Builder.Default
	@OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<ReviewFile> fileList = new ArrayList<>();

	@Transient
	@JsonIgnoreProperties(ignoreUnknown = true)
	@JsonIgnore
	private List<ReviewPoint> pointList = new ArrayList<>();


}