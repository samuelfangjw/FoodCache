package com.breakfastseta.foodcache.inventory;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.breakfastseta.foodcache.Inventory;
import com.breakfastseta.foodcache.R;
import com.breakfastseta.foodcache.Util;
import com.google.firebase.Timestamp;
import com.tobiasschuerg.prefixsuffix.PrefixSuffixEditText;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

public class EditQuantityAdapter extends
        RecyclerView.Adapter<EditQuantityAdapter.ViewHolder>{

    Context context;

    public ArrayList<String> keys;
    public Map<String, Double> map;
    private String units;

    private EditQuantityListener listener;

    public EditQuantityAdapter(ArrayList<String> keys, Map<String, Double> map, String units, Context context) {
        this.keys = keys;
        this.map = map;
        this.units = units;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View view = inflater.inflate(R.layout.item_edit_quantity, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String key = keys.get(position);
        Double quantity = map.get(key);
        Date expiry = Inventory.stringToTimestamp(key).toDate();

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");
        String expiryString = dateFormat.format(expiry);

        ImageButton plusButton = holder.plusButton;
        ImageButton minusButton = holder.minusButton;
        PrefixSuffixEditText editQuantity = holder.editQuantity;
        TextView editExpiry = holder.editExpiry;

        editQuantity.setSuffix(" " + units);

        editQuantity.setText(Util.formatQuantityNumber(quantity, units));
        editExpiry.setText(expiryString);

        plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double current = Double.parseDouble(editQuantity.getText().toString());
                editQuantity.setText(Inventory.addQuantity(current, units));
                double newQuantity = Double.parseDouble(editQuantity.getText().toString());
                map.put(key, newQuantity);
            }
        });

        minusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double current = Double.parseDouble(editQuantity.getText().toString());
                editQuantity.setText(Inventory.subtractQuantity(current, units));
                double newQuantity = Double.parseDouble(Inventory.subtractQuantity(current, units));
                map.put(key, newQuantity);
            }
        });

        editQuantity.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    String newQuantity = editQuantity.getText().toString().trim();
                    if (newQuantity.isEmpty()) {
                        map.put(key, 0.0);
                        editQuantity.setText(Util.formatQuantityNumber(0.0, units));
                    } else {
                        map.put(key, Double.parseDouble(newQuantity));
                        editQuantity.setText(newQuantity);
                    }
                }
            }
        });

        editExpiry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editQuantity.clearFocus();

                @SuppressLint("SimpleDateFormat") final SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");
                Date expiryDate = new Date();
                try {
                    expiryDate = dateFormat.parse(editExpiry.getText().toString());
                } catch (ParseException e){
                    e.printStackTrace();
                }

                final Calendar cldr = Calendar.getInstance();
                cldr.setTime(expiryDate);
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);

                // open date picker dialog
                DatePickerDialog picker = new DatePickerDialog(context,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                                String s = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                                Date expiry;
                                try {
                                    expiry = simpleDateFormat.parse(s);
                                    editExpiry.setText(dateFormat.format(expiry));

                                    // updating data
                                    String newKey = (new Timestamp(expiry)).toString();
                                    String oldKey = keys.get(position);
                                    keys.remove(position);

                                    if (keys.contains(newKey)) {
                                        //same date merge entries
                                        Double oldQuantity = map.get(newKey);
                                        Double newQuantity = map.get(oldKey) + oldQuantity;
                                        map.put(newKey, newQuantity);
                                        map.remove(oldKey);
                                    } else {
                                        // change current entry
                                        keys.add(position, newKey);
                                        Double oldQuantity = map.get(oldKey);
                                        map.remove(oldKey);
                                        map.put(newKey, oldQuantity);
                                    }

                                    listener.updateData();
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, year, month, day);
                picker.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return keys.size();
    }

    public interface EditQuantityListener {
        void updateData();
    }

    public void setOnUpdateQuantityListener(EditQuantityListener listener) {
        this.listener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageButton plusButton;
        public ImageButton minusButton;
        public PrefixSuffixEditText editQuantity;
        public TextView editExpiry;

        public ViewHolder(View itemView) {
            super(itemView);

            plusButton = itemView.findViewById(R.id.plus_button);
            minusButton = itemView.findViewById(R.id.minus_button);
            editExpiry = itemView.findViewById(R.id.edit_expiry_date);
            editQuantity = itemView.findViewById(R.id.edit_quantity);
        }
    }
}
