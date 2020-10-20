import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import models.Employee;
import utils.Common;

import java.util.*;
import java.util.concurrent.ExecutionException;

public class EmployeeMenu {
    static Scanner scan = new Scanner(System.in);
    Employee employee;
    static int month, week; // 이번 주 월, 주차
    static int nextMonth, nextWeek; // 다음 주 월, 주차
    static List<String> teamList = new ArrayList<>();
    static CollectionReference employeeColRef;
    static DocumentReference docRef, month_docRef, holiday_docRef;
    static Firestore db;
    static int holiday_total = 5, not_workhour_total = 0;
    static int myTeamSize;
    static List<Object> workhour_list = new ArrayList<>();
    static boolean workhour_alreadySaved = false;
    static Map<Integer, String> dayMap = new HashMap<Integer, String>() {
        {put(1,"월");put(2,"화");put(3,"수");put(4,"목");put(5,"금");}};
    static String[] dayFieldNameList = {"mon_list", "tue_list", "wed_list", "thu_list", "fri_list"};
//    static List<ArrayList>[] holidayList = new List[5];
    static ArrayList<String>[] holidayList = new ArrayList[5];
    static Boolean[] availableList = new Boolean[5];
    public EmployeeMenu(Employee employee, int month, int week, int myTeamSize, Firestore db) {
        this.employee = employee;
        this.month = month;
        this.week = week;
        this.myTeamSize = myTeamSize;
        this.db = db;
    }


    public void saveWorkHour(int workHour) {
//        근무시간 DB에 저장
        Map<String, Object> update = new HashMap<>();
        workhour_list.add(workHour);
        update.put("workhour_list", workhour_list);
        if(workHour<35) {
            int notWorkHour = 35-workHour;
           update.put("not_workhour_total", not_workhour_total+notWorkHour);
           not_workhour_total += notWorkHour;
        }
        month_docRef.set(update, SetOptions.merge());
    }

    public void saveWorkHourMenu() {
        if(workhour_alreadySaved) {
            System.out.println("이번 주 근무시간이 이미 저장되었습니다.\n");
            return;
        }
        while (true) {
            System.out.println("< 근무시간 저장 >");
            System.out.println("이번 주 근무시간을 입력해주세요.\n");
            System.out.println("\n(메인 메뉴로 돌아가기 : z)\n");
            System.out.println("근무 시간 : ");
            String num = scan.nextLine().trim();
            if(num.equals("z")||num.equals("Z")) {
                return;
            }
            if(Common.isStringInt(num)) {
                int workHour = Integer.parseInt(num);
                if(workHour>=0&&workHour<=35) {
                    System.out.println(month+"월 "+week+"주차 근무시간 : "+workHour+"시간을 저장하시겠습니까?");
                    System.out.println("1. 예 2. 아니오\n");
                    String num2 = scan.nextLine().trim();
                    switch (num2) {
                        case "1":
                            System.out.println("근무시간이 저장되었습니다.\n");
                            saveWorkHour(workHour);
                            return;
                        case "2":
                            System.out.println("취소되었습니다.\n");
                            return;
                        default:
                            System.out.println("입력 형식이 잘못되었습니다. 다시 입력해주세요.\n");
                    }
                }
                else {
                    System.out.println("0 이상 35 이하의 정수를 입력해주세요.\n");
                }
            }
        }
    }
    public void workHistoryMenu() {
        System.out.println("< "+month+"월 근무 현황 >");
//        int i=1;
        int workhour_total = 0;
//        이번 달에 만근했는지 여부 저장
        boolean allWorked = false;
//        for(Object obj : workhour_list) {
//            int hour;
//            if(obj instanceof Integer) {
//                hour = (Integer)obj;
//            }
//            else hour = ((Long)obj).intValue();
//            System.out.print(i+"주차 : "+hour+"시간 근무 ");
//            if(hour==35) System.out.print("(만근)");
//            System.out.println();
//            i++;
//            workhour_total += hour;
//        }
//        int j=i;
        for(int i=1;i<=week;i++) {
            int hour;
            if(i<workhour_list.size()) {
                Object obj = workhour_list.get(i);
                if(obj instanceof Integer) {
                    hour = (Integer)obj;
                }
                else hour = ((Long)obj).intValue();
                System.out.print(i+"주차 : "+hour+"시간 근무 ");
                if(hour==35) System.out.print("(만근)");
                System.out.println();
                workhour_total += hour;
            }
            else if(i!=week){
                System.out.println(i+"주차 : 0시간 근무");
            }
            else {
                System.out.println(i+"주차 : 아직 저장하지 않음");
            }
        }
        if(workhour_list.size()==week) {
            allWorked = workhour_total==(week-1)*35;
        }
        else {
            allWorked = workhour_total==week*35;
        }
//        for (j=i;j<week;j++) {
//            System.out.println(j+"주차 : 0시간 근무");
//        }
//        if(j!=week+1&&j!=5&&!workhour_alreadySaved) {
//            System.out.println(j+"주차 : 아직 저장하지 않음");
//            allWorked = workhour_total==(week-1)*35;
//        }
//        else {
//            allWorked = workhour_total==week*35;
//        }
        System.out.println("사용 가능한 휴가 일 수 : "+holiday_total);
        if(allWorked) {
            System.out.println("이번 달 "+(5-week)+"주 더 만근하면 월차 1일 지급!");
        }
        else {
            System.out.println("이번 달 월차 불가");
        }
        System.out.println("총 미달 근무시간 : "+not_workhour_total+"시간");
        System.out.println("(미달 근무시간이 24시간일 경우 휴가 1일이 차감됩니다.)");
        System.out.println("\n(아무키나 누르면 메인 메뉴로 돌아갑니다.)\n");
        scan.nextLine().trim();
    }

