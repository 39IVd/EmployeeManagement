import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import database.FirestoreData;
import models.Admin;
import models.Employee;
import models.User;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ExecutionException;


public class Main {
    static User user;
    static Admin admin;
    static Employee employee;
    static int type = -1;
    static Firestore db;
    static List<String> teamList = new ArrayList<>();
    static int myTeamSize;
    static int month = 10, week = 3;

    public static void main(String[] args) throws IOException, URISyntaxException {
        System.out.println(LocalDateTime.now());
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
            myTeamSize = FirestoreData.getMyTeamSize(user.getTeam());
            employee = getInfoFromDB();
            EmployeeMenu menu = new EmployeeMenu(employee, month, week, myTeamSize, db);
            menu.showEmployeeMenu();

        }
    }
    public static Employee getInfoFromDB() {
        // DB에서 employee 정보 불러오기
        DocumentReference docRef = db.collection("Employee").document(user.getPhoneNum());
        DocumentReference month_docRef = docRef.collection("Work").document(month+"월");
        ApiFuture<DocumentSnapshot> future = docRef.get();
        ApiFuture<DocumentSnapshot> future_work = month_docRef.get();
        Employee employee;
        try {
            DocumentSnapshot document = future.get();
           int holiday_total = Integer.parseInt(document.get("holiday_total").toString());
           int not_workhour_total = Integer.parseInt(document.get("not_workhour_total").toString());
            DocumentSnapshot document_work = future_work.get();
           List<Object> workhour_list = (ArrayList)document_work.get("workhour_list");
           employee = new Employee(user.getPhoneNum(), user.getName(), user.getTeam(),holiday_total, not_workhour_total, workhour_list);
            return employee;
        }
        catch (InterruptedException ie) { } catch (ExecutionException ee) { }
        return null;
    }

}
