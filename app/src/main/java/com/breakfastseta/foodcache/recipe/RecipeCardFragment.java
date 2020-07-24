package com.breakfastseta.foodcache.recipe;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.breakfastseta.foodcache.R;
import com.breakfastseta.foodcache.recipe.viewrecipe.ViewRecipeActivity;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

public class RecipeCardFragment extends Fragment {

    private static final String ARG_COUNT = "param1";
    private Integer counter;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String uid = user.getUid();
    private CollectionReference recipeRef = db.collection("Users").document(uid).collection("RecipeCache");

    View view;
    RecyclerView recyclerView;
    private RecipeAdapter adapter;
    private TextView message2;
    private ListenerRegistration registration;

    private LinearLayout coordinatorLayout;

    String[] tabs;

    public RecipeCardFragment() {
        // Required empty public constructor
    }

    public static RecipeCardFragment newInstance(Integer counter) {
        RecipeCardFragment fragment = new RecipeCardFragment();
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

        tabs = getResources().getStringArray(R.array.cuisines);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_card, container, false);

        recyclerView = view.findViewById(R.id.cardRecyclerView);

        setUpRecyclerView();

        coordinatorLayout = view.findViewById(R.id.activityRecipe);

        message2 = view.findViewById(R.id.message2);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void setUpRecyclerView() {
        CollectionReference collectionRef = recipeRef.document(tabs[counter]).collection("Recipes");
        Query query = collectionRef.orderBy("name", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<RecipeSnippet> options = new FirestoreRecyclerOptions.Builder<RecipeSnippet>()
                .setQuery(query, RecipeSnippet.class)
                .build();

        adapter = new RecipeAdapter(options);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new RecipeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                String path = (String) documentSnapshot.get("path");

                Intent intent = new Intent(getContext(), ViewRecipeActivity.class);
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

        Query query = recipeRef.document(tabs[counter]).collection("Recipes");
        registration = query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {
                if (snapshots.isEmpty()) {
                    message2.setVisibility(View.VISIBLE);
                } else {
                    message2.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    // Stops listening for changes on activity stop
    @Override
    public void onStop() {
        super.onStop();
        if (adapter != null) {
            adapter.startListening();
        }

        registration.remove();
    }
}