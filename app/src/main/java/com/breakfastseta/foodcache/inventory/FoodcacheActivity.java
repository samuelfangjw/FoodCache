package com.breakfastseta.foodcache.inventory;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.viewpager2.widget.ViewPager2;

import com.breakfastseta.foodcache.AlertReceiver;
import com.breakfastseta.foodcache.App;
import com.breakfastseta.foodcache.R;
import com.breakfastseta.foodcache.Util;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;
import java.util.Objects;

public class FoodcacheActivity extends AppCompatActivity {

    private static final String TAG = "FoodcacheActivity";

    TabLayout tabLayout;
    ViewPager2 viewPager;

    String[] tabs;
    long shortest = Long.MAX_VALUE;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String uid = App.getFamilyUID();
    String personalUID = App.getUID();
    private CollectionReference inventoryRef = db.collection("Users").document(uid).collection("Inventory");
    private CollectionReference unclassifiedRef = db.collection("Users").document(personalUID).collection("Unclassified");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foodcache);

        // To Create Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Util.createToolbar(this, toolbar);

        viewPager = findViewById(R.id.view_pager);
        tabLayout = findViewById(R.id.tabs);

        FloatingActionButton buttonAddItem = findViewById(R.id.add_item_fab);
        buttonAddItem.setOnClickListener(v -> startActivity(new Intent(FoodcacheActivity.this, AddIngredientActivity.class)));

        tabs = getResources().getStringArray(R.array.tabs);

        viewPager.setAdapter(createCardAdapter());
        viewPager.setUserInputEnabled(false);
        new TabLayoutMediator(tabLayout, viewPager,
                new TabLayoutMediator.TabConfigurationStrategy() {
                    @Override
                    public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                        tab.setText(tabs[position]);
                    }
                }).attach();
    }

    @Override
    protected void onStart() {
        super.onStart();

        manageNotifications();
        checkUnclassified();
    }

    private ViewPagerAdapter createCardAdapter() {
        ViewPagerAdapter adapter = new ViewPagerAdapter(this);
        return adapter;
    }

    private void manageNotifications() {
        stopAlarm();

        final Timestamp now = new Timestamp(new Date());

        Query query = inventoryRef.orderBy("dateTimestamp", Query.Direction.ASCENDING)
                .whereGreaterThan("dateTimestamp", now).limit(1);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                        Timestamp timestamp = (Timestamp) document.getData().get("dateTimestamp");
                        long seconds = timestamp.getSeconds();
                        shortest = Math.min(shortest, seconds);
                        alarmHelper();
                    }
                } else {
                    Log.d("TAG", "Error getting documents: ", task.getException());
                }
            }
        });
    }

    private void alarmHelper() {
        final Timestamp now = new Timestamp(new Date());
        long timediff = (shortest - now.getSeconds() - 259200);

        Log.d(TAG, "manageNotifications " + timediff);

        if (timediff < 86400) {
            timediff = 86400 * 1000;
        } else {
            timediff = timediff * 1000;
        }

        long timeInMillies = (now.getSeconds() * 1000) + timediff;

        startAlarm(timeInMillies);
    }

    private void startAlarm(long time) {
        Log.d(TAG, "manageNotifications: time " + time);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlertReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intent, 0);

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, time, pendingIntent);
    }

    private void stopAlarm() {
        Log.d(TAG, "manageNotifications: Stop Alarm");
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlertReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intent, 0);

        alarmManager.cancel(pendingIntent);
    }

    private void checkUnclassified() {
        unclassifiedRef.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String path = document.getReference().getPath();
                                DialogFragment dialogFragment = new UnclassifiedDialogFragment();
                                Bundle bundle = new Bundle();
                                bundle.putString("path", path);
                                dialogFragment.setArguments(bundle);
                                dialogFragment.show(getSupportFragmentManager(), "unclassified");
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
}