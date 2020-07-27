package com.breakfastseta.foodcache.social;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.breakfastseta.foodcache.App;
import com.breakfastseta.foodcache.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class SocialFriendRequest extends AppCompatActivity {
    private static final String TAG = "SocialFriendRequest";

    private SocialFriendsRequestAdapter adapter;
    private ArrayList<String> profilesArr = new ArrayList<>();

    private RecyclerView recyclerView;
    private TextView message;

    private String uID = App.getUID();

    ArrayList<QueryDocumentSnapshot> queryDocumentSnapshotsProfArr = new ArrayList<QueryDocumentSnapshot>();

    private CollectionReference notebookRef = FirebaseFirestore.getInstance().collection("Profiles");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social_friend_request);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Friend Requests");
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_black);

        recyclerView = findViewById(R.id.friend_request_recycler_view);
        message = findViewById(R.id.message);

        getData();
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

    // for Friend Requests
    private void getData() {
        notebookRef.document(uID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    ArrayList<String> arr = (ArrayList<String>) documentSnapshot.get("friendRequests");
                    if (arr != null) {
                        profilesArr.addAll(arr);
                    }

                    if (profilesArr.isEmpty()) {
                        message.setVisibility(View.VISIBLE);
                    } else {
                        getProfileData(profilesArr);
                    }
                } else {
                    Log.d(TAG, "get failed with",  task.getException());
                }
            }
        });
    }

    private void getProfileData(ArrayList<String> profilesArr) {
        notebookRef.whereIn("uid", profilesArr).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot documentSnapshots : task.getResult()) {
                        queryDocumentSnapshotsProfArr.add(documentSnapshots);
                    }
                    createRecyclerView();
                } else {
                    Log.d(TAG, "Error getting profiles: ", task.getException());
                }
            }
        });
    }

    private void createRecyclerView() {

        adapter = new SocialFriendsRequestAdapter(queryDocumentSnapshotsProfArr);
        adapter.setOnItemClickListener(new SocialFriendsRequestAdapter.OnItemClickListener() {
            @Override
            public void AcceptFriend(String friendUID) {
                acceptFriendRequest(friendUID, uID);
                if (queryDocumentSnapshotsProfArr.isEmpty()) {
                    message.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void DeclineFriend(String friendUID) {
                declineFriend(friendUID, uID);
                if (queryDocumentSnapshotsProfArr.isEmpty()) {
                    message.setVisibility(View.VISIBLE);
                }
            }
        });

        recyclerView.setAdapter(adapter);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

    }

    private void declineFriend(String friendUID, String uID) {
        // update current user's friend request array to remove the friend request
        notebookRef.document(uID).update("friendRequests", FieldValue.arrayRemove(friendUID));

        Toast toast = Toast.makeText(SocialFriendRequest.this, "Friend Request Declined!", Toast.LENGTH_SHORT);
        toast.show();
    }

    private void acceptFriendRequest(String friendUID, String uID) {
        // update current user's friend Request array and friend array
        notebookRef.document(uID).update("friendRequests", FieldValue.arrayRemove(friendUID));
        notebookRef.document(uID).update("friends", FieldValue.arrayUnion(friendUID));

        // update friend's array
        notebookRef.document(friendUID).update("friends", FieldValue.arrayUnion(uID));

        CollectionReference postRef = FirebaseFirestore.getInstance().collection("SocialPosts");
        CollectionReference socialUsersRef = FirebaseFirestore.getInstance().collection("SocialUsers").document(uID).collection("posts");
        CollectionReference friendsSocialUsersRef = FirebaseFirestore.getInstance().collection("SocialUsers").document(friendUID).collection("posts");

        postRef.whereEqualTo("uID", friendUID).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot snapshots) {
                Log.d(TAG, "onSuccess: middle" + snapshots.size());
                for (DocumentSnapshot snapshot : snapshots) {
                    Log.d(TAG, "onSuccess: after");
                    String date = snapshot.getString("date");
                    String path = snapshot.getReference().getPath();
                    String time = snapshot.getString("time");
                    String type = snapshot.getString("type");

                    SocialPostType typeEnum = type.equals("REQUESTPOST") ? SocialPostType.REQUESTPOST : SocialPostType.BLOGPOST;

                    SocialPostSnippet snippet = new SocialPostSnippet(date, time, path, typeEnum, friendUID);

                    socialUsersRef.add(snippet);
                }
            }
        });

        postRef.whereEqualTo("uID", uID).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot snapshots) {
                Log.d(TAG, "onSuccess: middle" + snapshots.size());
                for (DocumentSnapshot snapshot : snapshots) {
                    Log.d(TAG, "onSuccess: after");
                    String date = snapshot.getString("date");
                    String path = snapshot.getReference().getPath();
                    String time = snapshot.getString("time");
                    String type = snapshot.getString("type");

                    SocialPostType typeEnum = type.equals("REQUESTPOST") ? SocialPostType.REQUESTPOST : SocialPostType.BLOGPOST;

                    SocialPostSnippet snippet = new SocialPostSnippet(date, time, path, typeEnum, uID);

                    friendsSocialUsersRef.add(snippet);
                }
            }
        });

        App.getProfile().getFriends().add(friendUID);

        Toast toast = Toast.makeText(SocialFriendRequest.this, "Friend Request Accepted!", Toast.LENGTH_SHORT);
        toast.show();
    }
}