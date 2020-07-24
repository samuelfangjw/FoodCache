package com.breakfastseta.foodcache.recipe.discover;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.breakfastseta.foodcache.R;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class DiscoverRecipeAdapter extends RecyclerView.Adapter<DiscoverRecipeAdapter.ViewHolder>{
    private static final String TAG = "DiscoverRecipeAdapter";

    private ArrayList<DiscoverSnippet> arr;
    private OnItemClickListener listener;

    public DiscoverRecipeAdapter(ArrayList<DiscoverSnippet> arr) {
        this.arr = arr;
    }

    @NonNull
    @Override
    public DiscoverRecipeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View recipeView = inflater.inflate(R.layout.item_recipe, parent, false);

        // Return a new holder instance
        DiscoverRecipeAdapter.ViewHolder viewHolder = new DiscoverRecipeAdapter.ViewHolder(recipeView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull DiscoverRecipeAdapter.ViewHolder holder, int position) {
        // Get the data model based on position
        DiscoverSnippet snippet = arr.get(position);

        // Set item views based on your views and data model
        ImageView imageView = holder.imageView;
        TextView textView = holder.textView;
        TextView descriptionTV = holder.descriptionTV;

        textView.setText(snippet.getName());
        descriptionTV.setText(snippet.getDescription());
        String image_url = snippet.getImage();
        if (image_url != null) {
            Uri image_path = Uri.parse(image_url);
            Glide.with(holder.itemView.getContext())
                    .load(image_path)
                    .into(imageView);
        } else {
            holder.imageView.setImageDrawable(holder.itemView.getContext().getDrawable(R.drawable.ic_fastfood));
        }

        holder.path = snippet.getPath();
    }

    @Override
    public int getItemCount() {
        return arr.size();
    }

    public void filterList(ArrayList<DiscoverSnippet> filteredArr) {
        if (arr != filteredArr) {
            arr = filteredArr;
            notifyDataSetChanged();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView textView;
        public TextView descriptionTV;
        public String path;

        public ViewHolder(View itemView) {

            super(itemView);

            imageView = (ImageView) itemView.findViewById(R.id.recipe_image);
            textView = (TextView) itemView.findViewById(R.id.recipe_name);
            descriptionTV = (TextView) itemView.findViewById(R.id.recipe_description);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition(); // to differentiate cards
                    // check if position exists
                    if (position != RecyclerView.NO_POSITION && listener != null) {
                        listener.onItemClick(path, position);
                    }
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(String path, int position);
    }

    public void setOnItemClickListener(DiscoverRecipeAdapter.OnItemClickListener listener) {
        this.listener = listener;
    }}
