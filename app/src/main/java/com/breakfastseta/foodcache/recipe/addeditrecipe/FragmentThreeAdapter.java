package com.breakfastseta.foodcache.recipe.addeditrecipe;

import android.content.Context;
import android.content.DialogInterface;
import android.text.InputType;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.breakfastseta.foodcache.R;

import java.util.ArrayList;

public class FragmentThreeAdapter extends
        RecyclerView.Adapter<FragmentThreeAdapter.ViewHolder>{

    private ArrayList<String> arr;

    public FragmentThreeAdapter(ArrayList<String> arr) {
        this.arr = arr;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View stepsView = inflater.inflate(R.layout.item_fragment_three, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(stepsView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Get the data model based on position
        String step = arr.get(position);

        // Set item views based on your views and data model
        TextView textView = holder.stepsTextView;
        textView.setText("Step " + (position + 1) + ": " + step);
    }

    @Override
    public int getItemCount() {
        return arr.size();
    }

    public void delete(int position) {
        arr.remove(position);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView stepsTextView;

        public ViewHolder(View itemView) {

            super(itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    String text = arr.get(position);

                    final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(itemView.getContext());

                    LinearLayout container = new LinearLayout(itemView.getContext());
                    container.setOrientation(LinearLayout.VERTICAL);
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    lp.setMargins(48, 8, 48, 8);
                    final EditText edittext = new EditText(itemView.getContext());
                    edittext.setLayoutParams(lp);
                    edittext.setGravity(Gravity.CENTER);
                    edittext.setMaxLines(10);
                    edittext.setVerticalScrollBarEnabled(true);
                    edittext.setMovementMethod(ScrollingMovementMethod.getInstance());
                    edittext.setScrollBarStyle(View.SCROLLBARS_INSIDE_INSET);
                    edittext.setImeOptions(EditorInfo.IME_FLAG_NO_ENTER_ACTION);
                    edittext.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                    container.addView(edittext);

                    edittext.setText(text);

                    dialogBuilder.setTitle("Edit step")
                            .setPositiveButton(android.R.string.ok, null)
                            .setNegativeButton(android.R.string.cancel, null)
                            .create();
                    dialogBuilder.setView(container);

                    AlertDialog dialog = dialogBuilder.create();
                    dialog.setOnShowListener((DialogInterface.OnShowListener) dialogInterface -> {
                        Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                        button.setOnClickListener(view1 -> {
                            String name = edittext.getText().toString().trim();

                            if (name.isEmpty()) {
                                Toast.makeText(itemView.getContext(), "Name cannot be blank", Toast.LENGTH_SHORT).show();
                            } else {
                                arr.remove(position);
                                arr.add(position, name);
                                notifyItemChanged(position);
                                dialog.dismiss();
                            }
                        });
                    });
                    dialog.show();
                }
            });

            stepsTextView = itemView.findViewById(R.id.ingredient);
        }
    }
}
