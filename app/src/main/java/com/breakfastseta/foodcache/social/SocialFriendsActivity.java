package com.breakfastseta.foodcache.social;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.breakfastseta.foodcache.App;
import com.breakfastseta.foodcache.R;
import com.breakfastseta.foodcache.Util;
import com.breakfastseta.foodcache.profile.Profile;
import com.breakfastseta.foodcache.recipe.discover.DiscoverRecipeActivity;
import com.breakfastseta.foodcache.recipe.discover.DiscoverRecipeAdapter;
import com.breakfastseta.foodcache.recipe.viewrecipe.ViewRecipeActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SocialFriendsActivity extends AppCompatActivity {

    private static final String TAG = "FRIEND_REQ";
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private CollectionReference notebookRef = db.collection("Profiles");

    private ArrayList<String> profilesArr = new ArrayList<>();
    private ArrayList<String> friendsArr = new ArrayList<>();

    private SocialFriendsRequest adapter;
    private SocialFriendsList adapter2;

    private RecyclerView recyclerView;
    private RecyclerView recyclerViewFriendList;
    private TextView friendRequestTextView;
    private TextView friendListTextView;
    private View divider;

    String uID = App.getUID();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social_friends);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Friends");
        Util.createToolbar(this, toolbar);

        recyclerView = findViewById(R.id.friend_request_recycler_view);
        friendRequestTextView = findViewById(R.id.request_text);
        divider = findViewById(R.id.divider_friends);
        recyclerViewFriendList = findViewById(R.id.social_friend_recycler_view);
        friendListTextView = findViewById(R.id.friend_list_text);

        getData();

        getFriendListData();

    }

    // for friend list
    private void getFriendListData() {
        notebookRef.document(uID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    friendsArr.addAll((ArrayList<String>) documentSnapshot.get("friends"));
                    if (friendsArr.isEmpty()) {
                        recyclerViewFriendList.setVisibility(View.GONE);
                        friendListTextView.setVisibility(View.GONE);
                    } else {
                        recyclerViewFriendList.setVisibility(View.VISIBLE);
                        friendListTextView.setVisibility(View.VISIBLE);
                        getFriendProfileData(friendsArr);
                    }
                } else {
                    Log.d(TAG, "get failed with", task.getException());
                }
            }
        });
    }

    private void getFriendProfileData(ArrayList<String> friendsArr) {
        ArrayList<QueryDocumentSnapshot> queryDocumentSnapshotsFriendProfArr = new ArrayList<QueryDocumentSnapshot>();
        notebookRef.whereIn("uid", friendsArr).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                        queryDocumentSnapshotsFriendProfArr.add(documentSnapshot);
                    }
                    createRecyclerViewFriendList(queryDocumentSnapshotsFriendProfArr);
                }
            }
        });
    }

    private void createRecyclerViewFriendList(ArrayList<QueryDocumentSnapshot> arr) {
        adapter2 = new SocialFriendsList(arr);

        recyclerViewFriendList.setAdapter(adapter2);

        recyclerViewFriendList.setLayoutManager(new LinearLayoutManager(this));
    }

    // for Friend Requests
    private void getData() {

        notebookRef.document(uID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    profilesArr.addAll((ArrayList<String>) documentSnapshot.get("friendRequests"));
                    //Log.d(TAG, "onComplete: " + profilesArr.toString());
                    if (profilesArr.isEmpty()) {
                        recyclerView.setVisibility(View.GONE);
                        friendRequestTextView.setVisibility(View.GONE);
                        divider.setVisibility(View.GONE);
                    } else {
                        recyclerView.setVisibility(View.VISIBLE);
                        friendRequestTextView.setVisibility(View.VISIBLE);
                        divider.setVisibility(View.VISIBLE);
                        getProfileData(profilesArr);
                    }
                } else {
                    Log.d(TAG, "get failed with",  task.getException());
                }
            }
        });

    }

    private void getProfileData(ArrayList<String> profilesArr) {
        ArrayList<QueryDocumentSnapshot> queryDocumentSnapshotsProfArr = new ArrayList<QueryDocumentSnapshot>();
        notebookRef.whereIn("uid", profilesArr).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot documentSnapshots : task.getResult()) {
                        queryDocumentSnapshotsProfArr.add(documentSnapshots);
                    }
                    createRecyclerView(queryDocumentSnapshotsProfArr);
                } else {
                    Log.d(TAG, "Error getting profiles: ", task.getException());
                }
            }
        });
    }

    private void createRecyclerView(ArrayList<QueryDocumentSnapshot> arr) {

        adapter = new SocialFriendsRequest(arr);
        adapter.setAcceptFriendListener(new SocialFriendsRequest.AcceptFriendListener() {
            @Override
            public void AcceptFriend(String friendUID) {
                acceptFriendRequest(friendUID, uID);
            }
        });

        adapter.setDeclineFriendListener(new SocialFriendsRequest.DeclineFriendListener() {
            @Override
            public void DeclineFriend(String friendUID) {
                declineFriend(friendUID, uID);
            }
        });

        recyclerView.setAdapter(adapter);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

    }

    private void declineFriend(String friendUID, String uID) {
        // update current user's friend request array to remove the friend request
        notebookRef.document(uID).update("friendRequests", FieldValue.arrayRemove(friendUID));

        Toast toast = Toast.makeText(SocialFriendsActivity.this, "Friend Request Declined!", Toast.LENGTH_SHORT);
        toast.show();
    }

    private void acceptFriendRequest(String friendUID, String uID) {
        // update current user's friend Request array and friend array
        notebookRef.document(uID).update("friendRequests", FieldValue.arrayRemove(friendUID));
        notebookRef.document(uID).update("friends", FieldValue.arrayUnion(friendUID));

        // update friend's array
        notebookRef.document(friendUID).update("friends", FieldValue.arrayUnion(uID));

        Toast toast = Toast.makeText(SocialFriendsActivity.this, "Friend Request Accepted!", Toast.LENGTH_SHORT);
        toast.show();
    }
}