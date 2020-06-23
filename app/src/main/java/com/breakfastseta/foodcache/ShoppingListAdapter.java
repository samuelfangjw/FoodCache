package com.breakfastseta.foodcache;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.concurrent.RecursiveAction;

public class ShoppingListAdapter extends FirestoreRecyclerAdapter<ShoppingListItem, ShoppingListAdapter.ShoppingListHolder> {
    private OnItemClickListener listener;

    public ShoppingListAdapter(@NonNull FirestoreRecyclerOptions<ShoppingListItem> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ShoppingListHolder holder, int position, @NonNull ShoppingListItem model) {
        holder.textViewTitle.setText(model.getItemName());
        holder.textViewDescription.setText(model.getDescription());
        holder.textViewQuantity.setText(String.valueOf(model.getNoItems()));
    }

    @NonNull
    @Override
    public ShoppingListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.shopping_item,
                parent, false);
        return new ShoppingListHolder(v);
    }

    public void deleteItem(int position) {
        getSnapshots().getSnapshot(position).getReference().delete();
    }

    public void restoreItem(ShoppingListItem item) {
        CollectionReference notebookRef = FirebaseFirestore.getInstance()
                .collection("ShoppingList");

        String name = item.getItemName();
        String description = item.getDescription();
        int quantity = item.getNoItems();

        notebookRef.add(new ShoppingListItem(name, description, quantity));
    }

    class ShoppingListHolder extends RecyclerView.ViewHolder {
        TextView textViewTitle;
        TextView textViewDescription;
        TextView textViewQuantity;

        public ShoppingListHolder(View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.shopping_list_ingredient);
            textViewDescription = itemView.findViewById(R.id.shopping_list_description);
            textViewQuantity = itemView.findViewById(R.id.shopping_list_quantity);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View V) {
                    int position = getAdapterPosition();
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
