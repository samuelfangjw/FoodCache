package com.breakfastseta.foodcache.recipe;

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
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

public class RecipeAdapter extends FirestoreRecyclerAdapter<RecipeSnippet, RecipeAdapter.RecipeHolder> {
    private static final String TAG = "RecipeAdapter";

    private OnItemClickListener listener;

    public RecipeAdapter(@NonNull FirestoreRecyclerOptions<RecipeSnippet> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull final RecipeHolder holder, int position, @NonNull RecipeSnippet model) {
        String name = model.getName();
        String image_url = model.getImage();
        String description = model.getDescription();

        //Set Views
        holder.textViewName.setText(name);
        holder.description.setText(description);

        if (image_url != null) {
            Uri image_path = Uri.parse(image_url);
            Glide.with(holder.itemView.getContext())
                    .load(image_path)
                    .into(holder.imageView);
        }
    }

    @NonNull
    @Override
    public RecipeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recipe, parent, false);
        return new RecipeHolder(v);
    }

    class RecipeHolder extends RecyclerView.ViewHolder {
        TextView textViewName;
        ImageView imageView;
        TextView description;

        public RecipeHolder(@NonNull View recipeView) {
            super(recipeView);
            textViewName = recipeView.findViewById(R.id.recipe_name);
            imageView = recipeView.findViewById(R.id.recipe_image);
            description = recipeView.findViewById(R.id.recipe_description);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition(); // to differentiate cards
                    // check if position exists
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

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}

