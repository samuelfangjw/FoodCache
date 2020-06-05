package com.breakfastseta.foodcache;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class ItemAdapter extends FirestoreRecyclerAdapter<Item, ItemAdapter.ItemHolder> {
    /*
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     * @param options
     */
    public ItemAdapter(@NonNull FirestoreRecyclerOptions<Item> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ItemHolder holder, int position, @NonNull Item model) {
        holder.textViewIngredient.setText(model.getIngredient());
        holder.textViewQuantity.setText(String.valueOf(model.getQuantity()));
    }

    @NonNull
    @Override
    public ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.ingredient_item, parent, false);
        return new ItemHolder(v);
    }

    public void deleteItem(int position) {
        getSnapshots().getSnapshot(position).getReference().delete();
    }

    class ItemHolder extends RecyclerView.ViewHolder {
        TextView textViewIngredient;
        TextView textViewQuantity;

        public ItemHolder(@NonNull View itemView) {
            super(itemView);
            textViewIngredient = itemView.findViewById(R.id.ingredient);
            textViewQuantity = itemView.findViewById(R.id.quantity);
        }
    }
}

