<img alt="브로셔 이미지" src="https://github.com/MeetUpEat/Zipbab/assets/48354989/941aed6e-a9d3-476c-95f9-f7d0c64fbaf6"/>

## 프로젝트 개요

#### `Zipbab`은 공유 주방등의 장소에서 사용자들이 같이 집밥을 만들어 먹을 수 있는 서비스를 제공합니다.

##### 💡 `Zipbab`은 다음과 같은 분들을 위해서 구현되었습니다.

>👬 배달 대신 다른 사람들과 음식을 만들고 공유하면서 모임을 즐기고 싶은 분 <br/>

>☕ 요리가 미숙해서 함께 요리하며 배우고 싶어하는 분 <br/>

>👯 친구나 가족과 함께 요리를 즐기고 싶은 분 <br/>

<br>

## 팀 소개 🧑‍🤝‍🧑

| 김태영 | 이종성 | 박준식 | 최윤찬 |
|:---------:|:---------:|:----------:|:---------:|
| [@rlaxodud214](https://github.com/rlaxodud214) | [@kory0115](https://github.com/kory0115) | [@DoTheBestMayB](https://github.com/DoTheBestMayB) | [@yoonchanchoi](https://github.com/yoonchanchoi) |

<br>
  

### 프로젝트 일정


| 기간 | 내용 |
| ----------- | -------------- |
| 24/05/02 ~ 24/05/23 | 아이디어 회의 |
| 24/05/24(금) ~ 24/05/26 | 팀빌딩 및 주제 선정 |
| 24/05/27 ~ 24/05/30 | 와이어 프레임 및 Flow Chart 구상 |
| 24/05/31(금) | 프로젝트 Init 코드 작성 <br> (App Architecture) |
| 24/06/01 ~ 24/06/13 | 집중 개발 스프린트 |
| 24/06/14(금) | App 출시 준비 : <br> Reject 방지를 위한 검토 및 테스트 |
| 24/06/15 ~ 24/06/18 | [v1.0.1] 전체로직 테스트 및 이슈 수정 <br> (권한 처리, 메모리 누수) |
| 24/06/18(화) ~ 24/06/23 | [v1.0.2] 1차 배포 및 사용자 피드백 반영 <br> (UX, 알림, 다크모드 등) |
| 24/06/24(월) ~ 24/06/25 | [v1.0.3] 디자인 피드백 반영 UI/UX) |
| 24/06/26(수) ~ 24/06/28 | [v1.0.4] 피드백 관련 UX 및 오류 개선 |
| 24/07/01(월) ~ | 발표 준비, 사용자 피드백 반영 및 오류 개선 |



<br>

### 기술 스택
| 분류 | 이름 |
| --- | --- |
| Architecture | <img src="https://img.shields.io/badge/MVVM-FDECC8"> <img src="https://img.shields.io/badge/Android App Architecture-34A853">|
| Jetpack | <img src="https://img.shields.io/badge/Paging3-EE0000"> <img src="https://img.shields.io/badge/WorkManager-FF7900" > |
| DI | <img src="https://img.shields.io/badge/Hilt-22D172"> |
| 비동기 처리 | <img src="https://img.shields.io/badge/Coroutine-7F52FF"> <img src="https://img.shields.io/badge/Flow-7F52FF"> <img src="https://img.shields.io/badge/Channel-7F52FF">  |
| Firebase | <img src="https://img.shields.io/badge/Storage-DD2C00"> <img src="https://img.shields.io/badge/Firestore-DD2C00"> <img src="https://img.shields.io/badge/FCM-DD2C00"> |
| 지도 | <img src="https://img.shields.io/badge/네이버지도-03C75A">    |
| UI Frameworks | <img src="http://img.shields.io/badge/Navigation Component-3DDC84"> <img src="https://img.shields.io/badge/XML-89632a"> <img src="http://img.shields.io/badge/Compose-4285F4&logo=jetpackcompose"> 
  
<br>

### 앱 디자인 설계

<a href="https://www.figma.com/design/3BK4bSRabyePguTgqxgyQ7/Zipbab?node-id=258-3833&t=PbTV9wHc0s0tY6ba-1"><strong>Figma Wire Flow »</strong></a>


