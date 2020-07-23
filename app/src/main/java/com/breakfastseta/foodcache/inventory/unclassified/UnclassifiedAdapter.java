package com.breakfastseta.foodcache.inventory.unclassified;

import android.app.DatePickerDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.breakfastseta.foodcache.App;
import com.breakfastseta.foodcache.DigitsInputFilter;
import com.breakfastseta.foodcache.Inventory;
import com.breakfastseta.foodcache.R;
import com.breakfastseta.foodcache.Util;
import com.breakfastseta.foodcache.inventory.Item;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.angmarch.views.NiceSpinner;
import org.angmarch.views.OnSpinnerItemSelectedListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UnclassifiedAdapter extends
        RecyclerView.Adapter<UnclassifiedAdapter.ViewHolder>{
    private static final String TAG = "UnclassifiedAdapter";

    //TODO units not carrying over, crash with null pointer (data validation)

    final SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");
    private ArrayList<DocumentSnapshot> snapshots;
    private Context context;
    CollectionReference barcodeRef = FirebaseFirestore.getInstance().collection("Users").document(App.getFamilyUID()).collection("Barcodes");
    private UnclassifiedListener listener;

    public UnclassifiedAdapter(ArrayList<DocumentSnapshot> snapshots, Context context) {
        this.snapshots = snapshots;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.item_unclassified, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DocumentSnapshot snapshot = snapshots.get(position);
        String name = snapshot.getString("Name");
        Double quantity = snapshot.getDouble("quantity");
        String units = snapshot.getString("units");

        EditText nameEditText = holder.name;
        EditText quantityEditText = holder.quantity;
        TextView expiryDate = holder.expiryDate;
        NiceSpinner unitsSpinner = holder.unitsSpinner;
        NiceSpinner tabsSpinner = holder.tabsSpinner;
        Button discardButton = holder.discardButton;
        Button saveButton = holder.saveButton;

        discardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snapshot.getReference().delete();
                snapshots.remove(position);
                notifyItemRemoved(position);
                App.minusUnclassifiedNum();

                if (snapshots.isEmpty()) {
                    listener.onEmpty();
                }
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nameString = nameEditText.getText().toString().trim();
                Double quantity = Double.parseDouble(quantityEditText.getText().toString());
                String units = unitsSpinner.getSelectedItem().toString();
                String tab = tabsSpinner.getSelectedItem().toString();
                String dateString = expiryDate.getText().toString();

                if (nameString.isEmpty() || dateString.isEmpty()) {
                    Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        Date date = dateFormat.parse(dateString);

                        Calendar cal = Calendar.getInstance();
                        cal.setTime(date);
                        cal.set(Calendar.HOUR_OF_DAY, 0);
                        cal.set(Calendar.MINUTE, 0);
                        cal.set(Calendar.SECOND, 0);
                        cal.set(Calendar.MILLISECOND, 0);
                        date = cal.getTime();

                        Timestamp dateTimestamp = new Timestamp(date);

                        Inventory inventory = Inventory.create();
                        inventory.addIngredient(new Item(nameString, quantity, dateTimestamp, units, tab))
                                .setListener(new Inventory.OnFinishListener() {
                                    @Override
                                    public void onFinish() {
                                        snapshot.getReference().delete();
                                    }
                                });

                        Date now = new Date();
                        final long expiryDays = date.getTime() - now.getTime();
                        final Map<String, Object> docData = new HashMap<>();
                        docData.put("Name", nameString);
                        docData.put("expiryDays", expiryDays);
                        docData.put("quantity", quantity);
                        docData.put("units", units);
                        docData.put("location", tab);
                        docData.put("nameLowerCase", nameString.toLowerCase());
                        inventory.checkBarcode(docData);

                        snapshots.remove(position);
                        notifyItemRemoved(position);
                        App.minusUnclassifiedNum();

                        if (snapshots.isEmpty()) {
                            listener.onEmpty();
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        final Date[] date = {null};

        nameEditText.setText(name);
        quantityEditText.setText(Util.formatQuantityNumber(quantity, units));

        ArrayList<String> tabs = App.getTabs();
        List<String> unitsArr = Arrays.asList(context.getResources().getStringArray(R.array.units));

        tabsSpinner.attachDataSource(tabs);
        unitsSpinner.attachDataSource(unitsArr);

        expiryDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (date[0] == null) {
                    pickDate(expiryDate, null);
                } else {
                    pickDate(expiryDate, date[0]);
                }
            }
        });

        unitsSpinner.setOnSpinnerItemSelectedListener(new OnSpinnerItemSelectedListener() {
            @Override
            public void onItemSelected(NiceSpinner parent, View view, int position, long id) {
                checkUnits(unitsSpinner, quantityEditText);
            }
        });

        checkUnits(unitsSpinner, quantityEditText);

        Query query = barcodeRef.whereEqualTo("nameLowerCase", name.toLowerCase()).limit(1);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot result = task.getResult();
                    if (!result.isEmpty()) {
                        for (QueryDocumentSnapshot document : result) {
                            Map<String, Object> map = document.getData();
                            long expiryInMillies = (long) map.get("expiryDays");
                            String location = (String) map.get("location");

                            if (location != null && tabs.contains(location)) {
                                int position = tabs.indexOf(location);
                                tabsSpinner.setSelectedIndex(position);
                            }

                            // calculating expiry date
                            Timestamp now = new Timestamp(new Date());
                            Date expiry = new Date(now.getSeconds() * 1000 + expiryInMillies);

                            // Removing time part from date
                            Calendar cal = Calendar.getInstance();
                            cal.setTime(expiry);
                            cal.set(Calendar.HOUR_OF_DAY, 0);
                            cal.set(Calendar.MINUTE, 0);
                            cal.set(Calendar.SECOND, 0);
                            cal.set(Calendar.MILLISECOND, 0);
                            date[0] = cal.getTime();
                            expiryDate.setText(dateFormat.format(date[0]));
                        }
                    }
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
    }

    private void checkUnits(NiceSpinner spinnerUnits, EditText editTextQuantity) {
        String units = spinnerUnits.getSelectedItem().toString();
        if (units.equals("kg")) {
            editTextQuantity.setFilters(DigitsInputFilter.DOUBLE_FILTER);
        } else {
            String text = editTextQuantity.getText().toString();
            if (text.contains(".")) {
                editTextQuantity.setText(Util.formatQuantityNumber(Double.parseDouble(text), units));
            }
            editTextQuantity.setFilters(DigitsInputFilter.INTEGER_FILTER);
        }
    }

    public void pickDate(TextView expiryDateView, Date date) {

        final Calendar cldr = Calendar.getInstance();
        if (date != null) {
            cldr.setTime(date);
        }
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
                        try {
                            Date date = simpleDateFormat.parse(s);
                            expiryDateView.setText(dateFormat.format(date));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }, year, month, day);
        picker.show();
    }

    @Override
    public int getItemCount() {
        return snapshots.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public EditText name;
        public EditText quantity;
        public TextView expiryDate;
        public NiceSpinner unitsSpinner;
        public NiceSpinner tabsSpinner;
        public Button discardButton;
        public Button saveButton;

        public ViewHolder(View itemView) {

            super(itemView);

            name = (EditText) itemView.findViewById(R.id.edit_text_ingredient);
            quantity = (EditText) itemView.findViewById(R.id.edit_text_quantity);
            expiryDate = (TextView) itemView.findViewById(R.id.expiry_date);
            unitsSpinner = (NiceSpinner) itemView.findViewById(R.id.spinner_units);
            tabsSpinner = (NiceSpinner) itemView.findViewById(R.id.tab_spinner);
            discardButton = (Button) itemView.findViewById(R.id.discard_button);
            saveButton = (Button) itemView.findViewById(R.id.save_button);
        }
    }

    public interface UnclassifiedListener {
        void onEmpty();
    }

    public void setUnclassifiedListener(UnclassifiedListener listener) {
        this.listener = listener;
    }
}
