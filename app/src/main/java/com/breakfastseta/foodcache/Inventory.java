package com.breakfastseta.foodcache;

import android.util.Log;

import androidx.annotation.NonNull;

import com.breakfastseta.foodcache.inventory.Item;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class Inventory {
    private static final String TAG = "Inventory";

    private OnFinishListener listener;

    private static String uid = App.getFamilyUID();
    private static FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static CollectionReference inventoryRef = db.collection("Users").document(uid).collection("Inventory");
    private static CollectionReference barcodeRef = db.collection("Users").document(uid).collection("Barcodes");

    Inventory() {
        // empty constructor
    }

    public static Inventory create() {
        return new Inventory();
    }

    // Helper class to add based on units
    public static String addQuantity(double quantity, String units) {
        switch (units) {
            case "kg":
                return Util.formatQuantityNumber(quantity + 0.1, units);
            case "g":
            case "ml":
                return Util.formatQuantityNumber(quantity + 50, units);
            case "Items":
            default:
                return Util.formatQuantityNumber(quantity + 1, units);
        }
    }

    // Helper class to subtract based on units
    public static String subtractQuantity(double quantity, String units) {
        switch (units) {
            case "kg":
                return Util.formatQuantityNumber(quantity - 0.1, units);
            case "g":
            case "ml":
                return Util.formatQuantityNumber(quantity - 50, units);
            case "Items":
            default:
                return Util.formatQuantityNumber(quantity - 1, units);
        }
    }

    public Inventory addIngredient(Item ingredient) {
        //Check if ingredient with same units exists
        String name = ingredient.getIngredient();
        String units = ingredient.getUnits();
        String location = ingredient.getLocation();
        String nameLowerCase = ingredient.getNameLowerCase();

        Query query = inventoryRef
                .whereEqualTo("nameLowerCase", nameLowerCase)
                .whereEqualTo("units", units)
                .whereEqualTo("location", location)
                .limit(1);

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                QuerySnapshot snapshot = task.getResult();
                if (snapshot.isEmpty()) {
                    // add item
                    inventoryRef.add(ingredient).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            callback();
                        }
                    });
                } else {
                    for (DocumentSnapshot document : snapshot) {
                        DocumentReference docRef = document.getReference();
                        Double quantity = document.getDouble("quantity");
                        Map<String, Double> expiryMap = (Map<String, Double>) document.get("expiryMap");

                        double quantityToAdd = ingredient.getQuantity();

                        quantity += quantityToAdd;
                        String key = ingredient.getDateTimestamp().toString();
                        if (expiryMap.containsKey(key)) {
                            double value = expiryMap.get(key);
                            value += quantityToAdd;
                            expiryMap.put(key, value);
                        } else {
                            expiryMap.put(key, quantityToAdd);
                        }

                        // recalculate expiry
                        TreeMap<Date, Double> tree = new TreeMap<>();
                        for (String s : expiryMap.keySet()) {
                            tree.put(Inventory.stringToTimestamp(s).toDate(), expiryMap.get(s));
                        }
                        Timestamp dateTimestamp = new Timestamp(tree.firstKey());

                        Map<String, Object> updateMap = new HashMap<>();
                        updateMap.put("quantity", quantity);
                        updateMap.put("expiryMap", expiryMap);
                        updateMap.put("dateTimestamp", dateTimestamp);

                        docRef.update(updateMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                callback();
                            }
                        });
                    }
                }
            }
        });

        return this;
    }

    public void checkBarcode(Map<String, Object> docData) {
        Query query = barcodeRef.whereEqualTo("nameLowerCase", docData.get("nameLowerCase")).limit(1);

        // update document with matching field, else create new document
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot result = task.getResult();
                    if (result.isEmpty()) { // no matching document found
                        barcodeRef.add(docData);
                    } else { // matching document found
                        for (QueryDocumentSnapshot document : result) {
                            String path = document.getReference().getPath();
                            db.document(path).set(docData);
                        }
                    }
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
    }

    public static Timestamp stringToTimestamp(String string) {
        String[] arr = string.split(" ");
        String secondsString = arr[0].replaceAll("[^0-9]", "");
        String nanosecondsString = arr[1].replaceAll("[^0-9]", "");

        long seconds = Long.parseLong(secondsString);
        int nanoseconds = Integer.parseInt(nanosecondsString);

        return new Timestamp(seconds, nanoseconds);
    }

    private void callback() {
        if (listener != null) {
            listener.onFinish();
        }
    }

    public interface OnFinishListener {
        void onFinish();
    }

    public void setListener(OnFinishListener value) {
        this.listener = value;
    }
}
