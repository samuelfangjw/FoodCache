package com.breakfastseta.foodcache;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class App extends Application {

    public static String FamilyUID;
    public static String UID;

    public static final String CHANNEL_1_ID = "expiryAlarm";

    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannels();
    }

    public static void setUID() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        UID = user.getUid();
        //TODO family sharing
        FamilyUID = UID;
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
        return FamilyUID;
    }

    public static String getUID() {
        return UID;
    }
}
