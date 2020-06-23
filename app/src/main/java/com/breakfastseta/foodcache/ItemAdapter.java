package com.breakfastseta.foodcache;

import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class ItemAdapter extends FirestoreRecyclerAdapter<Item, ItemAdapter.ItemHolder> {
    private OnItemClickListener listener;

    /*
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     * @param options
     */
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
        long diffInMillies = expiry.getTime() - now.getTime();
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
                holder.textViewDaysLeft.setText("EXPIRED");
            }
        };
        cdt.start();

        //Getting Units
        String units = model.getUnits();
        if (units.equals("Items")) {
            units = "";
        }

        //Formatting Strings
        String quantity = "Quantity: " + String.valueOf(model.getQuantity()) + units;
        String expiryDate = "Expiry Date: " + expiryString;

        //Set TextViews
        holder.textViewIngredient.setText(model.getIngredient());
        holder.textViewQuantity.setText(quantity);
        holder.textViewExpiryDate.setText(expiryDate);
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
        TextView textViewExpiryDate;
        TextView textViewDaysLeft;

        public ItemHolder(@NonNull View itemView) {
            super(itemView);
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

