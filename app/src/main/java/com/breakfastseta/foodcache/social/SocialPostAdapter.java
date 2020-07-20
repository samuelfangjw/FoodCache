package com.breakfastseta.foodcache.social;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.breakfastseta.foodcache.R;
import com.breakfastseta.foodcache.Util;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.squareup.picasso.Picasso;

public class SocialPostAdapter extends FirestoreRecyclerAdapter<SocialPost, RecyclerView.ViewHolder> {
    private OnItemClickListener listener;
    private Context mCtx;
    private static int TYPE_REQUEST = 100;
    private static int TYPE_BLOG = 200;
    private String uID;

    public SocialPostAdapter(@NonNull FirestoreRecyclerOptions<SocialPost> options, Context mCtx, String uID) {
        super(options);
        this.mCtx = mCtx;
        this.uID = uID;
    }

    @Override
    protected void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, @NonNull SocialPost model) {
        if (holder.getClass() == SocialPostHolder.class) {
            String units = model.getUnits();
            ((SocialPostHolder) holder).textViewReqItemName.setText(model.getName());
            ((SocialPostHolder) holder).textViewReqItemDesc.setText(model.getDesc());
            ((SocialPostHolder) holder).textViewReqItemQuan.setText(Util.formatQuantityNumber(model.getNoItems(), units));
            ((SocialPostHolder) holder).textViewReqItemUnits.setText(units);
            ((SocialPostHolder) holder).textViewUsername.setText(model.getUserName());
            ((SocialPostHolder) holder).textViewDate.setText(model.getDate());
            ((SocialPostHolder) holder).buttonViewOptions.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {



                    //creating a popup menu
                    PopupMenu popupMenu = new PopupMenu(mCtx, ((SocialPostHolder) holder).buttonViewOptions);
                    //inflating menu from xml resource
                    popupMenu.inflate(R.menu.social_options_card_menu);
                    //if not user's post, hide delete button
                    if (!model.getuID().equals(uID)) {
                        MenuItem option = popupMenu.getMenu().findItem(R.id.delete_menu);
                        option.setVisible(false);
                    }
                    //adding click listener
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.delete_menu:
                                    deleteItem(position);
                                    break;
                                case R.id.edit_menu:
                                    String path = getSnapshots().getSnapshot(position).getReference().getPath();

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

            if (model.getProfileImage() != null) {
                Picasso.get().load(Uri.parse(model.getProfileImage())).into(((SocialPostHolder) holder).imageViewReqProfilePic);
            }
        } else {
            ((SocialPostHolder2) holder).textViewBlogTitle.setText(model.getName());
            ((SocialPostHolder2) holder).textViewBlogDesc.setText(model.getDesc());
            ((SocialPostHolder2) holder).textViewUsername.setText(model.getUserName());
            ((SocialPostHolder2) holder).textViewDate.setText(model.getDate());
            Picasso.get().load(model.getDownloadURL()).into(((SocialPostHolder2) holder).imageViewBlogPostImage);

            ((SocialPostHolder2) holder).buttonViewOptions.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //creating a popup menu
                    PopupMenu popupMenu = new PopupMenu(mCtx, ((SocialPostHolder2) holder).buttonViewOptions);
                    //inflating menu from xml resource
                    popupMenu.inflate(R.menu.social_options_card_menu);
                    //if not user's post, hide delete button
                    if (!model.getuID().equals(uID)) {
                        MenuItem option = popupMenu.getMenu().findItem(R.id.delete_menu);
                        option.setVisible(false);
                    }
                    //adding click listener
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.delete_menu:
                                    deleteItem(position);
                                    break;
                                case R.id.edit_menu:
                                    String path = getSnapshots().getSnapshot(position).getReference().getPath();

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

            if (model.getProfileImage() != null) {
                Picasso.get().load(Uri.parse(model.getProfileImage())).into(((SocialPostHolder2) holder).imageViewBlogProfilePic);
            }
        }
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_REQUEST) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.social_post_request,
                    parent, false);
            return new SocialPostHolder(v);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.social_post_blog,
                    parent, false);
            return new SocialPostHolder2(v);
        }
    }

    class SocialPostHolder extends RecyclerView.ViewHolder {
        TextView textViewUsername;
        TextView textViewDate;
        TextView textViewReqItemName;
        TextView textViewReqItemDesc;
        TextView textViewReqItemQuan;
        TextView textViewReqItemUnits;
        ImageView imageViewReqProfilePic;
        TextView buttonViewOptions;

        View mView;

        public SocialPostHolder(View itemView) {
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


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && listener != null) {
                        listener.onItemClick(getSnapshots().getSnapshot(position), position);
                    }
                }
            });
        }
    }

    public void deleteItem(int postPos) {
        getSnapshots().getSnapshot(postPos).getReference().delete();
    }

    class SocialPostHolder2 extends RecyclerView.ViewHolder {
        TextView textViewUsername;
        TextView textViewDate;
        TextView textViewBlogTitle;
        TextView textViewBlogDesc;
        ImageView imageViewBlogProfilePic;
        ImageView imageViewBlogPostImage;
        TextView buttonViewOptions;

        View mView;

        public SocialPostHolder2(View itemView) {
            super(itemView);
            textViewUsername = itemView.findViewById(R.id.blog_post_user_name);
            textViewBlogTitle = itemView.findViewById(R.id.blog_post_title);
            textViewBlogDesc = itemView.findViewById(R.id.blog_post_description);
            imageViewBlogProfilePic = itemView.findViewById(R.id.profile_pic_social_blog);
            textViewDate = itemView.findViewById(R.id.blog_post_date);
            imageViewBlogPostImage = itemView.findViewById(R.id.blog_post_image);
            buttonViewOptions = itemView.findViewById(R.id.blog_social_button);

            mView = itemView;


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && listener != null) {
                        listener.onItemClick(getSnapshots().getSnapshot(position), position);
                    }
                }
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (this.getItem(position).getType() == SocialPostType.REQUESTPOST) {
            return TYPE_REQUEST;
        } else {
            return TYPE_BLOG;
        }
    }

    public interface OnItemClickListener {
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }

    public void setOnItemClickListener(SocialPostAdapter.OnItemClickListener listener) {
        this.listener = listener;
    }
}
