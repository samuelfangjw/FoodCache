package com.breakfastseta.foodcache.social;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.breakfastseta.foodcache.App;
import com.breakfastseta.foodcache.R;
import com.breakfastseta.foodcache.Util;
import com.breakfastseta.foodcache.profile.Profile;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class SocialFindFriendMain extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final String TAG = "SocialFindFriends";

    String uid = App.getUID();

    private CollectionReference notebookRef = db.collection("Profiles");
    private CoordinatorLayout coordinatorLayout;

    private EditText editTextNameSearch;
    private RecyclerView recyclerView;

    private SocialFindFriendAdapter adapter;

    private Query query;

    ArrayList<Profile> searchArr = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social_find_friend_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Find Friends");
        Util.createToolbar(this, toolbar);

        editTextNameSearch = findViewById(R.id.friends_search_bar);
        recyclerView = findViewById(R.id.findfriend_recycler_view);

        query = notebookRef.orderBy("name", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<Profile> options = new FirestoreRecyclerOptions.Builder<Profile>()
                .setQuery(query, Profile.class)
                .build();

        adapter = new SocialFindFriendAdapter(options, this, uid);
        adapter.setAddFriendListener(new SocialFindFriendAdapter.AddFriendListener() {
            @Override
            public void addfriend(String uID) {
                addFriendRequest(uID);
            }
        });

        recyclerView = findViewById(R.id.findfriend_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        editTextNameSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().isEmpty()) {
                    query = notebookRef.orderBy("name").startAt(s.toString()).endAt(s.toString() + "\uf8ff");
                    FirestoreRecyclerOptions<Profile> newOptions = new FirestoreRecyclerOptions.Builder<Profile>()
                            .setQuery(query, Profile.class)
                            .build();
                    adapter.updateOptions(newOptions);
                    Toast toast = Toast.makeText(SocialFindFriendMain.this, s, Toast.LENGTH_SHORT);
                    toast.show();
                } else {
                    query = notebookRef.orderBy("name", Query.Direction.DESCENDING);
                    FirestoreRecyclerOptions<Profile> newOptions2 = new FirestoreRecyclerOptions.Builder<Profile>()
                            .setQuery(query, Profile.class)
                            .build();
                    adapter.updateOptions(newOptions2);

                }
            }
        });

    }

    public void addFriendRequest(String requestuID) {
        DocumentReference docRef = db.collection("Profiles").document(requestuID);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {

            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    ArrayList<String> arrayList = ((ArrayList<String>) documentSnapshot.get("friendRequests"));
                    if (arrayList.contains(uid)) {
                        Toast toast = Toast.makeText(SocialFindFriendMain.this, "Friend Request Already Sent!", Toast.LENGTH_SHORT);
                        toast.show();
                    } else {
                        arrayList.add(uid);
                        docRef.update("friendRequests", arrayList);
                        Toast toast = Toast.makeText(SocialFindFriendMain.this, "Friend Request Sent!", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                } else {
                    Log.d(TAG, "get failed with",  task.getException());
                }
            }
        });
    }



    private void search(String text) {
        query = notebookRef.orderBy("name").whereLessThanOrEqualTo("name", text + "\uf8ff");
        FirestoreRecyclerOptions<Profile> newOptions = new FirestoreRecyclerOptions.Builder<Profile>()
                .setQuery(query, Profile.class)
                .build();
        adapter.updateOptions(newOptions);
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