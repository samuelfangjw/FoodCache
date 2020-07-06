package com.breakfastseta.foodcache.recipe;

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

import com.breakfastseta.foodcache.R;

public class AddRecipeFragmentThree extends Fragment {


    private AddRecipeFragmentThree.FragmentThreeListener listener;

    private EditText editText_step;
    private Button addButton;
    private Button saveRecipe;

    String steps = "";

    public AddRecipeFragmentThree() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof AddRecipeFragmentThree.FragmentThreeListener) {
            listener = (AddRecipeFragmentThree.FragmentThreeListener) context;
        } else {
            throw new ClassCastException(context.toString()
                    + " must implement AddRecipeFragmentThree.FragmentThreeListener");
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
        return inflater.inflate(R.layout.fragment_add_recipe_three, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        editText_step = (EditText) view.findViewById(R.id.step);
        addButton = (Button) view.findViewById(R.id.button_add);
        saveRecipe = (Button) view.findViewById(R.id.button_save);

        addButton.setOnClickListener(v -> addStep());
        saveRecipe.setOnClickListener(v -> {
            if (steps.isEmpty()) {
                Toast.makeText(getContext(), "Steps Cannot be empty", Toast.LENGTH_SHORT).show();
            } else {
                listener.nextFragmentThree(steps);
            }
        });
    }



    public interface FragmentThreeListener {
        void nextFragmentThree(String steps);
    }

    private void addStep() {
        String step = editText_step.getText().toString().trim();

        if(!step.isEmpty()) {
            steps += step +"\n";
        }
        editText_step.setText("");
    }
}