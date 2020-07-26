package com.breakfastseta.foodcache.recipe.addeditrecipe;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.breakfastseta.foodcache.R;
import com.tobiasschuerg.prefixsuffix.PrefixSuffixEditText;

import java.util.ArrayList;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class AddRecipeFragmentThree extends Fragment {

    FragmentThreeAdapter adapter;
    ArrayList<String> arr = new ArrayList<>();

    private AddRecipeFragmentThree.FragmentThreeListener listener;

    private RecyclerView recyclerView;
    private PrefixSuffixEditText editText_step;
    private Button addButton;
    private Button saveRecipe;


    public AddRecipeFragmentThree() {
        // Empty constructor for add activity
    }

    public AddRecipeFragmentThree(ArrayList<String> steps) {
        // constructor for edit activity
        arr.addAll(steps);
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
        editText_step = (PrefixSuffixEditText) view.findViewById(R.id.step);
        addButton = (Button) view.findViewById(R.id.button_add);
        saveRecipe = (Button) view.findViewById(R.id.button_save);

        // Create adapter passing in the sample user data
        adapter = new FragmentThreeAdapter(arr);
        // Attach the adapter to the recyclerview to populate items
        recyclerView.setAdapter(adapter);
        // Set layout manager to position the items
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        addItemTouchHelper();

        addButton.setOnClickListener(v -> addStep());
        saveRecipe.setOnClickListener(v -> {
            if (arr.isEmpty()) {
                Toast.makeText(getContext(), "Steps Cannot be empty", Toast.LENGTH_SHORT).show();
            } else {
                listener.nextFragmentThree(arr);
            }
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
                int stepNumber = arr.size();
                editText_step.setPrefix("Step " + (stepNumber) + ": ");
                adapter.delete(position);

            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        .addActionIcon(R.drawable.ic_delete)
                        .addSwipeLeftLabel("Delete")
                        .addSwipeRightLabel("Delete")
                        .create()
                        .decorate();

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }


    public interface FragmentThreeListener {
        void nextFragmentThree(ArrayList<String> steps);
    }

    private void addStep() {
        String step = editText_step.getText().toString().trim();
        int stepNumber = arr.size() + 1;

        if (!step.isEmpty()) {
            int pos = adapter.getItemCount();
            arr.add(step);

            editText_step.setPrefix("Step " + (stepNumber + 1) + ": ");
            adapter.notifyItemInserted(pos);
            recyclerView.scrollToPosition(pos);
        }
        editText_step.setText("");
    }
}