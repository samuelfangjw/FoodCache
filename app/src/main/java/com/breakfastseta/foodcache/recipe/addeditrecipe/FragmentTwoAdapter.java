package com.breakfastseta.foodcache.recipe.addeditrecipe;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.breakfastseta.foodcache.R;
import com.breakfastseta.foodcache.Util;
import com.breakfastseta.foodcache.recipe.Ingredient;

import org.angmarch.views.NiceSpinner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FragmentTwoAdapter extends
        RecyclerView.Adapter<FragmentTwoAdapter.ViewHolder>{

    private ArrayList<Ingredient> arr;

    public FragmentTwoAdapter(ArrayList<Ingredient> arr) {
        this.arr = arr;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View ingredientView = inflater.inflate(R.layout.item_fragment_two, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(ingredientView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Get the data model based on position
        Ingredient ingredient = arr.get(position);

        // Set item views based on your views and data model
        TextView textView = holder.ingredientTextView;
        TextView quantity = holder.quantityTextView;
        textView.setText(ingredient.getName());
        quantity.setText(Util.formatQuantity(ingredient.getQuantity(), ingredient.getUnits()));
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
        public TextView ingredientTextView;
        public TextView quantityTextView;

        public ViewHolder(View itemView) {

            super(itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    Ingredient ingredient = arr.get(position);

                    AlertDialog.Builder builder = new AlertDialog.Builder(itemView.getContext());
                    LayoutInflater inflater = LayoutInflater.from(itemView.getContext());

                    View view = inflater.inflate(R.layout.dialog_add_recipe, null);

                    EditText edit_name = view.findViewById(R.id.edit_text_name);
                    EditText edit_quantity = view.findViewById(R.id.edit_text_quantity);
                    NiceSpinner spinner =view.findViewById(R.id.spinner_units);

                    edit_name.setText(ingredient.getName());
                    edit_quantity.setText(Util.formatQuantityNumber(ingredient.getQuantity(), ingredient.getUnits()));
                    List<String> unitsArr = Arrays.asList(itemView.getContext().getResources().getStringArray(R.array.units));
                    spinner.attachDataSource(unitsArr);
                    int index = unitsArr.indexOf(ingredient.getUnits());
                    spinner.setSelectedIndex(index);

                    builder.setView(view)
                            .setPositiveButton(android.R.string.ok, null)
                            .setNegativeButton(android.R.string.cancel, null);

                    AlertDialog dialog = builder.create();
                    dialog.setOnShowListener((DialogInterface.OnShowListener) dialogInterface -> {
                        Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                        button.setOnClickListener(view1 -> {
                            String name = edit_name.getText().toString().trim();
                            String quantityString = edit_quantity.getText().toString().trim();

                            if (name.isEmpty() || quantityString.isEmpty()) {
                                Toast.makeText(itemView.getContext(), "Name and Quantity cannot be blank", Toast.LENGTH_SHORT).show();
                            } else {
                                Ingredient newIngredient = new Ingredient(name, Double.parseDouble(quantityString), spinner.getSelectedItem().toString());

                                arr.remove(position);
                                arr.add(position, newIngredient);
                                notifyItemChanged(position);
                                dialog.dismiss();
                            }
                        });
                    });
                    dialog.show();
                }
            });

            ingredientTextView = itemView.findViewById(R.id.ingredient);
            quantityTextView = itemView.findViewById(R.id.quantity);
        }
    }
}