<p align="center">
    <img width="800" alt="image" src="https://github.com/MeetUpEat/Zipbab/assets/52482206/ba5a0c7b-8348-4388-b0f6-1f026a279f78">
</p> </br>
   

### 역할 분담

| 이름 | 역할 |
| :---: | :------- |
| 김태영 | 지도 페이지, 내 모임 페이지, FireStorage & FireStore|
| 이종성 | 프로필 페이지, 게시글 페이지, 내정보 관리 페이지, FireStorage & FireStore  |
| 박준식 | 알림 페이지, 모임신청 페이지, 로그인 & 회원가입 |
| 최윤찬 | 메인 페이지, 카테고리 페이지, 모임 참가 및 관리 페이지 |

<br/>


## 전체 기능

### 홈 화면
| 다가오는 모임 | 메뉴별 모임 탐색 | 금액별 모임 탐색 | 키워드로 모임 탐색 |
| --- | --- | --- | --- |
| <img width="200" src="https://github.com/MeetUpEat/Zipbab/assets/48354989/ffec8357-6151-485a-bdca-39ed3a2fe059"> | <img width="200" src="https://github.com/MeetUpEat/Zipbab/assets/48354989/398a5359-0ac5-49c6-a4c2-1b481a5bef18"> | <img width="200" src="https://github.com/MeetUpEat/Zipbab/assets/48354989/70c300c1-661d-4bf2-96e6-1628aa2913b7"> | <img width="200" src="https://github.com/MeetUpEat/Zipbab/assets/48354989/edb542a7-c3bc-48bb-9cf7-78dd8fefe3ec"> |

### 지도 화면
| 사용자 위치 기반 모임 탐색 및 거리 필터링 | 위치 권한 처리 | 가까운 모임 정보 보기 및 참가 | 모임 거리순 정렬 |
| --- | --- | --- | --- |
| <video src="https://github.com/boostcampwm-2022/android10-PlzStop/assets/48354989/90d27101-5f91-4854-be62-1e1a3b7636c6"> | <img width="200" src="https://github.com/boostcampwm-2022/android10-PlzStop/assets/48354989/9e3d912b-310a-4bf8-af55-e22cc774782d"> | <video src="https://github.com/boostcampwm-2022/android10-PlzStop/assets/48354989/d22cddb3-976c-49e6-b7ce-9f58a0f67a3f"> | <video src="https://github.com/boostcampwm-2022/android10-PlzStop/assets/48354989/38119354-b05b-45ed-bf3c-4d27f85912a2"> |

### 모임 모집글 생성 화면
| 모임 생성 |
| --- |
| <video src="https://github.com/boostcampwm-2022/android10-PlzStop/assets/48354989/da3a68b5-23b4-4315-b2b1-f882593bb6ba"> |
    
### 알림 화면
| 알림 처리 Flow |
| --- |
| <img width="200" src="https://github.com/MeetUpEat/Zipbab/assets/48354989/1df4e979-b874-429a-b888-cc6f41da0eb9"> |
    
### 로그인 & 회원가입
| 로그인 | 회원가입 |
| --- | --- |
| <img width="200" src="https://github.com/MeetUpEat/Zipbab/assets/48354989/19b9521e-61ed-4bb1-b6cb-1ce5a953e634"> | <video src="https://github.com/MeetUpEat/Zipbab/assets/48354989/e11617d7-6540-438f-a7f8-1461f8550cdd"> |


### 설정 화면
| 사용자의 로그인 상태에 따른 로그인, 로그아웃 |
| --- |
| <video src="https://github.com/MeetUpEat/Zipbab/assets/48354989/d1e86f4f-f7fd-40b0-9720-ca5f95a83f72"> |
    
### 프로필 화면
| 프로필 수정 | 게시글 업로드 |
| --- | --- |
| <img width="200" src="https://github.com/MeetUpEat/Zipbab/assets/48354989/f78285e2-61e2-48ab-b935-70863566f7fb"> | <video src="https://github.com/MeetUpEat/Zipbab/assets/48354989/ccf5dfb5-55fd-4e04-b05f-e86feb0a2fe4"> |
    
    
[내 모임 보기] |
| --- |
| <img width="200" src="https://github.com/MeetUpEat/Zipbab/assets/48354989/b627c9f1-d464-48d4-a20f-a9c251ee307d"> |
 
<br/>
