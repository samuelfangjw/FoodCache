package com.breakfastseta.foodcache.inventory;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.breakfastseta.foodcache.R;
import com.breakfastseta.foodcache.shoppinglist.ShoppingListItem;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class CardFragment extends Fragment {

    private static final String ARG_COUNT = "param1";
    private Integer counter;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String uid = user.getUid();
    private CollectionReference inventoryRef = db.collection("Users").document(uid).collection("Inventory");

    View view;
    RecyclerView recyclerView;
    private ItemAdapter adapter;

    private LinearLayout coordinatorLayout;

    private Snackbar snackbar = null;

    String[] tabs;

    public CardFragment() {
        // Required empty public constructor
    }

    public static CardFragment newInstance(Integer counter) {
        CardFragment fragment = new CardFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COUNT, counter);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            counter = getArguments().getInt(ARG_COUNT);
        }

        tabs = getResources().getStringArray(R.array.tabs);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_card, container, false);

        recyclerView = view.findViewById(R.id.cardRecyclerView);

        setUpRecyclerView();

        coordinatorLayout = view.findViewById(R.id.activityFoodCache);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void setUpRecyclerView() {
        Query query = inventoryRef.whereEqualTo("location", tabs[counter]).orderBy("dateTimestamp", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<Item> options = new FirestoreRecyclerOptions.Builder<Item>()
                .setQuery(query, Item.class)
                .build();

        adapter = new ItemAdapter(options);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                final int ingredientPos = viewHolder.getAdapterPosition();
                final Item ingredient = adapter.getItem(ingredientPos);

                if (direction == ItemTouchHelper.LEFT) {
                    adapter.deleteItem(viewHolder.getAdapterPosition());

                    snackbar = Snackbar
                            .make(view.findViewById(R.id.cardfragment), "Item was removed from the list.", Snackbar.LENGTH_LONG);
                    snackbar.setAction("UNDO", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            adapter.restoreItem(ingredient);
                        }
                    }).setDuration(Snackbar.LENGTH_SHORT).setActionTextColor(Color.YELLOW)
                            .addCallback(new Snackbar.Callback() {
                                @Override
                                public void onDismissed(Snackbar transientBottomBar, int event) {
                                    switch (event) {
                                        // when snackbar finishes showing
                                        case Snackbar.Callback.DISMISS_EVENT_MANUAL:
                                        case Snackbar.Callback.DISMISS_EVENT_SWIPE:
                                        case Snackbar.Callback.DISMISS_EVENT_TIMEOUT:
                                        case Snackbar.Callback.DISMISS_EVENT_CONSECUTIVE:
                                            CollectionReference notebookRef = FirebaseFirestore.getInstance()
                                                    .collection("Users")
                                                    .document(uid)
                                                    .collection("ShoppingList");

                                            String name = ingredient.getIngredient();
                                            String description = "RESTOCK: Auto Added";
                                            double quantity = ingredient.getQuantity();
                                            String units = ingredient.getUnits();
                                            notebookRef.add(new ShoppingListItem(name, description, quantity, units));
                                            break;
                                    }
                                }
                            });
                    snackbar.show();
                } else if (direction == ItemTouchHelper.RIGHT) {
                    final double quantityDeleted = adapter.deleteExpired(viewHolder.getAdapterPosition(), ingredient);

                    if (quantityDeleted == 0) {
                        Snackbar.make(view.findViewById(R.id.cardfragment), "Item not Expired", Snackbar.LENGTH_LONG).show();
                    } else if (quantityDeleted == ItemAdapter.ALL) {
                        snackbar = Snackbar
                                .make(view.findViewById(R.id.cardfragment), "Item was removed from the list.", Snackbar.LENGTH_LONG);
                        snackbar.setAction("UNDO", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                adapter.restoreItem(ingredient);
                            }
                        }).setDuration(Snackbar.LENGTH_SHORT).setActionTextColor(Color.YELLOW)
                                .addCallback(new Snackbar.Callback() {
                                    @Override
                                    public void onDismissed(Snackbar transientBottomBar, int event) {
                                        switch (event) {
                                            // when snackbar finishes showing
                                            case Snackbar.Callback.DISMISS_EVENT_MANUAL:
                                            case Snackbar.Callback.DISMISS_EVENT_SWIPE:
                                            case Snackbar.Callback.DISMISS_EVENT_TIMEOUT:
                                            case Snackbar.Callback.DISMISS_EVENT_CONSECUTIVE:
                                                CollectionReference notebookRef = FirebaseFirestore.getInstance()
                                                        .collection("Users")
                                                        .document(uid)
                                                        .collection("ShoppingList");

                                                String name = ingredient.getIngredient();
                                                String description = "RESTOCK: Auto Added";
                                                double quantity = ingredient.getQuantity();
                                                String units = ingredient.getUnits();
                                                notebookRef.add(new ShoppingListItem(name, description, quantity, units));
                                                break;
                                        }
                                    }
                                });
                        snackbar.show();
                    } else {
                        snackbar = Snackbar
                                .make(view.findViewById(R.id.cardfragment), "Expired items were removed", Snackbar.LENGTH_LONG);
                        snackbar.setAction("UNDO", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                adapter.restoreExpired(ingredient);
                            }
                        }).setDuration(Snackbar.LENGTH_SHORT).setActionTextColor(Color.YELLOW)
                                .addCallback(new Snackbar.Callback() {
                                    @Override
                                    public void onDismissed(Snackbar transientBottomBar, int event) {
                                        switch (event) {
                                            // when snackbar finishes showing
                                            case Snackbar.Callback.DISMISS_EVENT_MANUAL:
                                            case Snackbar.Callback.DISMISS_EVENT_SWIPE:
                                            case Snackbar.Callback.DISMISS_EVENT_TIMEOUT:
                                            case Snackbar.Callback.DISMISS_EVENT_CONSECUTIVE:
                                                CollectionReference notebookRef = FirebaseFirestore.getInstance()
                                                        .collection("Users")
                                                        .document(uid)
                                                        .collection("ShoppingList");

                                                String name = ingredient.getIngredient();
                                                String description = "RESTOCK: Auto Added";
                                                String units = ingredient.getUnits();
                                                notebookRef.add(new ShoppingListItem(name, description, quantityDeleted, units));
                                                break;
                                        }
                                    }
                                });
                        snackbar.show();
                    }

                }
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

                new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        .addBackgroundColor(ContextCompat.getColor(getContext(), R.color.primaryColor))
                        .addActionIcon(R.drawable.ic_delete)
                        .addSwipeLeftLabel("All")
                        .addSwipeRightLabel("Expired")
                        .create()
                        .decorate();

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }

        }).attachToRecyclerView(recyclerView);

        adapter.setOnItemClickListener(new ItemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                Item item = documentSnapshot.toObject(Item.class);
                String id = documentSnapshot.getId(); //id stored in firebase database
                String path = documentSnapshot.getReference().getPath();

                // Starting EditItem activity
                Intent intent = new Intent(getContext(), EditItem.class);
                intent.putExtra("path", path);
                startActivity(intent);
            }
        });
    }

    // Starts listening for changes on activity start
    @Override
    public void onStart() {
        super.onStart();
        if (adapter != null) {
            adapter.startListening();
        }
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

    // Stops listening for changes on activity stop
    @Override
    public void onStop() {
        super.onStop();
        if (adapter != null) {
            adapter.startListening();
        }
    }
}