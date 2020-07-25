package com.breakfastseta.foodcache.social;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.breakfastseta.foodcache.App;
import com.breakfastseta.foodcache.R;
import com.breakfastseta.foodcache.profile.Profile;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class SocialFindFriendAdapter extends FirestoreRecyclerAdapter<Profile, SocialFindFriendAdapter.SocialFindFriendHolder> {
    private Context mCtx;
    private String uID;
    private ArrayList<String> friends;

    private OnItemClickListener listener;
    private AddFriendListener listener2;

    public SocialFindFriendAdapter(@NonNull FirestoreRecyclerOptions<Profile> options, Context mCtx, String uID) {
        super(options);
        this.mCtx = mCtx;
        this.uID = uID;

        friends = App.getProfile().getFriends();
    }

    @NonNull
    @Override
    public SocialFindFriendHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.social_find_friend,
                parent, false);
        return new SocialFindFriendHolder(v);
    }

    @Override
    protected void onBindViewHolder(@NonNull SocialFindFriendAdapter.SocialFindFriendHolder holder, int position, @NonNull Profile model) {
        holder.textViewUsername.setText(model.getName());

        if (uID.equals(model.getUID()) || friends.contains(model.getUID())) {
            holder.imageButtonAddFriend.setVisibility(View.GONE);
        }

        if (model.getPhotoURL() != null) {
            Picasso.get().load((Uri.parse(model.getPhotoURL()))).into(holder.imageViewProfilePic);
        } else {
            holder.imageViewProfilePic.setImageDrawable(ContextCompat.getDrawable(mCtx, R.drawable.ic_profile_pic));
        }
        holder.imageButtonAddFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener2 != null) {
                    listener2.addfriend(model.getUID());
                }
            }
        });
    }

    public interface AddFriendListener {
        void addfriend(String uID);
    }

    public void setAddFriendListener(AddFriendListener listener2) {
        this.listener2 = listener2;
    }


    class SocialFindFriendHolder extends RecyclerView.ViewHolder {
        TextView textViewUsername;
        CircleImageView imageViewProfilePic;
        ImageButton imageButtonAddFriend;

        View mView;

        public SocialFindFriendHolder(View itemView) {
            super(itemView);
            textViewUsername = itemView.findViewById(R.id.find_friend_name);
            imageViewProfilePic = itemView.findViewById(R.id.find_friend_profile_pic);
            imageButtonAddFriend = itemView.findViewById(R.id.find_friend_add_button);

            mView = itemView;

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View V) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && listener != null) {
                        listener.onItemClick(getSnapshots().getSnapshot(position), position);
                    }
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }

    public void setOnItemClickListener(SocialFindFriendAdapter.OnItemClickListener listener) {
        this.listener = listener;
    }
}
