import com.google.cloud.firestore.*;
import database.FirestoreData;
import models.Admin;
import models.Employee;
import models.User;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;


public class Main {
    static Scanner scan = new Scanner(System.in);
//    static String name="aaa", phoneNum ="01011111111", team="개발팀";
    static String name="", phoneNum ="", team = "";
    static User user;
    static Admin admin;
    static Employee employee;
    static int type = -1;
//    static String projectId = "employeemanagement-b0424";
    static Firestore db;
    static List<String> teamList = new ArrayList<>();
    static int myTeamSize;
    static int month = 10, week = 3;


    public static void main(String[] args) throws IOException, InterruptedException, ExecutionException  {
        db = FirestoreData.initFirebase();
        teamList = FirestoreData.getTeamName();
        Login login = new Login(db, teamList, month, week);
        user = login.startMenu();
//        user = new User(0, "01011111111", "관리자1", "");
//        user = new User(1, "01044444444", "김사원", "개발팀");
        type = user.getType();

        if(type==0) { // admin
//            TODO: DB에서 admin 정보 불러오기
            admin = new Admin(user.getPhoneNum(), user.getName());
            AdminMenu menu = new AdminMenu(admin, month, week, teamList, db);
            menu.showAdminMenu();
        }
        else if(type==1) { // employee
//            TODO: DB에서 employee 정보 불러오기
            myTeamSize = FirestoreData.getMyTeamSize(user.getTeam());
            employee = new Employee(user.getPhoneNum(), user.getName(), user.getTeam());
            EmployeeMenu menu = new EmployeeMenu(employee, month, week, myTeamSize, db);
            menu.showEmployeeMenu();

        }
    }

}
