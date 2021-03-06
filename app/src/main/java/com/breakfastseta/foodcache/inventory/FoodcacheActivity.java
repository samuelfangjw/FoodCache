package com.breakfastseta.foodcache.inventory;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager2.widget.ViewPager2;

import com.breakfastseta.foodcache.AlertReceiver;
import com.breakfastseta.foodcache.App;
import com.breakfastseta.foodcache.R;
import com.breakfastseta.foodcache.Util;
import com.breakfastseta.foodcache.inventory.unclassified.UnclassifiedActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.mikepenz.actionitembadge.library.ActionItemBadge;

import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class FoodcacheActivity extends AppCompatActivity {

    private static final String TAG = "FoodcacheActivity";

    TabLayout tabLayout;
    ViewPager2 viewPager;

    ArrayList<String> tabs;
    long shortest = Long.MAX_VALUE;
    ViewPagerAdapter adapter;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    String uid = App.getFamilyUID();
    private CollectionReference inventoryRef = db.collection("Users").document(uid).collection("Inventory");

    MenuItem menuItem;

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

        makeTabs();
    }

    private void makeTabs() {
        tabs = App.getTabs();

        viewPager.setAdapter(createCardAdapter());
        viewPager.setUserInputEnabled(false);
        new TabLayoutMediator(tabLayout, viewPager,
                new TabLayoutMediator.TabConfigurationStrategy() {
                    @Override
                    public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                        tab.setText(tabs.get(position));
                    }
                }).attach();
    }

    @Override
    protected void onStart() {
        super.onStart();

        manageNotifications();
    }

    @Override
    protected void onResume() {
        super.onResume();

        checkUnclassified();
    }

    private ViewPagerAdapter createCardAdapter() {
        adapter = new ViewPagerAdapter(this);
        return adapter;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.foodcache_menu, menu);
        menuItem = menu.findItem(R.id.action_unclassified);
        if (App.getUnclassifiedNum() > 0) {
            ActionItemBadge.update(this, menu.findItem(R.id.action_unclassified), getDrawable(R.drawable.ic_unclassified), ActionItemBadge.BadgeStyles.RED, App.getUnclassifiedNum());
        } else {
            ActionItemBadge.hide(menu.findItem(R.id.action_unclassified));
        }

        return true;
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
        if (menuItem != null) {
            int num = App.getUnclassifiedNum();
            if (num > 0) {
                ActionItemBadge.update(menuItem, num);
            } else {
                ActionItemBadge.hide(menuItem);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_tabs:
                Intent intent = new Intent(this, ManageTabsActivity.class);
                startActivityForResult(intent, 0);
                return true;
            case R.id.action_unclassified:
                Intent intent_unclassified = new Intent(this, UnclassifiedActivity.class);
                startActivityForResult(intent_unclassified, 0);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 0) {
            adapter.notifyDataSetChanged();
        }
    }
}