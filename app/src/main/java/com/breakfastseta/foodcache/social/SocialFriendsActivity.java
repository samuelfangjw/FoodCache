package com.breakfastseta.foodcache.social;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.breakfastseta.foodcache.App;
import com.breakfastseta.foodcache.R;
import com.breakfastseta.foodcache.Util;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.mikepenz.actionitembadge.library.ActionItemBadge;

import java.util.ArrayList;

public class SocialFriendsActivity extends AppCompatActivity {

    private static final String TAG = "FRIEND_REQ";
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private CollectionReference notebookRef = db.collection("Profiles");

    private ArrayList<String> friendsArr = new ArrayList<>();

    ArrayList<QueryDocumentSnapshot> queryDocumentSnapshotsFriendProfArr = new ArrayList<>();

    private SocialFriendsListAdapter adapter2;

    private RecyclerView recyclerViewFriendList;
    private TextView message;

    private MenuItem menuItem;

    String uID = App.getUID();

    ListenerRegistration registration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social_friends);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Friends");
        Util.createToolbar(this, toolbar);

        recyclerViewFriendList = findViewById(R.id.social_friend_recycler_view);
        message = findViewById(R.id.message);

        getFriendListData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.friends_menu, menu);
        menuItem = menu.findItem(R.id.action_request);

        DocumentReference docRef = db.collection("Profiles").document(uID);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                ArrayList<String> friendRequests = (ArrayList<String>) documentSnapshot.get("friendRequests");
                int count = 0;

                if (friendRequests != null) {
                    count += friendRequests.size();
                }

                if (count > 0) {
                    ActionItemBadge.update(SocialFriendsActivity.this, menuItem, getDrawable(R.drawable.ic_people), ActionItemBadge.BadgeStyles.RED, count);
                } else {
                    ActionItemBadge.hide(menuItem);
                }

                registration = docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {

                        if (documentSnapshot != null) {
                            ArrayList<String> friendRequests = (ArrayList<String>) documentSnapshot.get("friendRequests");
                            int count = 0;

                            if (friendRequests != null) {
                                count += friendRequests.size();
                            }

                            if (count > 0) {
                                ActionItemBadge.update(SocialFriendsActivity.this, menuItem, getDrawable(R.drawable.ic_people), ActionItemBadge.BadgeStyles.RED, count);
                            } else {
                                ActionItemBadge.hide(menuItem);
                            }

                            compareFriendsArray(documentSnapshot);
                        }

                    }
                });
            }
        });

        return true;
    }

    private void compareFriendsArray(DocumentSnapshot documentSnapshot) {
        ArrayList<String> friendsArr2 = (ArrayList<String>) documentSnapshot.get("friends");

        if (adapter2 != null && !friendsArr2.equals(friendsArr)) {
            if (friendsArr2.isEmpty()) {
                queryDocumentSnapshotsFriendProfArr.clear();
                adapter2.notifyDataSetChanged();
                message.setVisibility(View.VISIBLE);
            } else {
                message.setVisibility(View.GONE);
                notebookRef.whereIn("uid", friendsArr2).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            queryDocumentSnapshotsFriendProfArr.clear();
                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                queryDocumentSnapshotsFriendProfArr.add(documentSnapshot);
                            }
                            adapter2.notifyDataSetChanged();
                        }
                    }
                });
            }
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_request:
                Intent intent = new Intent(this, SocialFriendRequest.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // for friend list
    private void getFriendListData() {
        DocumentReference docRef = notebookRef.document(uID);

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    ArrayList<String> friendsArr2 = (ArrayList<String>) documentSnapshot.get("friends");

                    if (friendsArr2 != null) {
                        friendsArr.addAll(friendsArr2);
                    }

                    if (friendsArr.isEmpty()) {
                        message.setVisibility(View.VISIBLE);
                        createRecyclerViewFriendList();
                    } else {
                        getFriendProfileData(friendsArr);
                    }
                } else {
                    Log.d(TAG, "get failed with", task.getException());
                }
            }
        });
    }

    private void getFriendProfileData(ArrayList<String> friendsArr) {
        notebookRef.whereIn("uid", friendsArr).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                        queryDocumentSnapshotsFriendProfArr.add(documentSnapshot);
                    }
                    createRecyclerViewFriendList();
                }
            }
        });
    }

    private void createRecyclerViewFriendList() {
        adapter2 = new SocialFriendsListAdapter(queryDocumentSnapshotsFriendProfArr, this);

        recyclerViewFriendList.setAdapter(adapter2);

        recyclerViewFriendList.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onDestroy() {
        registration.remove();
        super.onDestroy();
    }
}