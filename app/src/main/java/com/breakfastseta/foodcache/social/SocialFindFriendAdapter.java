package com.breakfastseta.foodcache.social;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.breakfastseta.foodcache.R;
import com.breakfastseta.foodcache.profile.Profile;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.squareup.picasso.Picasso;

public class SocialFindFriendAdapter extends FirestoreRecyclerAdapter<Profile, SocialFindFriendAdapter.SocialFindFriendHolder> {
    private Context mCtx;

    private OnItemClickListener listener;

    public SocialFindFriendAdapter(@NonNull FirestoreRecyclerOptions<Profile> options, Context mCtx) {
        super(options);
        this.mCtx = mCtx;
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

        if (model.getPhotoURL() != null) {
            Picasso.get().load((Uri.parse(model.getPhotoURL()))).into(holder.imageViewProfilePic);
        }
    }

    class SocialFindFriendHolder extends RecyclerView.ViewHolder {
        TextView textViewUsername;
        ImageView imageViewProfilePic;
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
