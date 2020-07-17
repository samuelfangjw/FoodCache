package com.breakfastseta.foodcache.social;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.breakfastseta.foodcache.R;
import com.breakfastseta.foodcache.Util;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.squareup.picasso.Picasso;

public class SocialPostAdapter extends FirestoreRecyclerAdapter<SocialPost, RecyclerView.ViewHolder> {
    private OnItemClickListener listener;
    private static int TYPE_REQUEST = 100;
    private static int TYPE_BLOG = 200;

    public SocialPostAdapter(@NonNull FirestoreRecyclerOptions<SocialPost> options) {
        super(options);
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

            if (model.getProfileImage() != null) {
                Picasso.get().load(model.getProfileImage()).into(((SocialPostHolder) holder).imageViewReqProfilePic);
            }
        } else {
            ((SocialPostHolder2) holder).textViewBlogTitle.setText(model.getName());
            ((SocialPostHolder2) holder).textViewBlogDesc.setText(model.getDesc());
            ((SocialPostHolder2) holder).textViewUsername.setText(model.getUserName());
            ((SocialPostHolder2) holder).textViewDate.setText(model.getDate());
            Picasso.get().load(model.getDownloadURL()).into(((SocialPostHolder2) holder).imageViewBlogPostImage);

            if (model.getProfileImage() != null) {
                Picasso.get().load(model.getProfileImage()).into(((SocialPostHolder2) holder).imageViewBlogProfilePic);
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

    class SocialPostHolder2 extends RecyclerView.ViewHolder {
        TextView textViewUsername;
        TextView textViewDate;
        TextView textViewBlogTitle;
        TextView textViewBlogDesc;
        ImageView imageViewBlogProfilePic;
        ImageView imageViewBlogPostImage;

        View mView;

        public SocialPostHolder2(View itemView) {
            super(itemView);
            textViewUsername = itemView.findViewById(R.id.blog_post_user_name);
            textViewBlogTitle = itemView.findViewById(R.id.blog_post_title);
            textViewBlogDesc = itemView.findViewById(R.id.blog_post_description);
            imageViewBlogProfilePic = itemView.findViewById(R.id.profile_pic_social_blog);
            textViewDate = itemView.findViewById(R.id.blog_post_date);
            imageViewBlogPostImage = itemView.findViewById(R.id.blog_post_image);

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
