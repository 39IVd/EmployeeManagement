# Employee Management Project

# 2020 전공기초프로젝트2

## 프로젝트명 : 직원 근태관리 프로그램

- Language : Java
- IDE : Intellij
- Database : Firestore

## 기본 설정 방법

- Firebase에 연결하기
    1. 프로젝트 Root 하위의 em_firebase.json 파일을 자신의 로컬 디렉토리에 저장
    2. EmployeeManagement/src/main/java/database/FirestoreData.java 파일 열기
    3. FirestoreData 클래스 안의 initFirebase() 메소드에서 FileInputStream의 경로를 위에서 저장한 em_firebase.json 파일의 절대 경로로 변경

        ```java
        // 예시
        FileInputStream serviceAccount =
           new FileInputStream("/Users/paige/swproject/em_firebase.json");
        ```

    4. Terminal 혹은 Console에서 프로젝트 Root 경로로 디렉토리 이동 후 아래의 명령어 실행

        ```java
        gradle build
        ```

    5. build 가 완료되면 Java 프로젝트 실행
    
## 관리자 계정으로 프로그램 실행하기
1. 프로그램 실행
2. 로그인 선택
3. 휴대폰 번호 입력 : 01011111111
4. 패스워드 입력 : 1111

## 직원 계정으로 프로그램 테스트 
1. 프로그램 실행
2. 로그인 선택
3. 휴대폰 번호 입력 : 01022222222
4. 패스워드 입력 : 2222

