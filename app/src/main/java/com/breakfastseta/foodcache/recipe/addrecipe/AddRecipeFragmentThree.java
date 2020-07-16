package com.breakfastseta.foodcache.recipe.addrecipe;

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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.breakfastseta.foodcache.R;

import java.util.ArrayList;

public class AddRecipeFragmentThree extends Fragment {

    FragmentThreeAdapter adapter;
    ArrayList<String> arr = new ArrayList<>();

    private AddRecipeFragmentThree.FragmentThreeListener listener;

    private RecyclerView recyclerView;
    private EditText editText_step;
    private Button addButton;
    private Button saveRecipe;


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

        recyclerView = (RecyclerView) view.findViewById(R.id.rvFragmentThree);
        editText_step = (EditText) view.findViewById(R.id.step);
        addButton = (Button) view.findViewById(R.id.button_add);
        saveRecipe = (Button) view.findViewById(R.id.button_save);

        // Create adapter passing in the sample user data
        adapter = new FragmentThreeAdapter(arr);
        // Attach the adapter to the recyclerview to populate items
        recyclerView.setAdapter(adapter);
        // Set layout manager to position the items
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        addButton.setOnClickListener(v -> addStep());
        saveRecipe.setOnClickListener(v -> {
            if (arr.isEmpty()) {
                Toast.makeText(getContext(), "Steps Cannot be empty", Toast.LENGTH_SHORT).show();
            } else {
                listener.nextFragmentThree(arr);
            }
        });
    }



    public interface FragmentThreeListener {
        void nextFragmentThree(ArrayList<String> steps);
    }

    private void addStep() {
        String step = editText_step.getText().toString().trim();

        if(!step.isEmpty()) {
            int pos = adapter.getItemCount();
            arr.add(step);

            adapter.notifyItemInserted(pos);
            recyclerView.scrollToPosition(pos);
        }
        editText_step.setText("");
    }
}