package com.breakfastseta.foodcache.social;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.breakfastseta.foodcache.App;
import com.breakfastseta.foodcache.R;
import com.breakfastseta.foodcache.recipe.recommend.RecommendSnippet;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class SocialFriendsRequest extends
        RecyclerView.Adapter<SocialFriendsRequest.ViewHolder>{

    private ArrayList<String> arr;

    public SocialFriendsRequest(ArrayList<String> arr) {
        this.arr = arr;
    }

    private SocialFriendsRequest.OnItemClickListener listener;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View requestView = inflater.inflate(R.layout.social_friend_request, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(requestView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Get the data model based on position
        String request = arr.get(position);

        // Set item views based on your views and data model
        TextView textView = holder.textViewName;
        ImageView imageView = holder.imageViewProfile;

        String uID = App.getUID();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference notebookRef = db.collection("Profiles").document(uID).collection("friendRequests");

//        notebookRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                if (task.isSuccessful()) {
//                    DocumentSnapshot documentSnapshot = task.getResult();
//
//                }
//            }
//        })
//
//
//        textView.setText(snippet.getName());
//        String image_url = snippet.getImage();
//        if (image_url != null) {
//            Uri image_path = Uri.parse(image_url);
//            Glide.with(holder.itemView.getContext())
//                    .load(image_path)
//                    .into(imageView);
//        }
    }

    @Override
    public int getItemCount() {
        return arr.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewName;
        ImageView imageViewProfile;

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
                        listener.onItemClick(arr.get(position));
                    }
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(String path);
    }

    public void setOnItemClickListener(SocialFriendsRequest.OnItemClickListener listener) {
        this.listener = listener;
    }
}
