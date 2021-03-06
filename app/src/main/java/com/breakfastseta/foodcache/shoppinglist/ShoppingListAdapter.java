package com.breakfastseta.foodcache.shoppinglist;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.breakfastseta.foodcache.App;
import com.breakfastseta.foodcache.R;
import com.breakfastseta.foodcache.Util;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ShoppingListAdapter extends FirestoreRecyclerAdapter<ShoppingListItem, ShoppingListAdapter.ShoppingListHolder> {
    private OnItemClickListener listener;

    public ShoppingListAdapter(@NonNull FirestoreRecyclerOptions<ShoppingListItem> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ShoppingListHolder holder, int position, @NonNull ShoppingListItem model) {
        String units = model.getUnits();
        holder.textViewTitle.setText(model.getItemName());
        holder.textViewDescription.setText(model.getDescription());
        holder.textViewQuantity.setText(Util.formatQuantityNumber(model.getNoItems(), units));
        holder.textViewUnits.setText(units);
    }

    @NonNull
    @Override
    public ShoppingListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_shopping,
                parent, false);
        return new ShoppingListHolder(v);
    }

    public void deleteItem(int position) {
        getSnapshots().getSnapshot(position).getReference().delete();
    }

    public void restoreItem(ShoppingListItem item) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = App.getFamilyUID();

        CollectionReference notebookRef = FirebaseFirestore.getInstance()
                .collection("Users")
                .document(uid)
                .collection("ShoppingList");

        String name = item.getItemName();
        String description = item.getDescription();
        double quantity = item.getNoItems();
        String units = item.getUnits();

        notebookRef.add(new ShoppingListItem(name, description, quantity, units));
    }

    class ShoppingListHolder extends RecyclerView.ViewHolder {
        TextView textViewTitle;
        TextView textViewDescription;
        TextView textViewQuantity;
        TextView textViewUnits;

        public ShoppingListHolder(View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.shopping_list_ingredient);
            textViewDescription = itemView.findViewById(R.id.shopping_list_description);
            textViewQuantity = itemView.findViewById(R.id.shopping_list_quantity);
            textViewUnits = itemView.findViewById(R.id.shopping_list_units);

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
