package com.breakfastseta.foodcache.inventory.unclassified;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.breakfastseta.foodcache.App;
import com.breakfastseta.foodcache.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class UnclassifiedActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private UnclassifiedAdapter adapter;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference unclassifiedRef = db.collection("Users").document(App.getFamilyUID()).collection("Unclassified");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unclassified);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Unclassified Ingredients");

        recyclerView = findViewById(R.id.recycler_view);

        setUpRecyclerView();
    }

    private void setUpRecyclerView() {
        unclassifiedRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot snapshots) {
                ArrayList<DocumentSnapshot> snapshotsArr = new ArrayList<>();
                for (DocumentSnapshot d : snapshots) {
                    snapshotsArr.add(d);
                }

                if (snapshotsArr.isEmpty()) {
                    TextView message = findViewById(R.id.message);
                    message.setVisibility(View.VISIBLE);
                }

                adapter = new UnclassifiedAdapter(snapshotsArr, UnclassifiedActivity.this);
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(UnclassifiedActivity.this));
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}