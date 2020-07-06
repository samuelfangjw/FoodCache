package com.breakfastseta.foodcache.recipe;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.breakfastseta.foodcache.R;

import java.util.ArrayList;

public class AddRecipeFragmentTwo extends Fragment {

    private AddRecipeFragmentTwo.FragmentTwoListener listener;

    ArrayList<Ingredient> arr = new ArrayList<>();

    EditText editText_name;
    EditText editText_quantity;
    Spinner spinner_units;
    Button button_add;
    Button button_next;

    public AddRecipeFragmentTwo() {
        // Required empty public constructor
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

        editText_name = (EditText) view.findViewById(R.id.edit_text_name);
        editText_quantity = (EditText) view.findViewById(R.id.edit_text_quantity);
        spinner_units = (Spinner) view.findViewById(R.id.spinner_units);
        button_add = (Button) view.findViewById(R.id.add_item_button);
        button_next = (Button) view.findViewById(R.id.button_next);

        ArrayAdapter<CharSequence> adapterUnits;
        adapterUnits = ArrayAdapter.createFromResource(getContext(),
                R.array.units, android.R.layout.simple_spinner_item);
        adapterUnits.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_units.setAdapter(adapterUnits);

        button_add.setOnClickListener(v -> addIngredient());
        button_next.setOnClickListener(v -> {
            listener.nextFragmentTwo(arr);
        });
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
            Ingredient ingredient = new Ingredient(name, Integer.parseInt(quantity), units);
            arr.add(ingredient);
            editText_name.setText("");
            editText_quantity.setText("");
        }
    }

}