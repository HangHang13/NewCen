



# :star2:신입사원 소통 창구 서비스:star2:





## 목차

[1. 서비스 소개](#ⅰ-서비스-소개)

[2. 기술 스택](#ⅱ-기술스택)

[3. 프로젝트 진행](#ⅲ-프로젝트-진행)





# **Ⅰ**. 서비스 소개

## :dizzy:서비스 설명

### 개요

- 한줄 소개: 신입 직원들이 회사에 **궁금한 점**을 물어보거나, 사내 행사 **일정** 등을 확인할 수 있는 **소통** 창구 서비스

- 서비스 명 : **NewCen**

### 이용 타겟

- 많은 사람에게 일일히 정보를 전달해야 하는 인사팀원들
- 일정이나 행사 등을 알고 싶어하는 신입사원



### :calendar: 개발 기간

-------

**2023.02.06~2023.02.16**



### :dizzy: NewCen의 모토

`신입사원 소통은 손쉽고 빠르게!`





### :dizzy: 기획배경

신입사원으로 소통이 어렵다는 생각이 들었습니다. 인사팀 또한 개별적으로 연락을 하는 것이 불편할 것 같다는 생각이 들어 기획하게 되었습니다.



### 배경

- 인사팀은 교육이나 계약에 필요한 피일을 메일로 보냅니다.
- 또한 필요한 정보들을 문자로 보냅니다.
- 공지사항 페이지를 활용하여 파일을 올려 신입사원으로 하여금 다운을 받게 할 수 있습니다.
- 신입사원들은 댓글을 달 수 있고, 댓글에 파일을 첨부할 수 있습니다.
- 인사팀원은 메일을 일일히 확인하지 않고도 한 페이지에서 사원들이 댓글로 첨부한 파일을 다운받을 수 있습니다.



### 기대효과

- 소통의 창구
- 자주 묻는 질문을 통해 인사팀의 업무량 감소 및 사원들의 궁금증 해소
- 효율적인 파일 다운 페이지 제공
- 메세지를 보내서 사원들끼리 소통





## :dizzy:서비스 화면



















## :dizzy:주요 기능

- 서비스 설명 : 사원 커뮤니티 서비스

- 주요 기능 :

  - 공지사항 페이지 

    - 인사팀은 공지사항을 작성할 수 있고, 파일을 첨부할 수 있음.
    - 인사팀은 댓글을 허용하게 글을 생성할 수 있음.
    - 사원들은 댓글을 달 수 있고, 댓글에 파일을 첨부하여 제출할 수 있음.

  - 문의사항 페이지

    - 사원들은 문의사항을 작성할 수 있음.
    - 파일을 첨부할 수 있음.

  - FAQ 페이지

    - 자주 묻는 질문등을 추려 인사팀은 글을 등록할 수 있음.

  - 메세지 작성

    - 사원들끼리 또는 인사팀간에 메세지를 주고 받을 수 있음.

    







# **Ⅱ. 기술스택**

## :computer: 개발환경

- s3를 활용하여 파일 저장 및 react 배포
- SpringBoot, JPA, QueryDsl을 활용하여 서버 api 개발
- RDS를 활용하여 데이터 베이스 저장



### ![제목 없는 다이어그램](https://user-images.githubusercontent.com/82326116/219445525-5ad878c2-ba57-4a70-b01c-744d0bff8c0b.jpg)



### frontEnd

- react: 18.2.0

### backend

- springframework.boot 2.7.8
- springfox-swagger-ui 2.9.2

- queryDslVersion = 5.0.0
- spring-cloud-starter-aws:2.2.6

### infra

- Amazon Linux EC2
- Amazon RDS
- Amazon S3



### 협업툴

- Git
- Slack
- Notion
- Figma
- Postman
- Swagger



# **Ⅲ. 프로젝트 진행**

- 기능별로 개발을 진행했습니다.
- 필요시 백엔드, 프론트 엔드 역할을 나누어 개발했습니다.



### FIGMA

![image-피그마](https://user-images.githubusercontent.com/82326116/219445597-ba6be0a3-751c-4f36-9c05-7219d6fdcc7a.png)





### ERD

![image-20230217023605992](https://user-images.githubusercontent.com/82326116/219445451-bd07bc4e-4a37-48be-bce2-00e60b3f9842.png)





## 깃 전략

### 깃 플로우 전략

- 개발의 내용을 명확하게 하기 위해서 깃플로우 전략을 사용했습니다.
- 규모가 크지 않다보니 release 브랜치는 사용하지 않고, 최종적으로 master 브랜치에서 배포를 했습니다.
- 팀장 주도하에 merge request를 했습니다.



![0](https://user-images.githubusercontent.com/82326116/211334754-02dfd431-ae00-45ca-982c-70ddde5efe27.png)

- master : 제품으로 출시될 수 있는 브랜치
- develop : 다음 출시 버전을 개발하는 브랜치
- feat : 기능을 개발하는 브랜치
- fix : 출시 버전에서 발생한 버그를 수정 하는 브랜치



 



### :family: 개발 팀원 소개

| <img src="https://user-images.githubusercontent.com/82326116/219449128-ba9b7a7d-8aaa-4883-abe2-e15737474a10.png" alt="KakaoTalk_20230217_025240499" style="zoom: 25%;" width=400 /> | <img src="https://user-images.githubusercontent.com/82326116/219447782-ee46240d-b9d1-4984-8b7e-f38fcf9d3538.jpg" alt="KakaoTalk_20230217_025240499" style="zoom: 25%;" width=400 /> | <img src="https://user-images.githubusercontent.com/82326116/219448503-181eeaca-7611-46b0-b472-6a81911cc722.png" alt="KakaoTalk_20230217_025240499" style="zoom: 25%;" width=400 /> | <img src="https://user-images.githubusercontent.com/82326116/219448121-5cccb578-260e-4aa5-9c93-d9dc487db97e.png" alt="KakaoTalk_20230217_025240499" style="zoom: 25%;" width=400 /> |
| ------------------------------------------------------------ | ------------------------------------------------------------ | ------------------------------------------------------------ | ------------------------------------------------------------ |
| 이진행<br />(팀장, Back-end)                                 | 이진혁<br />(Back-end & Front-end)                           | 박승연<br />(Back-end & Front-end)                           | 허진영<br />(Back-end & Front-end)                           |

| 이름   | 역할                 | 개발 내용                                                    |
| ------ | -------------------- | ------------------------------------------------------------ |
| 이진행 | 팀장, Back-end       | - JPA 기반 Entity 설계<br />- ERD 설계<br />- 스프링 S3 연동하여 파일 저장<br />- 검색 기능(제목, 제목+내용, 글쓴이) 개발<br />- 페이지 제네이션<br />- QueryDsl을 활용하여 동적쿼리작성 |
| 이진혁 | Back-end & Front-end |                                                              |
| 박승연 | Back-end & Front-end |                                                              |
| 허진영 | Back-end & Front-end |                                                              |



