import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import models.User;
import sun.rmi.runtime.Log;
import utils.Common;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Login {
    static Scanner scan = new Scanner(System.in);
    static boolean loginSuccess = false;
    static int type = -1; // 0 : admin(관리자), 1 : employee(직원)
    static String phoneNum ="", password="", name = "", team = "";
    static int month, week;
    static Firestore db;
    static CollectionReference colRef;
    static DocumentReference docRef, month_docRef;
    static List<String> teamList = new ArrayList<>();
    static int holiday_total = 5, not_workhour_total = 0;
    static List<Integer> workhour_list = new ArrayList<>();

    public Login(Firestore db, List<String> teamList, int month, int week) {
        this.db = db;
        this.teamList = teamList;
        this.month = month;
        this.week = week;
    }

    public boolean checkValidPhoneNum(String phoneNumInput) {
        if(phoneNumInput.length()==11&&phoneNumInput.substring(0,3).equals("010")) {
            if(Common.isStringInt(phoneNumInput)) {
                return true;
            }
             return false;
        }
        System.out.println("입력 형식이 잘못되었습니다. 다시 입력해주세요.\n");
        return false;
    }
    public boolean checkValidName(String nameInput) {
        String regex = "^[ㄱ-ㅎ가-힣]*$";
        Pattern p = Pattern.compile(regex); // 패스워드 정규표현식 비교 표본
        Matcher m = p.matcher(nameInput);
        if (m.matches()&&nameInput.length()>0&&nameInput.length()<=10) { // 패스워드 형식이 맞으면 true 값 입력
            return true;
        }
        return false;
    }
    public boolean checkValidPassword(String passwordInput) {
        if(passwordInput.length()==4) { // TODO: 패스워드 형식 검사
            if(Common.isStringInt(passwordInput)) {
                return true;
            }
            return false;
        }
        System.out.println("입력 형식이 잘못되었습니다. 다시 입력해주세요.\n");
        return false;
    }
    public boolean checkIfAdmin(String phoneNumInput) {
        colRef = db.collection("Admin");
        docRef = colRef.document(phoneNumInput);
        ApiFuture<DocumentSnapshot> future = docRef.get();
        try {
            DocumentSnapshot document = future.get();
            if(document.exists()) {
                return true;
            }
        }
        catch (InterruptedException ie) { } catch (ExecutionException ee) { }
        return false;

    }
    public boolean checkIfEmployee(String phoneNumInput) {
        // 직원 DB에 해당 번호가 있는지 검사
        colRef = db.collection("Employee");
        docRef = colRef.document(phoneNumInput);
        ApiFuture<DocumentSnapshot> future = docRef.get();
        try {
            DocumentSnapshot document = future.get();
            if(document.exists()) {
               return true;
            }
        }
        catch (InterruptedException ie) { } catch (ExecutionException ee) { }
        return false;
    }
    public boolean checkIfIdPwMatch(int type, String phoneNumInput, String passwordInput) {
        if(type==0) colRef = db.collection("Admin");
        else colRef = db.collection("Employee");
        docRef = colRef.document(phoneNumInput);
        ApiFuture<DocumentSnapshot> future = docRef.get();
        try {
            DocumentSnapshot document = future.get();
            if(document.exists()) {
                if(document.get("password").equals(passwordInput)){
                    name = document.get("name").toString();
                    if(type==1) team = document.get("team").toString();
                    return true;
                }
            }
        }
        catch (InterruptedException ie) { } catch (ExecutionException ee) { }
        return false;
    }


    public void startLogin() {
        while (true) {
            String phoneNumInput;
            System.out.println("휴대폰 번호를 입력해주세요.\n(초기 화면으로 돌아가기 : z)\n");
            System.out.println("번호 입력 : ");
            phoneNumInput = scan.nextLine().trim();
            if(phoneNumInput.equals("z")||phoneNumInput.equals("Z")) {
                break;
            }
           if(checkValidPhoneNum(phoneNumInput)) { // 폰번호 형식 검사
               // 폰번호 중복 검사
               if(checkIfAdmin(phoneNumInput)) {
                   type = 0;
                   phoneNum = phoneNumInput;
                   break;
               }
               else if(checkIfEmployee(phoneNumInput)) {
                   type = 1;
                   phoneNum = phoneNumInput;
                   break;
               }
               else {
                   System.out.println("등록되지 않은 번호입니다. 다시 입력해주세요.\n");
               }
           }
        }
        if(!phoneNum.equals("")) {
            while (true) {
                String passwordInput;
                System.out.println("패스워드를 입력해주세요. (숫자 4자리)\n(초기 화면으로 돌아가기 : z)\n");
                passwordInput = scan.nextLine().trim();
                if(passwordInput.equals("z")||passwordInput.equals("Z")) {
                    break;
                }
                if(checkValidPassword(passwordInput)) { // 패스워드 형식 검사
                    if(checkIfIdPwMatch(type, phoneNum, passwordInput)) { // 기존 정보와 일치하면
                        password = passwordInput;
                        loginSuccess = true;
                        break;
                    }
                    else {
                        System.out.println("비밀번호가 일치하지 않습니다. 다시 입력해주세요.\n");
                    }
                }
            }
        }

    }
    public void startJoin() {
        while (true) {
            String phoneNumInput;
            System.out.println("휴대폰 번호를 입력해주세요.\n(초기 화면으로 돌아가기 : z)\n");
            System.out.println("번호 입력 : ");
            phoneNumInput = scan.nextLine().trim();
            if(phoneNumInput.equals("z")||phoneNumInput.equals("Z")) {
                break;
            }
            if(checkValidPhoneNum(phoneNumInput)) { // 폰번호 형식 검사
                if(!checkIfAdmin(phoneNumInput)&&!checkIfEmployee(phoneNumInput)) {
                    // 폰번호 중복 검사 -> 중복되는 번호가 없으면
                    phoneNum = phoneNumInput;
                    break;
                }
                else {
                    System.out.println("이미 등록된 번호입니다. 다시 입력해주세요.\n");
                }
            }
        }
        if(!phoneNum.equals("")) {
            while (true) {
                String passwordInput;
                System.out.println("패스워드를 입력해주세요. (숫자 4자리)\n(초기 화면으로 돌아가기 : z)\n");
                passwordInput = scan.nextLine().trim();
                if(passwordInput.equals("z")||passwordInput.equals("Z")) {
                    break;
                }
                if(checkValidPassword(passwordInput)) { // 패스워드 형식 검사
                    password = passwordInput;
                    break;


                }
            }
        }
        if(!password.equals("")) {
            while (true) {
                System.out.println("이름을 입력해주세요.\n(초기 화면으로 돌아가기 : z)\n");
                String nameInput = scan.nextLine().trim();
                if(nameInput.equals("z")||nameInput.equals("Z")) {
                    break;
                }
                if(checkValidName(nameInput)) {
                    name = nameInput;
                    break;
                }
                else {
                    System.out.println("10자 이내의 한글만 입력해주세요.\n");
                }
            }
        }
        if(!name.equals("")) {
            while (true) {
                String teamInput;
                System.out.println("팀을 선택해주세요.\n");
                // DB에 저장된 팀 목록 넘버링해서 나열
                for(int i=1;i<teamList.size()+1;i++) {
                    System.out.println(i+". "+teamList.get(i-1));
                }
                System.out.print("(초기 화면으로 돌아가기 : z)\n번호 입력 : ");
                teamInput = scan.nextLine().trim();
                if(teamInput.equals("z")||teamInput.equals("Z")) {
                    break;
                }
                if(Common.isStringInt(teamInput)) { // 번호 형식 체크
                    int selectTeamNum = Integer.parseInt(teamInput);
                    if(selectTeamNum>0&&selectTeamNum<=teamList.size()) {
                        team = teamList.get(selectTeamNum-1);
                        System.out.println("휴대폰 번호 : "+phoneNum);
                        System.out.println("이름 : "+name+"\n팀 : "+team);
                        System.out.println("정보가 등록되었습니다.\n");
//                      DB에 저장
                        addEmployee();
                        phoneNum =""; password=""; name=""; team="";
                        break;
                    }
                    else {
                        System.out.println("입력 형식이 잘못되었습니다. 다시 입력해주세요.\n");
                    }
                }
            }
        }
    }
    public static void addEmployee() {
        docRef = db.collection("Employee").document(phoneNum);

        // HashMap을 이용해 put data
        Map<String, Object> userData = new HashMap<>();
        userData.put("phone", phoneNum);
        userData.put("name", name);
        userData.put("password", password);
        userData.put("team", team);
        userData.put("holiday_total", holiday_total);
        userData.put("not_workhour_total", not_workhour_total);
        docRef.set(userData);
//        회원가입 시 Work Data 초기화
        month_docRef = docRef.collection("Work").document(month+"월");
        Map<String, Object> workData = new HashMap<>();
        workData.put("month",month);
//        workData.put("holiday_total", holiday_total);
//        workData.put("not_workhour_total", not_workhour_total);
        for(int i=1;i<week;i++) {
            workhour_list.add(0);
        }
        workData.put("workhour_list",workhour_list);
        month_docRef.set(workData);
    }

    public User startMenu() {
        User user;
        while(true)  {
            System.out.println("\n\n<근태관리 프로그램>\n");
            System.out.println("1. 로그인 2. 회원가입 3. 종료\n");
            System.out.println("번호 입력 : ");
            String num = scan.nextLine().trim();
//            clearScreen();
            switch (num) {
                case "1":
                    startLogin();
                    break;
                case "2":
                    startJoin();
                    break;
                case "3":
                    Common.exitProgram();
                    break;
                default:
                    System.out.println("입력 형식이 잘못되었습니다. 다시 입력해주세요.\n");
            }
            if(loginSuccess) {
                System.out.println("로그인이 완료되었습니다\n");
                user = new User(type, phoneNum, name, team);
                break;
            }
        }
        return user;
    }


}
