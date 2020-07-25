package com.breakfastseta.foodcache.social;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.breakfastseta.foodcache.App;
import com.breakfastseta.foodcache.R;
import com.breakfastseta.foodcache.Util;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class SocialMainActivity extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final String TAG = "SocialActivity";

    String uid = App.getUID();

    private CollectionReference notebookRef = db.collection("SocialUsers").document(uid).collection("posts");
    private CoordinatorLayout coordinatorLayout;

    private SocialFeedAdapter adapter;
    private RecyclerView recyclerView;
    private ArrayList<QueryDocumentSnapshot> queryDocumentSnapshotsArr = new ArrayList<QueryDocumentSnapshot>();
    private TextView message;

    private ListenerRegistration registration;

    private ArrayList<String> friendArr = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Social");
        Util.createToolbar(this, toolbar);

        FloatingActionButton buttonAddPost = findViewById(R.id.social_add_post);
        buttonAddPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SocialMainActivity.this, SocialAddPostActivity.class));
            }
        });

        recyclerView = findViewById(R.id.social_recycler_view);
        message = findViewById(R.id.message);

        adapter = new SocialFeedAdapter(queryDocumentSnapshotsArr, this);

        recyclerView.setAdapter(adapter);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        coordinatorLayout = findViewById(R.id.activitySocialMain);
    }

    private void getData() {
        Query query = notebookRef.orderBy("date", Query.Direction.DESCENDING)
                .orderBy("time", Query.Direction.DESCENDING);

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    queryDocumentSnapshotsArr.clear();
                    QuerySnapshot snapshots = task.getResult();
                    for (QueryDocumentSnapshot queryDocumentSnapshot : snapshots) {
                        queryDocumentSnapshotsArr.add(queryDocumentSnapshot);
                    }

                    if (queryDocumentSnapshotsArr.isEmpty()) {
                        message.setVisibility(View.VISIBLE);
                    } else {
                        message.setVisibility(View.GONE);
                    }

                    if (adapter != null) {
                        adapter.notifyDataSetChanged();
                    }
                    setSnapshotListener(query);
                }
            }
        });
    }

    private void setSnapshotListener(Query query) {
        registration = query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                queryDocumentSnapshotsArr.clear();
                if (queryDocumentSnapshots != null && adapter != null) {
                    for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                        queryDocumentSnapshotsArr.add(queryDocumentSnapshot);
                    }
                    adapter.notifyDataSetChanged();
                    if (queryDocumentSnapshots.isEmpty()) {
                        message.setVisibility(View.VISIBLE);
                    } else {
                        message.setVisibility(View.GONE);
                    }
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        registration.remove();
        super.onDestroy();
    }

//    private void createRecyclerView(ArrayList<QueryDocumentSnapshot> queryDocumentSnapshotsArr) {
//
//    }

//    private void findFriendList() {
//        db.collection("Profiles").document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                if (task.isSuccessful()) {
//                    friendArr.addAll((ArrayList<String>) task.getResult().get("friends"));
//                } else {
//                    Log.d(TAG, "find friend list failed with ", task.getException());
//
//                }
//            }
//        });
//    }


    @Override
    protected void onResume() {
        super.onResume();
        getData();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}