package com.breakfastseta.foodcache.social;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.breakfastseta.foodcache.App;
import com.breakfastseta.foodcache.R;
import com.breakfastseta.foodcache.Util;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;

public class SocialMainActivity extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final String TAG = "SocialActivity";

    String uid = App.getUID();

    private CollectionReference notebookRef = db.collection("SocialUsers").document(uid).collection("UserPosts");
    private CoordinatorLayout coordinatorLayout;

    private SocialPostAdapter adapter;

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

        setUpRecyclerView();
        coordinatorLayout = findViewById(R.id.activitySocialMain);
    }

    private void setUpRecyclerView() {
        Query query = notebookRef.orderBy("date", Query.Direction.DESCENDING)
                .orderBy("time", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<SocialPost> options = new FirestoreRecyclerOptions.Builder<SocialPost>()
                .setQuery(query, SocialPost.class)
                .build();

        adapter = new SocialPostAdapter(options, this, uid);

        RecyclerView recyclerView = findViewById(R.id.social_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

    }



    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}