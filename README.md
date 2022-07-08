
# Spring Rest Api with Swagger3.0

## Description
- Spring Boot + mysql + jpa 를 이용하여 리뷰 api를 개발하였습니다.
- Swagger를 통하여 api 정보 관리 및 테스트가 가능합니다.
  (http://localhost:8888/swaagger-ui/index.html)
* 리뷰 테이블은 미리 생성되어 있어야 합니다. 
create table REV (
       REVIEW_ID varchar(255) not null ,
        ACTION varchar(255),
        CONTENT varchar(255),
        PLACE_ID varchar(255),
        TYPE varchar(255),
        USER_ID varchar(255),
        primary key (REVIEW_ID)
    ) engine=InnoDB;
## Function
1.리뷰등록("POST" , /events/)
- action에 따라 등록,수정,삭제 가능하며, 수정 및 삭제 api도 별도로 추가하였습니다.
- type은 "REVIEW"만 허용합니다.
- action은 "ADD", "MODIFY", "DELETE"만 허용합니다.
- 리뷰내용, 첨부파일 여부, 장소 첫방문 여부에 따라 점수가 부여됩니다. 

2.리뷰수정("PUT", /events/)
- 리뷰를 수정하며 action, type을 체크하지 않습니다. 
- 리뷰 수정 시 기존에 입력한 내용 및 파일을 제거하거나 추가함에 따라 점수가 수정 부여됩니다. 
- 리뷰 점수가 변경됨에 따라 포인트 이력이 생성됩니다. 
예) 
1)리뷰 내용 등록, 첨부파일 등록 => 리뷰 1점 , 첨부파일 1점 
2)리뷰 내용 삭제 => 리뷰 -1점
3)첨부파일 삭제 -> 첨부파일 -1점

3.리뷰삭제("DELETE", /events/)
- 리뷰 및 첨부파일이 삭제되며, 리뷰포인트도 초기화 됩니다.

4.리뷰조회("GET", /events/{id})
- 리뷰아이디를 통해 리뷰를 조회합니다. 

5.포인트조회("GET", /events/point)
- 사용자 아이디 기준으로 작성한 리뷰의 점수의 합계를 리턴합니다.
