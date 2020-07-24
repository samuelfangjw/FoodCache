package com.breakfastseta.foodcache.social;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.breakfastseta.foodcache.App;
import com.breakfastseta.foodcache.R;
import com.bumptech.glide.Glide;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class SocialFriendsListAdapter extends
        RecyclerView.Adapter<SocialFriendsListAdapter.ViewHolder>{

    private ArrayList<QueryDocumentSnapshot> arr;
    CollectionReference profileRef = FirebaseFirestore.getInstance().collection("Profiles");

    Context context;

    public SocialFriendsListAdapter(ArrayList<QueryDocumentSnapshot> arr, Context context) {
        this.arr = arr;
        this.context = context;
    }

    private SocialFriendsListAdapter.OnItemClickListener listener;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View requestView = inflater.inflate(R.layout.social_friend_card, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(requestView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Get the data model based on position
        QueryDocumentSnapshot request = arr.get(position);

        // Set item views based on your views and data model
        TextView textView = holder.textViewName;
        CircleImageView imageView = holder.imageViewProfile;
        TextView button = holder.button;

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //creating a popup menu
                PopupMenu popupMenu = new PopupMenu(context,  holder.button);
                //inflating menu from xml resource
                popupMenu.inflate(R.menu.friends_options_card_menu);
                //adding click listener
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_remove:
                                String friendUID = request.getString("uid");
                                String myUID = App.getUID();
                                profileRef.document(friendUID).update("friends", FieldValue.arrayRemove(myUID));
                                profileRef.document(myUID).update("friends", FieldValue.arrayRemove(friendUID));
                                break;
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });

        textView.setText(request.getString("name"));
        String image_url = request.getString("photoURL");
        if (image_url != null) {
            Uri image_path = Uri.parse(image_url);
            Glide.with(holder.itemView.getContext())
                    .load(image_path)
                    .into(imageView);
        }

    }

    @Override
    public int getItemCount() {
        return arr.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewName;
        CircleImageView imageViewProfile;
        TextView button;

        public ViewHolder(View itemView) {

            super(itemView);

            textViewName = itemView.findViewById(R.id.social_friend_name);
            imageViewProfile = itemView.findViewById(R.id.social_friend_profile_pic);
            button = itemView.findViewById(R.id.social_friend_button);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition(); // to differentiate cards
                    // check if position exists
                    if (position != RecyclerView.NO_POSITION && listener != null) {
                        listener.onItemClick(arr.get(position).getString("uid"), position);
                    }
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(String path, int position);
    }

    public void setOnItemClickListener(SocialFriendsListAdapter.OnItemClickListener listener) {
        this.listener = listener;
    }
}
