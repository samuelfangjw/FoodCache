package com.breakfastseta.foodcache;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class App extends Application {
    private static final String TAG = "App";

    public static String familyUID;
    public static String UID;
    public static ArrayList<String> tabs;

    public static final String CHANNEL_1_ID = "expiryAlarm";
    private static OnInitialisedListener listener;

    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannels();

    }

    public static void afterAuthenticated() {
        //setting uid
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        UID = user.getUid();
        //TODO family sharing
        familyUID = UID;

        //setting tabs
        DocumentReference tabsRef = FirebaseFirestore.getInstance().collection("Users").document(familyUID);
        tabsRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                Object o = task.getResult().get("tabs");
                if (o == null) {
                    ArrayList<String> arr = new ArrayList<>();
                    arr.add("Fridge");
                    arr.add("Pantry");
                    tabsRef.update("tabs", arr);
                    tabs = arr;
                } else {
                    tabs = (ArrayList<String>) task.getResult().get("tabs");
                }

                listener.onComplete();
            }
        });
    }

    private void createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel1 = new NotificationChannel(
                    CHANNEL_1_ID,
                    "Food Expiry Notifications",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel1.setDescription("Sends notifications when food is about to expire");

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel1);
        }
    }

    public static String getFamilyUID() {
        return familyUID;
    }

    public static String getUID() {
        return UID;
    }

    public static ArrayList<String> getTabs() {
        return tabs;
    }

    public interface OnInitialisedListener {
        void onComplete();
    }

    public static void setOnInitialisedListener(OnInitialisedListener listener) {
        App.listener = listener;
    }
}
