package com.breakfastseta.foodcache.inventory;

import android.content.Context;
import android.content.DialogInterface;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.breakfastseta.foodcache.App;
import com.breakfastseta.foodcache.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class TabsAdapter extends
        RecyclerView.Adapter<TabsAdapter.ViewHolder> implements ItemTouchHelperAdapter {

    private ArrayList<String> arr;
    Context context;

    private CollectionReference inventoryRef = FirebaseFirestore.getInstance().collection("Users").document(App.getFamilyUID()).collection("Inventory");

    public TabsAdapter(ArrayList<String> arr, Context context) {
        this.arr = arr;
        this.context = context;
    }

    private ItemTouchHelper itemTouchHelper;
    private OnTabListener listener;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View recipeView = inflater.inflate(R.layout.item_tabs, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(recipeView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Get the data model based on position
        String tab = arr.get(position);

        // Set item views based on your views and data model
        TextView textView = holder.textViewName;

        textView.setText(tab);
    }

    @Override
    public int getItemCount() {
        return arr.size();
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        String fromTab = arr.get(fromPosition);
        arr.remove(fromTab);
        arr.add(toPosition, fromTab);
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public void onItemSwiped(int position) {
        if (arr.size() > 1) {
            String tab = arr.get(position);

            inventoryRef.whereEqualTo("location", tab).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot snapshots) {
                    if (snapshots.isEmpty()) {
                        arr.remove(position);
                        notifyItemRemoved(position);
                    } else {
                        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);

                        dialogBuilder.setTitle("Delete Tab")
                                .setMessage("This tab still contains items. Items have to be deleted before the tab can be removed. Would you like to delete all items? This action is irreversible.")
                                .setPositiveButton(android.R.string.ok, null)
                                .setNegativeButton(android.R.string.cancel, null)
                                .create();

                        AlertDialog dialog = dialogBuilder.create();
                        dialog.setOnShowListener((DialogInterface.OnShowListener) dialogInterface -> {
                            Button positive = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                            Button negative = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEGATIVE);

                            negative.setOnClickListener(view1 -> {
                                notifyItemChanged(position);
                                dialog.dismiss();
                            });

                            positive.setOnClickListener(view1 -> {
                                for (DocumentSnapshot snapshot : snapshots) {
                                    snapshot.getReference().delete();
                                }

                                arr.remove(position);
                                notifyItemRemoved(position);
                                dialog.dismiss();
                            });
                        });
                        dialog.show();
                    }
                }
            });

        } else {
            Toast.makeText(context, "You must have at least one tab", Toast.LENGTH_SHORT).show();
            notifyItemChanged(position);
        }
    }

    public void setItemTouchHelper(ItemTouchHelper touchHelper) {
        this.itemTouchHelper = touchHelper;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnTouchListener, GestureDetector.OnGestureListener {
        TextView textViewName;
        GestureDetector gestureDetector;

        public ViewHolder(View itemView) {

            super(itemView);

            textViewName = itemView.findViewById(R.id.tab_name);

            gestureDetector = new GestureDetector(itemView.getContext(), this);

            itemView.setOnTouchListener(this);
        }

        @Override
        public boolean onDown(MotionEvent e) {
            return false;
        }

        @Override
        public void onShowPress(MotionEvent e) {

        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            listener.onClick(getAdapterPosition());
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            itemTouchHelper.startDrag(this);
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return true;
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            gestureDetector.onTouchEvent(event);
            return true;
        }
    }

    public interface OnTabListener {
        void onClick(int position);
    }

    public void setOnTabListener(OnTabListener listener) {
        this.listener = listener;
    }

}
