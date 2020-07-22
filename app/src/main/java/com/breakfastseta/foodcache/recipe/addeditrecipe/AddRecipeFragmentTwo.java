package com.breakfastseta.foodcache.recipe.addeditrecipe;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.breakfastseta.foodcache.R;
import com.breakfastseta.foodcache.recipe.Ingredient;

import org.angmarch.views.NiceSpinner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class AddRecipeFragmentTwo extends Fragment {

    private AddRecipeFragmentTwo.FragmentTwoListener listener;

    private FragmentTwoAdapter adapter;
    private ArrayList<Ingredient> arr = new ArrayList<>();

    private RecyclerView recyclerView;
    private EditText editText_name;
    private EditText editText_quantity;
    private NiceSpinner spinner_units;
    private Button button_add;
    private Button button_next;

    public AddRecipeFragmentTwo() {
        // empty public constructor for add recipe
    }

    public AddRecipeFragmentTwo(ArrayList<Map<String, Object>> ingredients) {
        // for edit recipe activity
        for (Map<String, Object> map : ingredients) {
            String name = (String) map.get("name");
            Double quantity = (Double) map.get("quantity");
            String units = (String) map.get("units");

            arr.add(new Ingredient(name, quantity, units));
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof AddRecipeFragmentTwo.FragmentTwoListener) {
            listener = (AddRecipeFragmentTwo.FragmentTwoListener) context;
        } else {
            throw new ClassCastException(context.toString()
                    + " must implement AddRecipeFragmentTwo.FragmentTwoListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.listener = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_recipe_two, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = (RecyclerView) view.findViewById(R.id.rvFragmentTwo);
        editText_name = (EditText) view.findViewById(R.id.edit_text_name);
        editText_quantity = (EditText) view.findViewById(R.id.edit_text_quantity);
        spinner_units = (NiceSpinner) view.findViewById(R.id.spinner_units);
        button_add = (Button) view.findViewById(R.id.add_item_button);
        button_next = (Button) view.findViewById(R.id.button_next);

        // Create adapter passing in the sample user data
        adapter = new FragmentTwoAdapter(arr);
        // Attach the adapter to the recyclerview to populate items
        recyclerView.setAdapter(adapter);
        // Set layout manager to position the items
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        addItemTouchHelper();

        List<String> unitsArr = Arrays.asList(getResources().getStringArray(R.array.units));
        spinner_units.attachDataSource(unitsArr);

        button_add.setOnClickListener(v -> addIngredient());
        button_next.setOnClickListener(v -> {
            listener.nextFragmentTwo(arr);
        });

    }

    private void addItemTouchHelper() {
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                adapter.delete(position);
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    public interface FragmentTwoListener {
        void nextFragmentTwo(ArrayList<Ingredient> arr);
    }

    private void addIngredient() {
        String name = editText_name.getText().toString();
        String quantity = editText_quantity.getText().toString();
        String units = spinner_units.getSelectedItem().toString();
        if (name.trim().isEmpty() || quantity.trim().isEmpty()) {
            Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
        } else {
            int pos = adapter.getItemCount();
            Ingredient ingredient = new Ingredient(name, Double.parseDouble(quantity), units);
            arr.add(ingredient);
            editText_name.setText("");
            editText_quantity.setText("");

            // Notifiying recyclerview adapter
            adapter.notifyItemInserted(pos);
            recyclerView.scrollToPosition(pos);
        }
    }

}