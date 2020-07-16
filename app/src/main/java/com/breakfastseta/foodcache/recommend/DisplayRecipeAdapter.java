package com.breakfastseta.foodcache.recommend;

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

public class DisplayRecipeAdapter extends
        RecyclerView.Adapter<DisplayRecipeAdapter.ViewHolder>{

    private ArrayList<RecommendSnippet> arr;

    public DisplayRecipeAdapter(ArrayList<RecommendSnippet> arr) {
        this.arr = arr;
    }

    private DisplayRecipeAdapter.OnItemClickListener listener;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View recipeView = inflater.inflate(R.layout.item_, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(recipeView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Get the data model based on position
        RecommendSnippet snippet = arr.get(position);

        // Set item views based on your views and data model
        TextView textView = holder.textViewName;
        ImageView imageView = holder.imageView;


        textView.setText(snippet.getName());
        String image_url = snippet.getImage();
        if (image_url != null) {
            //TODO explore glide placeholders and fallback for image
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
        ImageView imageView;

        public ViewHolder(View itemView) {

            super(itemView);

            textViewName = itemView.findViewById(R.id.recipe_name);
            imageView = itemView.findViewById(R.id.recipe_image);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition(); // to differentiate cards
                    // check if position exists
                    if (position != RecyclerView.NO_POSITION && listener != null) {
                        listener.onItemClick(arr.get(position).getPath());
                    }
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(String path);
    }

    public void setOnItemClickListener(DisplayRecipeAdapter.OnItemClickListener listener) {
        this.listener = listener;
    }
}
