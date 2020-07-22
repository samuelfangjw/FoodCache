package com.breakfastseta.foodcache;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.breakfastseta.foodcache.profile.Profile;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class App extends Application {
    private static final String TAG = "App";

    public static Profile profile = new Profile(); // Stores profile information
    public static int authProvider;
    public static final int PASSWORD = 0;
    public static final int GOOGLE = 1;
    public static final int ANONYMOUS = 2;

    public static long unclassifiedNum;

    public static ArrayList<String> tabs;

    public static final String CHANNEL_1_ID = "expiryAlarm";
    private static OnAppCompleteListener listener;

    private static DocumentReference profileRef;

    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannels();
    }


    // afterAuthenticated, do profile stuff
    public static void afterAuthenticated(Context context) {
        //TODO check if user is authenticated using anonymous means
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String UID = user.getUid();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        profileRef = db.collection("Profiles").document(UID);
        profileRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    String name = documentSnapshot.getString("name");

                    if (name == null) {
                        // logging in for the first time
                        profile.setName(user.getDisplayName());
                        profile.setEmail(user.getEmail());
                        profile.setUID(UID);
                        profileRef.set(profile).addOnCompleteListener(task1 -> setTabs());
                    } else {
                        profile.setName(name);
                        profile.setEmail(documentSnapshot.getString("email"));
                        profile.setUsername(documentSnapshot.getString("username"));
                        profile.setUID(UID);
                        profile.setFamilyUID(documentSnapshot.getString("familyUID"));
                        profile.setPhotoURL(documentSnapshot.getString("photoURL"));
                        profile.setUseFamilySharing(documentSnapshot.getBoolean("useFamilySharing"));
                        profile.setRecipeCount(documentSnapshot.getLong("recipeCount"));

                        ArrayList<String> friends = (ArrayList<String>) documentSnapshot.get("friends");
                        if (friends != null) {
                            profile.setFriends(friends);
                        }

                        Map<String, Long> recipesPrepared = (Map<String, Long>) documentSnapshot.get("recipesPrepared");
                        if (recipesPrepared != null) {
                            profile.setRecipesPrepared(recipesPrepared);
                            setTabs();
                        } else {
                            profileRef.set(profile).addOnCompleteListener(task1 -> setTabs());
                        }
                    }

                    checkNumUnclassified();

                } else {
                    Toast.makeText(context, "Something went wrong, please restart", Toast.LENGTH_LONG).show();
                }
            }
        });

        setProvider();
    }

    private static void checkNumUnclassified() {
        CollectionReference unclassifiedRef = FirebaseFirestore.getInstance().collection("Users").document(App.getFamilyUID()).collection("Unclassified");
        unclassifiedRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot snapshots) {
                Long num = 0L;
                for (DocumentSnapshot d : snapshots) {
                    num ++;
                }
                unclassifiedNum = num;
            }
        });
    }

    public static DocumentReference getProfileRef() {
        return profileRef;
    }

    private static void setProvider() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        List<? extends UserInfo> providerdata = user.getProviderData();
        Log.d(TAG, providerdata.toString());
        if (providerdata.size() > 1) {
            UserInfo info = providerdata.get(1);
            String id = info.getProviderId();
            if (id.equals("password") ) {
                authProvider = PASSWORD;
            } else if (id.equals("google.com")) {
                authProvider = GOOGLE;
            } else {
                authProvider = ANONYMOUS;
            }
        }
    }

    private static void setTabs() {
        //setting tabs
        DocumentReference tabsRef = FirebaseFirestore.getInstance().collection("Users").document(getFamilyUID());
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
        if (profile.getFamilyUID() == null) {
            return profile.getUID();
        } else {
            if (profile.getUseFamilySharing()) {
                return profile.getFamilyUID();
            } else {
                return profile.getUID();
            }
        }
    }

    public static String getUID() {
        return profile.getUID();
    }

    public static Profile getProfile() {
        return profile;
    }

    public static int getAuthProvider() {
        return authProvider;
    }

    public static ArrayList<String> getTabs() {
        return tabs;
    }

    public interface OnAppCompleteListener {
        void onComplete();
    }

    public static void setOnAppCompleteListener(OnAppCompleteListener listener) {
        App.listener = listener;
    }

    public static long getUnclassifiedNum() {
        return unclassifiedNum;
    }

    public static void minusUnclassifiedNum() {
        unclassifiedNum--;
    }

    public static void plusUnclassifiedNum() {
        unclassifiedNum++;
    }
}
