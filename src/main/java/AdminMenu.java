import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import models.Admin;
import utils.Common;

import java.util.*;
import java.util.concurrent.ExecutionException;

public class AdminMenu {
    static Scanner scan = new Scanner(System.in);
    Admin admin;
    static int month, week;
    static List<String> teamList = new ArrayList<>();
    static CollectionReference colRef;
    static DocumentReference docRef;
    static Firestore db;
    static String selectedTeam="";
    public AdminMenu(Admin admin, int month, int week, List teamList, Firestore db) {
        this.admin = admin;
        this.month = month;
        this.week = week;
        this.teamList = teamList;
        this.db = db;
    }
    public Map getEmployeeListInTeam(String team) {
        Map<String, String> nameToPhone = new HashMap<>();
        colRef = db.collection("Employee");
        Query query = colRef.whereEqualTo("team", team);
        ApiFuture<QuerySnapshot> querySnapshot = query.get();
        try {
            for (DocumentSnapshot document : querySnapshot.get().getDocuments()) {
                nameToPhone.put(document.get("name").toString(), document.getId());
            }
        }
        catch (InterruptedException ie) { } catch (ExecutionException ee) { }
        return nameToPhone;
    }
    public void deleteEmployee(String phone) {
        db.collection("Employee").document(phone).delete();

    }
    public void selectEmployeeMenu(String selectedTeam) {
        Map<String, String> nameToPhone =  getEmployeeListInTeam(selectedTeam);
        if(nameToPhone.isEmpty()) {
            System.out.println("해당 팀에 직원이 없습니다.\n");
            return;
        }
        while (true) {
            System.out.println("\n해고할 직원을 선택해주세요.\n");
            List<String> nameList = new ArrayList<>();
            nameList.addAll(nameToPhone.keySet());
            for(int i=1;i<nameList.size()+1;i++) {
                System.out.println(i+". "+nameList.get(i-1));
            }
            System.out.println("(메인 메뉴로 돌아가기 : z)");
            String num = scan.nextLine().trim();
            if(num.equals("z")||num.equals("Z")) {
                selectedTeam = "";
                return;
            }
            if(Common.isStringInt(num)) {
                int selectedNum = Integer.parseInt(num);
                if(selectedNum>0&&selectedNum<=nameList.size()) {
                    while (true) {
                        String selectedName = nameList.get(selectedNum-1);
                        System.out.println("직원 "+selectedName+"을 해고하시겠습니까?");
                        System.out.println("1. 예 2. 아니오");
                        String num2 = scan.nextLine().trim();
                        switch (num2) {
                            case "1":
                                String phone = (nameToPhone.get(selectedName));
                                deleteEmployee(phone);
                                System.out.println(selectedTeam+"의 "+selectedName+" 해고 완료.\n");
                                return;
                            case "2":
                                selectedTeam = "";
                                System.out.println("취소되었습니다.\n");
                                return;
                            default:
                                System.out.println("입력 형식이 잘못되었습니다. 다시 입력해주세요.\n");

                        }
                    }
                }
                else {
                    System.out.println("입력 형식이 잘못되었습니다. 다시 입력해주세요.\n");
                }
            }
        }
    }
    public void selectTeamMenu() {
        while (true) {
            System.out.println("해고할 직원의 팀을 선택해주세요.\n");
            for(int i=1;i<teamList.size()+1;i++) {
                System.out.println(i+". "+teamList.get(i-1));
            }
            System.out.println("(메인 메뉴로 돌아가기 : z)");
            System.out.println("\n번호 입력 : ");
            String teamInput = scan.nextLine().trim();
            if(teamInput.equals("z")||teamInput.equals("Z")) {
                selectedTeam="";
                break;
            }
            if(Common.isStringInt(teamInput)) {
                int selectTeamNum = Integer.parseInt(teamInput);
                if(selectTeamNum>0&&selectTeamNum<=teamList.size()) {
                    selectedTeam = teamList.get(selectTeamNum-1);
                   break;
                }
                else {
                    System.out.println("\n입력 형식이 잘못되었습니다. 다시 입력해주세요.\n");
                }
            }

        }
    }
    public void showEmployeeInTeam(String selectedTeam) {
        colRef = db.collection("Employee");
        Query query = colRef.whereEqualTo("team", selectedTeam);
        ApiFuture<QuerySnapshot> querySnapshot = query.get();
        try {
            if(querySnapshot.get().getDocuments().isEmpty()) {
                System.out.println("해당 팀에 직원이 없습니다.\n");
                return;
            }
            System.out.println("\n\n< "+selectedTeam+"의 직원 근무 현황 >");
            System.out.println("이름\t\t\t전화번호\t\t\t사용가능 휴가일\t\t미달 근무시간");
            for (DocumentSnapshot document : querySnapshot.get().getDocuments()) {
                String name, phone, holiday_total, not_workhour_total;
                name = document.get("name").toString();
                phone = document.get("phone").toString();
                holiday_total = document.get("holiday_total").toString();
                not_workhour_total = document.get("not_workhour_total").toString();
                System.out.println(name+"\t\t"+phone+"\t\t"+holiday_total+"\t\t\t\t"+not_workhour_total);
            }
            System.out.println("\n(아무키나 누르면 메인 메뉴로 돌아갑니다.)\n");
            scan.nextLine().trim();

        }
        catch (InterruptedException ie) { } catch (ExecutionException ee) { }

    }
    public void employeeInfoMenu() {
        while (true) {
            System.out.println("팀을 선택해주세요.\n");
            for(int i=1;i<teamList.size()+1;i++) {
                System.out.println(i+". "+teamList.get(i-1));
            }
            System.out.println("(메인 메뉴로 돌아가기 : z)");
            System.out.println("\n번호 입력 : ");
            String teamInput = scan.nextLine().trim();
            if(teamInput.equals("z")||teamInput.equals("Z")) {
                selectedTeam="";
                break;
            }
            if(Common.isStringInt(teamInput)) {
                int selectTeamNum = Integer.parseInt(teamInput);
                if(selectTeamNum>0&&selectTeamNum<=teamList.size()) {
                    selectedTeam = teamList.get(selectTeamNum-1);
                    showEmployeeInTeam(selectedTeam);
                    return;
                }
                else {
                    System.out.println("\n입력 형식이 잘못되었습니다. 다시 입력해주세요.\n");
                }
            }

        }
    }
    public void showAdminMenu() {
        while (true) {
            System.out.println("<근태관리 프로그램> - 관리자 메뉴\n" +
                    "이름 : "+admin.getName()+ "\n" +
                    "오늘 날짜 : "+month+"월 "+week+"주차\n" +
                    "1. 직원 근무 현황\n" +
                    "2. 직원 해고\n" +
                    "3. 종료");
            System.out.println("번호 입력 : ");
            String num = scan.nextLine().trim();
//            clearScreen();
            switch (num) {
                case "1":
                    employeeInfoMenu();
                    break;
                case "2":
                    selectTeamMenu();
                    if(!selectedTeam.equals(""))
                        selectEmployeeMenu(selectedTeam);
                    break;
                case "3":
                    Common.exitProgram();
                    break;
                default:
                    System.out.println("입력 형식이 잘못되었습니다. 다시 입력해주세요.\n");
            }
        }
    }
}
