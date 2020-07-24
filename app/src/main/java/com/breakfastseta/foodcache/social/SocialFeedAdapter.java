package com.breakfastseta.foodcache.social;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.breakfastseta.foodcache.App;
import com.breakfastseta.foodcache.R;
import com.breakfastseta.foodcache.Util;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class SocialFeedAdapter extends
        RecyclerView.Adapter<SocialFeedAdapter.ViewHolder>{

    private static final String TAG = "SocialFeedAdapter";
    private ArrayList<QueryDocumentSnapshot> arr;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private static int TYPE_REQUEST = 100;
    private static int TYPE_BLOG = 200;
    private DeleteItemListener listenerDelete;
    private Context mCtx;

    private String uid = App.getUID();


    public SocialFeedAdapter(ArrayList<QueryDocumentSnapshot> arr, Context mCtx) {
        this.arr = arr;
        this.mCtx = mCtx;
    }

    private SocialFeedAdapter.OnItemClickListener listener;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        if (viewType == TYPE_REQUEST) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.social_post_request,
                    parent, false);
            return new SocialFeedHolder(v);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.social_post_blog,
                    parent, false);
            return new SocialFeedHolder2(v);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        // Get the data model based on position
        QueryDocumentSnapshot base = arr.get(position);

        String path = base.getString("path");

        db.document(path).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot request = task.getResult();


                    // Set item views based on your views and data model
                    if (holder.getClass() == SocialFeedHolder.class) {
                        String units = request.getString("units");
                        Object quant = request.get("noItems");
                        if (quant != null) {
                            ((SocialFeedHolder) holder).textViewReqItemQuan.setText(Util.formatQuantityNumber((Double) request.get("noItems"), units));
                        }
                        ((SocialFeedHolder) holder).textViewReqItemName.setText(request.getString("name"));
                        ((SocialFeedHolder) holder).textViewReqItemDesc.setText(request.getString("desc"));
                        ((SocialFeedHolder) holder).textViewReqItemUnits.setText(units);
                        ((SocialFeedHolder) holder).textViewUsername.setText(request.getString("userName"));
                        ((SocialFeedHolder) holder).textViewDate.setText(request.getString("date"));
                        ((SocialFeedHolder) holder).buttonViewOptions.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                //creating a popup menu
                                PopupMenu popupMenu = new PopupMenu(mCtx, ((SocialFeedHolder) holder).buttonViewOptions);
                                //inflating menu from xml resource
                                popupMenu.inflate(R.menu.social_options_card_menu);
                                //if not user's post, hide delete button
                                if (!request.getString("uID").equals(uid)) {
                                    MenuItem option = popupMenu.getMenu().findItem(R.id.delete_menu);
                                    option.setVisible(false);
                                }
                                //adding click listener
                                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                    @Override
                                    public boolean onMenuItemClick(MenuItem item) {
                                        switch (item.getItemId()) {
                                            case R.id.delete_menu:
                                                deleteItem(path);
                                                break;
                                            case R.id.edit_menu:
                                                Intent intent = new Intent(mCtx, EditSocialPost.class);
                                                intent.putExtra("path", path);
                                                mCtx.startActivity(intent);
                                                break;
                                        }
                                        return false;
                                    }
                                });
                                popupMenu.show();
                            }
                        });

                        if (request.getString("profileImage") != null) {
                            Picasso.get().load(Uri.parse(request.getString("profileImage"))).into(((SocialFeedHolder) holder).imageViewReqProfilePic);
                        }
                    } else {
                        ((SocialFeedHolder2) holder).textViewBlogTitle.setText(request.getString("name"));
                        ((SocialFeedHolder2) holder).textViewBlogDesc.setText(request.getString("desc"));
                        ((SocialFeedHolder2) holder).textViewUsername.setText(request.getString("userName"));
                        ((SocialFeedHolder2) holder).textViewDate.setText(request.getString("date"));
                        Picasso.get().load(request.getString("downloadURL")).into(((SocialFeedHolder2) holder).imageViewBlogPostImage);

                        if (request.getString("profileImage") != null) {
                            Picasso.get().load(Uri.parse(request.getString("profileImage"))).into(((SocialFeedHolder2) holder).imageViewBlogProfilePic);
                        }

                        ((SocialFeedHolder2) holder).buttonViewOptions.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                //creating a popup menu
                                PopupMenu popupMenu = new PopupMenu(mCtx, ((SocialFeedHolder2) holder).buttonViewOptions);
                                //inflating menu from xml resource
                                popupMenu.inflate(R.menu.social_options_card_menu);
                                //if not user's post, hide delete button
                                if (!request.getString("uID").equals(uid)) {
                                    MenuItem option = popupMenu.getMenu().findItem(R.id.delete_menu);
                                    option.setVisible(false);
                                }
                                //adding click listener
                                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                    @Override
                                    public boolean onMenuItemClick(MenuItem item) {
                                        switch (item.getItemId()) {
                                            case R.id.delete_menu:
                                                deleteItem(path);
                                                break;
                                            case R.id.edit_menu:
                                                Intent intent = new Intent(mCtx, EditSocialPost.class);
                                                intent.putExtra("path", path);
                                                mCtx.startActivity(intent);
                                                break;
                                        }
                                        return false;
                                    }
                                });
                                popupMenu.show();
                            }
                        });
                    }
                } else {
                    Log.d(TAG, "onComplete Failed to get post with ", task.getException());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return arr.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewName;
        CircleImageView imageViewProfile;
        Button acceptButton;
        Button declineButton;

        public ViewHolder(View itemView) {

            super(itemView);

            textViewName = itemView.findViewById(R.id.social_friend_request_name);
            imageViewProfile = itemView.findViewById(R.id.social_friend_request_profile_pic);
            acceptButton = itemView.findViewById(R.id.accept_button);
            declineButton = itemView.findViewById(R.id.decline_button);

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

    @Override
    public int getItemViewType(int position) {
        if (this.arr.get(position).get("type").equals(SocialPostType.REQUESTPOST.toString())) {
            return TYPE_REQUEST;
        } else {
            return TYPE_BLOG;
        }
    }

    //add to main activity
    public interface DeleteItemListener {
        void deleteItemListener();
    }

    public void setDeleteItemListener(DeleteItemListener listenerDelete) {
        this.listenerDelete = listenerDelete;
    }

    public void deleteItem(String path) {
        //delete from SocialPost
        db.document(path).delete();

        //delete from SocialUsers
        db.collectionGroup("posts").whereEqualTo("path", path).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (!task.getResult().isEmpty()) {
                                for (DocumentSnapshot documentSnapshot : task.getResult()) {
                                    documentSnapshot.getReference().delete();
                                }

                            }
                        } else {
                            Log.d(TAG, "onComplete failed to delete SocialUsers with ", task.getException());
                        }
                    }
                });


    }


    public interface OnItemClickListener {
        void onItemClick(String path, int position);
    }

    public void setOnItemClickListener(SocialFeedAdapter.OnItemClickListener listener) {
        this.listener = listener;
    }

    private class SocialFeedHolder extends ViewHolder {
        TextView textViewUsername;
        TextView textViewDate;
        TextView textViewReqItemName;
        TextView textViewReqItemDesc;
        TextView textViewReqItemQuan;
        TextView textViewReqItemUnits;
        CircleImageView imageViewReqProfilePic;
        TextView buttonViewOptions;

        View mView;

        public SocialFeedHolder(View itemView) {
            super(itemView);
            textViewUsername = itemView.findViewById(R.id.social_user_name);
            textViewReqItemName = itemView.findViewById(R.id.request_item_name);
            textViewReqItemDesc = itemView.findViewById(R.id.request_item_description);
            textViewReqItemQuan = itemView.findViewById(R.id.request_item_quantity);
            textViewReqItemUnits = itemView.findViewById(R.id.request_item_units);
            imageViewReqProfilePic = itemView.findViewById(R.id.profile_pic_social);
            textViewDate = itemView.findViewById(R.id.request_date);
            buttonViewOptions = itemView.findViewById(R.id.request_social_button);

            mView = itemView;

            //setOnClickListener
        }
    }

    private class SocialFeedHolder2 extends ViewHolder {
        TextView textViewUsername;
        TextView textViewDate;
        TextView textViewBlogTitle;
        TextView textViewBlogDesc;
        CircleImageView imageViewBlogProfilePic;
        ImageView imageViewBlogPostImage;
        TextView buttonViewOptions;

        View mView;

        public SocialFeedHolder2(View itemView) {
            super(itemView);
            textViewUsername = itemView.findViewById(R.id.blog_post_user_name);
            textViewBlogTitle = itemView.findViewById(R.id.blog_post_title);
            textViewBlogDesc = itemView.findViewById(R.id.blog_post_description);
            imageViewBlogProfilePic = itemView.findViewById(R.id.profile_pic_social_blog);
            textViewDate = itemView.findViewById(R.id.blog_post_date);
            imageViewBlogPostImage = itemView.findViewById(R.id.blog_post_image);
            buttonViewOptions = itemView.findViewById(R.id.blog_social_button);

            mView = itemView;

            //setOnClickListener
        }
    }
}