    public void applyHoliday(List day_list, String fieldName) {
        Map<String, Object> holidayUpdate = new HashMap<>();
        ApiFuture<DocumentSnapshot> future = holiday_docRef.get();
        try {
            DocumentSnapshot document = future.get();
            day_list.add(employee.getPhoneNum());
            holidayUpdate.put(fieldName, day_list);
            if(document.exists()) {
                holiday_docRef.set(holidayUpdate, SetOptions.merge());
            }
            else { // document가 존재하지 않을 경우 => 초기화해서 저장
                for(int i=0;i<5;i++) {
                    if(!dayFieldNameList[i].equals(fieldName)) {
                        holidayUpdate.put(dayFieldNameList[i], new ArrayList<>());
                    }
                }
               holiday_docRef.set(holidayUpdate);
            }
        }
        catch (InterruptedException ie) { } catch (ExecutionException ee) { }
        Map<String, Object> employeeUpdate = new HashMap<>();
        employeeUpdate.put("holiday_total", holiday_total);
        month_docRef.set(employeeUpdate, SetOptions.merge());
    }
    public void applyHolidayMenu() {
        Arrays.fill(availableList, false);
        Arrays.fill(holidayList, new ArrayList<>());
        ApiFuture<DocumentSnapshot> future = holiday_docRef.get();
        try {
            DocumentSnapshot document = future.get();
            if(document.exists()) {
                for(int i=0;i<5;i++) {
                    holidayList[i] = (ArrayList)document.get(dayFieldNameList[i]);
//                    if( holidayList[i])
                    if((holidayList[i].size()+1.0)/myTeamSize<0.3) {
                        availableList[i] = true;
                    }
                }
            }
            else {
                for(int i=0;i<5;i++) {
                    if(1/myTeamSize<0.3) availableList[i] = true;
                }
            }
        }
        catch (InterruptedException ie) { } catch (ExecutionException ee) { }
//        availableList[0] = true; //
        while (true) {
            System.out.println("< 휴가 신청하기 >");
            System.out.println("다음 주 중 휴가를 신청할 요일을 선택해주세요.");
            for(int i=1;i<6;i++) {
                System.out.print(i+". "+dayMap.get(i));
                if(availableList[i-1]) {
                    System.out.println(" (신청 가능)");
                }
                else if(holidayList[i-1].contains(employee.getPhoneNum())) {
                    System.out.println(" (신청 완료)");
                }
                else System.out.println(" (신청 불가)");
            }
            System.out.println("\n(메인 메뉴로 돌아가기 : z)\n");
            System.out.println("요일 선택 : ");
            String num = scan.nextLine().trim();
            if(num.equals("z")||num.equals("Z")) {
                return;
            }
            if(holiday_total<1) {
                System.out.println("휴가를 신청할 수 없습니다. 사용 가능한 휴가일이 부족합니다.\n");
                return;
            }
            if(Common.isStringInt(num)) {
                int day = Integer.parseInt(num);
                if(day>=1&&day<=5) {
                    if(availableList[day-1]) {
                        while (true) {
                            System.out.println(dayMap.get(day)+"요일에 휴가를 신청하시겠습니까?");
                            System.out.println("1. 예 2. 아니오");
                            String num2 = scan.nextLine().trim();
                            switch (num2) {
                                case "1":
                                    System.out.println(dayMap.get(day)+"요일에 휴가를 사용하였습니다.");
                                    holiday_total -= 1;
                                    System.out.println("남은 휴가일 : "+holiday_total);
                                    applyHoliday(holidayList[day-1], dayFieldNameList[day-1]);
                                    return;
                                case "2":
                                    System.out.println("취소되었습니다.");
                                    return;
                                default:
                                    System.out.println("입력 형식이 잘못되었습니다. 다시 입력해주세요.\n");
                            }
                        }
                    }
                    else if(holidayList[day-1].contains(employee.getPhoneNum())) {
                        System.out.println("이미 휴가를 신청한 요일입니다. 다시 선택해주세요.\n");
                    }
                    else {
                        System.out.println("신청 불가능한 요일입니다. 다시 선택해주세요.\n");
                    }
                }
                else {
                    System.out.println("입력 형식이 잘못되었습니다. 다시 입력해주세요.\n");
                }
            }

        }
    }

