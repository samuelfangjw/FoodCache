package com.breakfastseta.foodcache;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class ShoppingListAdapter extends FirestoreRecyclerAdapter<ShoppingListItem, ShoppingListAdapter.ShoppingListHolder> {

    public ShoppingListAdapter(@NonNull FirestoreRecyclerOptions<ShoppingListItem> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ShoppingListHolder holder, int position, @NonNull ShoppingListItem model) {
        holder.textViewTitle.setText(model.getItemName());
        holder.textViewDescription.setText(model.getDescription());
        holder.textViewPriority.setText(String.valueOf(model.getPriority()));
        holder.textViewQuantity.setText(String.valueOf(model.getNoItems()));
    }

    @NonNull
    @Override
    public ShoppingListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.shopping_item,
                parent, false);
        return new ShoppingListHolder(v);
    }

    class ShoppingListHolder extends RecyclerView.ViewHolder {
        TextView textViewTitle;
        TextView textViewDescription;
        TextView textViewPriority;
        TextView textViewQuantity;

        public ShoppingListHolder(View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.shopping_list_ingredient);
            textViewDescription = itemView.findViewById(R.id.shopping_list_description);
            textViewPriority = itemView.findViewById(R.id.text_view_priority);
            textViewQuantity = itemView.findViewById(R.id.shopping_list_quantity);
        }
    }
}
