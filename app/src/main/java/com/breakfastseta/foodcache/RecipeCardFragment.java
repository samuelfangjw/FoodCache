package com.breakfastseta.foodcache;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RecipeCardFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
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

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void setUpRecyclerView() {
        CollectionReference collectionRef = recipeRef.document(tabs[counter]).collection("Recipes");
        Query query = collectionRef.orderBy("name", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<Recipe> options = new FirestoreRecyclerOptions.Builder<Recipe>()
                .setQuery(query, Recipe.class)
                .build();

        adapter = new RecipeAdapter(options);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new RecipeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                Item item = documentSnapshot.toObject(Item.class);
                String id = documentSnapshot.getId(); //id stored in firebase database
                String path = documentSnapshot.getReference().getPath();

                // Starting EditItem activity
                //TODO edit intent to correct path
                Intent intent = new Intent(getContext(), ViewRecipe.class);
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

    // Stops listening for changes on activity stop
    @Override
    public void onStop() {
        super.onStop();
        if (adapter != null) {
            adapter.startListening();
        }
    }
}