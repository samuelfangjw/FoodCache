package com.breakfastseta.foodcache;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.viewpager2.widget.ViewPager2;

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
    int count = 0;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String uid = user.getUid();
    private CollectionReference inventoryRef = db.collection("Users").document(uid).collection("Inventory");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foodcache);

        viewPager = findViewById(R.id.view_pager);
        tabLayout = findViewById(R.id.tabs);

        FloatingActionButton buttonAddItem = findViewById(R.id.add_item_fab);
        buttonAddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(FoodcacheActivity.this, AddIngredientActivity.class));
            }
        });

        tabs = getResources().getStringArray(R.array.tabs);

        viewPager.setAdapter(createCardAdapter());
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

    // Use custom menu as menu for this activity
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.inventory_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // Decide what to do when menu item clicked
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.profile:
                startActivity(new Intent(FoodcacheActivity.this, ProfileActivity.class));
                return true;
            case R.id.shopping_list:
                startActivity(new Intent(FoodcacheActivity.this, ShoppingListActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private ViewPagerAdapter createCardAdapter() {
        ViewPagerAdapter adapter = new ViewPagerAdapter(this);
        return adapter;
    }

    private void manageNotifications() {
        stopAlarm();

        final Timestamp now = new Timestamp(new Date());

        for (String s : tabs) {
            CollectionReference collectionRef = inventoryRef.document(s).collection("Ingredients");
            Query query = collectionRef.orderBy("dateTimestamp", Query.Direction.ASCENDING)
                    .whereGreaterThan("dateTimestamp", now).limit(1);
            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                            Timestamp timestamp = (Timestamp) document.getData().get("dateTimestamp");
                            long seconds = timestamp.getSeconds();
                            shortest = Math.min(shortest, seconds);
                            callbackHelper();
                        }
                    } else {
                        Log.d("TAG", "Error getting documents: ", task.getException());
                    }
                }
            });
        }
    }

    private void callbackHelper() {
        count++;
        if (count >= tabs.length) {
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
        CollectionReference unclassifiedRef = inventoryRef.document("Unclassified").collection("Ingredients");
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