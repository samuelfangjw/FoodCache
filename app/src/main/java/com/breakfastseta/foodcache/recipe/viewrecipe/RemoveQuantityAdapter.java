package com.breakfastseta.foodcache.recipe.viewrecipe;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.breakfastseta.foodcache.R;
import com.breakfastseta.foodcache.Util;

import java.util.ArrayList;
import java.util.Map;

public class RemoveQuantityAdapter extends
        RecyclerView.Adapter<RemoveQuantityAdapter.ViewHolder>{

    private ArrayList<String> keys;
    private Map<String, RemoveQuantityActivity.ExistingIngredientSnippet> map;
    Context context;

    public RemoveQuantityAdapter(ArrayList<String> keys, Map<String, RemoveQuantityActivity.ExistingIngredientSnippet> map, Context context) {
        this.keys = keys;
        this.map = map;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View view = inflater.inflate(R.layout.item_remove_quantity, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String key = keys.get(position); //name
        RemoveQuantityActivity.ExistingIngredientSnippet snippet = map.get(key);
        String units = snippet.getUnits();
        Double quantityUsed = snippet.getQuantityUsed();
        Map<String, String> pathMap = snippet.getPathMap();
        Map<String, Double> quantityMap = snippet.getQuantityMap();

        TextView name = holder.name;
        TextView totalQuantity = holder.totalQuantity;
        LinearLayout locations = holder.locations;

        // Set textviews
        name.setText(key);
        totalQuantity.setText(Util.formatQuantity(quantityUsed, units));

        // Generate textviews
        for (String location : quantityMap.keySet()) {
            Double quantity = quantityMap.get(location);

            View ingredientLayout = LayoutInflater.from(context).inflate(R.layout.item_remove_quantity_ingredient, null);

            TextView locationTV = ingredientLayout.findViewById(R.id.location);
            TextView quantityBeforeTV = ingredientLayout.findViewById(R.id.quantityBefore);
            EditText quantityAfterTV = ingredientLayout.findViewById(R.id.quantityAfter);

            locationTV.setText(location);
            quantityBeforeTV.setText(Util.formatQuantity(quantity, units));
            quantityAfterTV.setText(Util.formatQuantity(0, units));

            locations.addView(ingredientLayout);
        }
    }

    @Override
    public int getItemCount() {
        return keys.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public TextView totalQuantity;
        public LinearLayout locations;

        public ViewHolder(View itemView) {

            super(itemView);

            name = (TextView) itemView.findViewById(R.id.name);
            totalQuantity = (TextView) itemView.findViewById(R.id.totalQuantity);
            locations = (LinearLayout) itemView.findViewById(R.id.locations);
        }
    }
}
