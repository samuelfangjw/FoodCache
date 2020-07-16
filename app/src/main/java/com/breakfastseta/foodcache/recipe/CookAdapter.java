package com.breakfastseta.foodcache.recipe;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.breakfastseta.foodcache.R;

import java.util.ArrayList;

public class CookAdapter extends
        RecyclerView.Adapter<CookAdapter.ViewHolder>{

    private ArrayList<String> arr;

    public CookAdapter(ArrayList<String> arr) {
        this.arr = arr;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View view = inflater.inflate(R.layout.steps, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Get the data model based on position
        String step = arr.get(position);

        // Set item views based on your views and data model
        TextView stepNumber = holder.stepNumber;
        TextView stepView = holder.stepView;

        stepNumber.setText("Step " + (position + 1) + ":");
        stepView.setText(arr.get(position));
    }

    @Override
    public int getItemCount() {
        return arr.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView stepNumber;
        public TextView stepView;

        public ViewHolder(View itemView) {

            super(itemView);

            stepNumber = (TextView) itemView.findViewById(R.id.step_number);
            stepView = (TextView) itemView.findViewById(R.id.step);
        }
    }
}
