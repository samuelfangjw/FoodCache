package com.breakfastseta.foodcache.shoppinglist;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.breakfastseta.foodcache.App;
import com.breakfastseta.foodcache.R;
import com.breakfastseta.foodcache.Util;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class ShoppingListActivity extends AppCompatActivity {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    String uid = App.getFamilyUID();
    private CollectionReference notebookRef = db.collection("Users").document(uid).collection("ShoppingList");
    private CoordinatorLayout coordinatorLayout;

    private static final String TAG = "ShoppingListActivity";
    private Snackbar snackbar;
    private TextView message;

    private ShoppingListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Shopping List");
        Util.createToolbar(this, toolbar);

        FloatingActionButton buttonAddShopItem = findViewById(R.id.button_add_note);
        buttonAddShopItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View V) {
                startActivity(new Intent(ShoppingListActivity.this, AddShopIngredientActivity.class));
            }
        });

        setUpRecyclerView();
        coordinatorLayout = findViewById(R.id.activityShoppingList);
        message = findViewById(R.id.message);
    }

    private void setUpRecyclerView() {
        Query query = notebookRef.orderBy("noItems", Query.Direction.DESCENDING);

        query.addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {
                if (snapshots.isEmpty()) {
                    message.setVisibility(View.VISIBLE);
                } else {
                    message.setVisibility(View.INVISIBLE);
                }
            }
        });

        FirestoreRecyclerOptions<ShoppingListItem> options = new FirestoreRecyclerOptions.Builder<ShoppingListItem>()
                .setQuery(query, ShoppingListItem.class)
                .build();

        adapter = new ShoppingListAdapter(options);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                final int shopItemPos = viewHolder.getAdapterPosition();
                final ShoppingListItem shopItem = adapter.getItem(shopItemPos);

                adapter.deleteItem(viewHolder.getAdapterPosition());

                // For undo feature when swiping to delete. Creates new snackbar with UNDO
                // restoreItem method added in adapter.
                if (direction == ItemTouchHelper.LEFT) {
                    // left swipe removes shopping list item and deletes it
                    snackbar = Snackbar
                            .make(coordinatorLayout, "Item was deleted from Shopping List!", Snackbar.LENGTH_LONG);

                    snackbar.setAction("UNDO", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            adapter.restoreItem(shopItem);
                        }
                    }).setDuration(5000);
                    snackbar.setActionTextColor(Color.YELLOW);
                    snackbar.show();
                } else {
                    // right swipe adds to foodCache
                    snackbar = Snackbar
                            .make(coordinatorLayout, "Item was moved to FoodCache!", Snackbar.LENGTH_LONG);

                    snackbar.setAction("UNDO", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            adapter.restoreItem(shopItem);
                            App.minusUnclassifiedNum();
                        }
                    }).setDuration(5000).setActionTextColor(Color.YELLOW)
                    .addCallback(new Snackbar.Callback() {
                        @Override
                        public void onDismissed(Snackbar transientBottomBar, int event) {
                            switch(event) {
                                case Snackbar.Callback.DISMISS_EVENT_MANUAL:
                                case Snackbar.Callback.DISMISS_EVENT_SWIPE:
                                case Snackbar.Callback.DISMISS_EVENT_TIMEOUT:
                                case Snackbar.Callback.DISMISS_EVENT_CONSECUTIVE:
                                    final CollectionReference unclassifiedRef = db.collection("Users").document(App.getFamilyUID()).collection("Unclassified");
                                    CollectionReference barcodeRef = db.collection("Users").document(uid).collection("Barcodes");

                                    final String ingredient = shopItem.getItemName();
                                    final double quantity = shopItem.getNoItems();
                                    final String units = shopItem.getUnits();

                                    Map<String, Object> incompleteItem = new HashMap<>();
                                    incompleteItem.put("Name", ingredient);
                                    incompleteItem.put("quantity", quantity);
                                    incompleteItem.put("units", units);
                                    unclassifiedRef.add(incompleteItem);
                                    break;
                            }
                        }
                    });
                    snackbar.show();
                    App.plusUnclassifiedNum();
                }
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        .addBackgroundColor(ContextCompat.getColor(ShoppingListActivity.this, R.color.primaryColor))
                        .addSwipeLeftActionIcon(R.drawable.ic_delete)
                        .addSwipeRightActionIcon(R.drawable.ic_shopping_list)
                        .addSwipeLeftLabel("Delete")
                        .addSwipeRightLabel("FoodCache")
                        .create()
                        .decorate();

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }

        }).attachToRecyclerView(recyclerView);

        adapter.setOnItemClickListener(new ShoppingListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                ShoppingListItem item = documentSnapshot.toObject(ShoppingListItem.class);
                String id = documentSnapshot.getId();
                String path = documentSnapshot.getReference().getPath();

                Intent intent = new Intent(ShoppingListActivity.this, EditShoppingIngredient.class);
                intent.putExtra("path", path);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onPause() {
        if (snackbar != null) {
            if (snackbar.isShown()) {
                snackbar.dismiss();
            }
        }
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}