package com.breakfastseta.foodcache.inventory;

import android.graphics.Color;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.breakfastseta.foodcache.App;
import com.breakfastseta.foodcache.Inventory;
import com.breakfastseta.foodcache.R;
import com.breakfastseta.foodcache.Util;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

public class ItemAdapter extends FirestoreRecyclerAdapter<Item, ItemAdapter.ItemHolder> {
    private static final String TAG = "ItemAdapter";

    private OnItemClickListener listener;

    public static double ALL = -1;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String uid = App.getFamilyUID();
    private CollectionReference inventoryRef = db.collection("Users").document(uid).collection("Inventory");

    public ItemAdapter(@NonNull FirestoreRecyclerOptions<Item> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull final ItemHolder holder, int position, @NonNull Item model) {
        //Formatting Date
        Date expiry = model.getDateTimestamp().toDate();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");
        String expiryString = dateFormat.format(expiry);

        //Calculating Days Left
        Date now = new Date();
        long diffInMillies = expiry.getTime() - now.getTime() + 86400000;
        if (diffInMillies < 0) {
            diffInMillies = 0;
        }

        //Countdown Timer
        CountDownTimer cdt = new CountDownTimer(diffInMillies, 3600000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long days = TimeUnit.MILLISECONDS.toDays(millisUntilFinished);

                String daysLeft = "Days Left: " + String.valueOf(days + 1);
                holder.textViewDaysLeft.setText(daysLeft);
            }

            @Override
            public void onFinish() {
                holder.itemCardView.setCardBackgroundColor(Color.parseColor("#E53935"));
                holder.textViewDaysLeft.setText("EXPIRED");
            }
        };
        cdt.start();

        //Getting Units
        String units = model.getUnits();
        double quantity = model.getQuantity();

        //Format expiry date
        Map<String, Double> map = model.getExpiryMap();

        String expiryDate;

        if (map.size() == 1) {
            expiryDate = "Expiry Date: " + expiryString;
        } else {
            TreeMap<Date, Double> tree = new TreeMap<>();
            for (String s : map.keySet()) {
                tree.put(Inventory.stringToTimestamp(s).toDate(), map.get(s));
            }

            StringBuilder result = new StringBuilder();
            for (Date d : tree.keySet()) {
                Double q = tree.get(d);
                Log.d(TAG, "onBindViewHolder: " + d);
                result.append(Util.formatQuantity(q, units)).append(" expiring ").append(dateFormat.format(d)).append("\n");
            }
            expiryDate = result.toString().trim();
        }

        //Set TextViews
        holder.textViewIngredient.setText(model.getIngredient());
        holder.textViewQuantity.setText(Util.formatQuantity(quantity, units));
        holder.textViewExpiryDate.setText(expiryDate);
    }

    @NonNull
    @Override
    public ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ingredient, parent, false);
        return new ItemHolder(v);
    }

    public void deleteItem(int position) {
        getSnapshots().getSnapshot(position).getReference().delete();
    }

    public void restoreItem(Item item) {
        Inventory.create().addIngredient(item);
    }

    public double deleteExpired(int adapterPosition, Item oldItem) {
        Item item = oldItem.makeCopy();
        double quantity = item.getQuantity();
        double quantityDeleted = 0;
        Map<String, Double> expiryMap = item.getExpiryMap();

        Timestamp now = Timestamp.now();


        Map<String, Double> newMap = new HashMap<>();

        for (String s : expiryMap.keySet()) {
            double quantityToAdd = expiryMap.get(s);
            if (Inventory.stringToTimestamp(s).getSeconds() < (now.getSeconds() - 86400)) {
                //expired
                quantityDeleted += quantityToAdd;
            } else {
                //not expired
                newMap.put(s, quantityToAdd);
            }
        }

        getSnapshots().getSnapshot(adapterPosition).getReference().delete();

        quantity -= quantityDeleted;

        if (quantityDeleted == 0) {
            Inventory.create().addIngredient(item);
        } else if (!newMap.isEmpty()) {
            item.setExpiryMap(newMap);
            item.setQuantity(quantity);
            item.recalculateExpiry();
            Inventory.create().addIngredient(item);
        }

        if (quantity == 0) {
            return ALL;
        } else {
            return quantityDeleted;
        }
    }

    public void restoreExpired(Item ingredient) {
        String name = ingredient.getIngredient();
        String units = ingredient.getUnits();
        String location = ingredient.getLocation();

        Query query = inventoryRef
                .whereEqualTo("ingredient", name)
                .whereEqualTo("units", units)
                .whereEqualTo("location", location)
                .limit(1);

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                QuerySnapshot snapshot = task.getResult();
                for (DocumentSnapshot s : snapshot) {
                    s.getReference().delete();
                    Inventory.create().addIngredient(ingredient);
                }
            }
        });

    }

    class ItemHolder extends RecyclerView.ViewHolder {
        MaterialCardView itemCardView;
        TextView textViewIngredient;
        TextView textViewQuantity;
        TextView textViewExpiryDate;
        TextView textViewDaysLeft;

        public ItemHolder(@NonNull View itemView) {
            super(itemView);
            itemCardView = itemView.findViewById(R.id.itemCardView);
            textViewIngredient = itemView.findViewById(R.id.ingredient);
            textViewQuantity = itemView.findViewById(R.id.quantity);
            textViewExpiryDate = itemView.findViewById(R.id.expiry_date);
            textViewDaysLeft = itemView.findViewById(R.id.days_left);

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