    public void showEmployeeMenu() {
        docRef = db.collection("Employee").document(employee.getPhoneNum());
        month_docRef = docRef.collection("Work").document(month+"월");
        //      이번 주 근무시간이 이미 저장되어 있는지 확인
        ApiFuture<DocumentSnapshot> future = month_docRef.get();
        try {
            DocumentSnapshot document = future.get();
            workhour_list = (ArrayList)document.get("workhour_list");
            holiday_total = Integer.parseInt(document.get("holiday_total").toString());
            not_workhour_total = Integer.parseInt(document.get("not_workhour_total").toString());
            if(workhour_list.size()==week) {
                workhour_alreadySaved = true;
            }
//            else {
//                for(int i=workhour_list.size();i<week-1;i++) {
////                    workhour_list.add();
//                }
//            }
        }
        catch (InterruptedException ie) { } catch (ExecutionException ee) { }
        if(week==4) {
            nextMonth=month+1;
            nextWeek=1;
        }
        else { // ex) 현재 : 2월 4주차인 경우 => 다음주 : 3월 1주차로 초기화
            nextMonth=month;
            nextWeek=week+1;
        }
        holiday_docRef = db.collection("Holiday").document(employee.getTeam()).collection(nextMonth+"월").document(nextWeek+"주차");

        while (true) {
            System.out.println("<근태관리 프로그램> - 직원 메뉴\n" +
                    "이름 : "+employee.getName()+" / "+employee.getTeam()+"\n" +
                    "오늘 날짜 : "+month+"월 "+week+"주차\n" +
                    "1. 근무시간 저장\n" +
                    "2. 근무 현황\n" +
                    "3. 휴가 신청하기\n" +
                    "4. 종료");
            System.out.println("번호 입력 : ");
            String num = scan.nextLine().trim();
//            clearScreen();
            switch (num) {
                case "1":
                    saveWorkHourMenu();
                    break;
                case "2":
                    workHistoryMenu();
                    break;
                case "3":
                    applyHolidayMenu();
                    break;
                case "4":
                    Common.exitProgram();
                    break;
                default:
                    System.out.println("입력 형식이 잘못되었습니다. 다시 입력해주세요.\n");
            }
        }
    }
}
