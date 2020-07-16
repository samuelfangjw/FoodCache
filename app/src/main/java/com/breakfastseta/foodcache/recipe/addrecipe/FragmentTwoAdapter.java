package com.breakfastseta.foodcache.recipe.addrecipe;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.breakfastseta.foodcache.R;
import com.breakfastseta.foodcache.recipe.Ingredient;

import java.util.ArrayList;

public class FragmentTwoAdapter extends
        RecyclerView.Adapter<FragmentTwoAdapter.ViewHolder>{

    private ArrayList<Ingredient> arr;

    public FragmentTwoAdapter(ArrayList<Ingredient> arr) {
        this.arr = arr;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View ingredientView = inflater.inflate(R.layout.fragment_two_item, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(ingredientView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Get the data model based on position
        Ingredient ingredient = arr.get(position);

        // Set item views based on your views and data model
        TextView textView = holder.ingredientTextView;
        textView.setText(ingredient.toString());
    }

    @Override
    public int getItemCount() {
        return arr.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView ingredientTextView;

        public ViewHolder(View itemView) {

            super(itemView);

            ingredientTextView = (TextView) itemView.findViewById(R.id.ingredient);
        }
    }
}
