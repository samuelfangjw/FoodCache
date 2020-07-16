package com.breakfastseta.foodcache.recipe.addrecipe;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.breakfastseta.foodcache.R;

import java.util.ArrayList;

public class FragmentThreeAdapter extends
        RecyclerView.Adapter<FragmentThreeAdapter.ViewHolder>{

    private ArrayList<String> arr;

    public FragmentThreeAdapter(ArrayList<String> arr) {
        this.arr = arr;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View stepsView = inflater.inflate(R.layout.fragment_three_item, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(stepsView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Get the data model based on position
        String step = arr.get(position);

        // Set item views based on your views and data model
        TextView textView = holder.stepsTextView;
        textView.setText(step);
    }

    @Override
    public int getItemCount() {
        return arr.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView stepsTextView;

        public ViewHolder(View itemView) {

            super(itemView);

            stepsTextView = itemView.findViewById(R.id.ingredient);
        }
    }
}
