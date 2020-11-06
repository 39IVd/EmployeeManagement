package database;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.core.ApiFuture;
import com.google.api.gax.paging.Page;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.*;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import com.google.firebase.cloud.StorageClient;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.lang.Object;

public class FirestoreData {
    static Firestore db;
    static CollectionReference colRef;

    public static Firestore initFirebase() throws IOException, URISyntaxException {

      FileInputStream serviceAccount =
//              TODO: FileInputStream() 안의 경로를 em_firebase.json 파일의 절대 경로로 변경
                new FileInputStream("/Users/paige/swproject/em_firebase.json");

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
}
