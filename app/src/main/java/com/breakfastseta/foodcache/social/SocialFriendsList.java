package com.breakfastseta.foodcache.social;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.breakfastseta.foodcache.App;
import com.breakfastseta.foodcache.R;
import com.bumptech.glide.Glide;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class SocialFriendsList extends
        RecyclerView.Adapter<SocialFriendsList.ViewHolder>{

    private ArrayList<QueryDocumentSnapshot> arr;


    public SocialFriendsList(ArrayList<QueryDocumentSnapshot> arr) {
        this.arr = arr;
    }

    private SocialFriendsList.OnItemClickListener listener;

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

        String uID = App.getUID();


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

        public ViewHolder(View itemView) {

            super(itemView);

            textViewName = itemView.findViewById(R.id.social_friend_name);
            imageViewProfile = itemView.findViewById(R.id.social_friend_profile_pic);

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

    public void setOnItemClickListener(SocialFriendsList.OnItemClickListener listener) {
        this.listener = listener;
    }
}
