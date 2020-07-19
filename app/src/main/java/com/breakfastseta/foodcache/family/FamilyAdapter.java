package com.breakfastseta.foodcache.family;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.breakfastseta.foodcache.App;
import com.breakfastseta.foodcache.R;
import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FamilyAdapter extends
        RecyclerView.Adapter<FamilyAdapter.ViewHolder>{

    public ArrayList<String> members;
    public Map<String, Boolean> memberStatus;
    Context context;
    String docPath;
    String ownerUID;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public FamilyAdapter(ArrayList<String> members, Map<String, Boolean> memberStatus, Context context, String docPath, String ownerUID) {
        this.members = members;
        this.memberStatus = memberStatus;
        this.context = context;
        this.docPath = docPath;
        this.ownerUID = ownerUID;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View view = inflater.inflate(R.layout.item_family, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String key = members.get(position); //name
        Boolean isVerified = memberStatus.get(key);

        TextView nameView = holder.name;
        TextView request = holder.request;
        ImageView imageView = holder.imageView;
        Button acceptButton = holder.acceptButton;
        Button declineButton = holder.declineButton;
        Button removeButton = holder.removeButton;

        if (!ownerUID.equals(App.getUID())) {
            request.setVisibility(View.GONE);
            acceptButton.setVisibility(View.GONE);
            declineButton.setVisibility(View.GONE);
            removeButton.setVisibility(View.GONE);
        } else if (!isVerified) {
            request.setVisibility(View.VISIBLE);
            acceptButton.setVisibility(View.VISIBLE);
            declineButton.setVisibility(View.VISIBLE);
            removeButton.setVisibility(View.GONE);
        } else {
            removeButton.setVisibility(View.VISIBLE);
            request.setVisibility(View.GONE);
            acceptButton.setVisibility(View.GONE);
            declineButton.setVisibility(View.GONE);
        }

        FirebaseFirestore.getInstance()
                .collection("Profiles")
                .whereEqualTo("uid", key)
                .limit(1)
                .get()
                .addOnSuccessListener(snapshots -> {
                    for (DocumentSnapshot d : snapshots) {
                        String photoURL = d.getString("photoURL");
                        String name = d.getString("name");
                        Uri photo = null;
                        String profileUID = d.getString("uid");

                        if (profileUID.equals(App.getUID())) {
                            removeButton.setVisibility(View.GONE);
                        }

                        nameView.setText(name);

                        if (photoURL != null) {
                            photo = Uri.parse(photoURL);
                        }

                        Glide.with(context)
                                .load(photo)
                                .placeholder(R.drawable.ic_profile_pic)
                                .fallback(R.drawable.ic_profile_pic)
                                .into(imageView);

                        if (isVerified) {
                            removeButton.setOnClickListener(v -> {
                                Map<String, Object> updates = new HashMap<>();
                                memberStatus.remove(key);
                                members.remove(key);
                                updates.put("memberStatus", memberStatus);
                                updates.put("members", members);
                                db.document(docPath).update(updates);
                            });
                        } else {
                            acceptButton.setOnClickListener(v -> {
                                memberStatus.remove(key);
                                memberStatus.put(key, true);
                                db.document(docPath).update("memberStatus", memberStatus);
                            });

                            declineButton.setOnClickListener(v -> {
                                Map<String, Object> updates = new HashMap<>();
                                memberStatus.remove(key);
                                members.remove(key);
                                updates.put("memberStatus", memberStatus);
                                updates.put("members", members);
                                db.document(docPath).update(updates);

                                Map<String, Object> profileUpdates = new HashMap<>();
                                profileUpdates.put("useFamilySharing", false);
                                profileUpdates.put("familyUID", null);
                                db.collection("Profiles").document(key).update(profileUpdates);
                            });
                        }
                    }
                });
    }

    @Override
    public int getItemCount() {
        return members.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public TextView request;
        public ImageView imageView;
        public Button acceptButton;
        public Button declineButton;
        public Button removeButton;

        public ViewHolder(View itemView) {

            super(itemView);

            name = (TextView) itemView.findViewById(R.id.name);
            request = (TextView) itemView.findViewById(R.id.request);
            imageView = (ImageView) itemView.findViewById(R.id.picture);
            acceptButton = (Button) itemView.findViewById(R.id.accept);
            declineButton = (Button) itemView.findViewById(R.id.decline);
            removeButton = (Button) itemView.findViewById(R.id.remove);
        }
    }

}
