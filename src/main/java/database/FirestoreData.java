package database;

import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.*;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import com.google.firebase.database.DatabaseReference;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class FirestoreData {
    static Firestore db;
    static CollectionReference colRef;
    static DocumentReference docRef;

    public static Firestore initFirebase() throws IOException {
        FileInputStream serviceAccount =
                new FileInputStream("/Users/paige/swproject/employeemanagement-b0424-firebase-adminsdk-v2e5h-6730f2119a.json");

        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setDatabaseUrl("https://employeemanagement-b0424.firebaseio.com")
                .build();
        //        Cloud Firestore 초기화
        FirebaseApp.initializeApp(options);
        db = FirestoreClient.getFirestore();
        return db;
    }
    public static List<String> getTeamName() {
        colRef = db.collection("Team");
        List<String> teamList = new ArrayList<>();
        ApiFuture<QuerySnapshot> query = colRef.get();
        try {
            List<QueryDocumentSnapshot> documents = query.get().getDocuments();
            for (QueryDocumentSnapshot document : documents) {
                teamList.add(document.getId());
            }
        }
        catch (InterruptedException ie) { } catch (ExecutionException ee) { }
        return teamList;
    }
    public static int getMyTeamSize(String myTeam) {
        colRef = db.collection("Employee");
        Query query = colRef.whereEqualTo("team", myTeam);
        ApiFuture<QuerySnapshot> querySnapshot = query.get();
        int teamSize = 1;
        try {
            teamSize = querySnapshot.get().getDocuments().size();
        }
        catch (InterruptedException ie) { } catch (ExecutionException ee) { }
        return teamSize;
    }
    public static void addData() throws IOException, InterruptedException, ExecutionException {
        DocumentReference docRef = db.collection("Admin").document("01033333333");

        // HashMap을 이용해 put data
        Map<String, Object> data = new HashMap<>();
        data.put("phone", "01033333333");
        data.put("name", "ccc");
        data.put("password", "3333");

        // asynchronously write data
        ApiFuture<WriteResult> result = docRef.set(data);

        // result.get() blocks on response
        System.out.println("Update time : " + result.get().getUpdateTime());
    }
    public static void getData() throws InterruptedException, ExecutionException{
        // asynchronously retrieve all users
        ApiFuture<QuerySnapshot> query = db.collection("Admin").get();

        // query.get() blocks on response
        QuerySnapshot querySnapshot = query.get();
        List<QueryDocumentSnapshot> documents = querySnapshot.getDocuments();
        for (QueryDocumentSnapshot document : documents) {
//            document.getId() : document 이름을 read
            System.out.println("User: " + document.getId());
//            document.getString() : 특정 field 값을 read
            System.out.println("phone : " + document.getString("phone"));
            if (document.contains("password")) {
                System.out.println("password : " + document.getString("password"));
            }
            System.out.println("name : " + document.getString("name"));
        }

    }
    public static void updateData() throws InterruptedException, ExecutionException {
        //asynchronously update doc, create the document if missing
        Map<String, Object> update = new HashMap<>();
        update.put("name", "이승주");

        ApiFuture<WriteResult> writeResult =
                db.collection("Admin")
                        .document("01011111111")
                        .set(update, SetOptions.merge());
        System.out.println("Update time : " + writeResult.get().getUpdateTime());
    }
}
