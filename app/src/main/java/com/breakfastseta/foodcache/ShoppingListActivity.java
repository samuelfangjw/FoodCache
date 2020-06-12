package com.breakfastseta.foodcache;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.vision.text.Line;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class ShoppingListActivity extends AppCompatActivity {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference notebookRef = db.collection("ShoppingList");

    private  ShoppingListAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_list);

        setUpRecyclerView();
    }

    private void setUpRecyclerView() {
        Query query = notebookRef.orderBy("priority", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<ShoppingListItem> options = new FirestoreRecyclerOptions.Builder<ShoppingListItem>()
                .setQuery(query, ShoppingListItem.class)
                .build();

        adapter = new ShoppingListAdapter(options);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
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