package com.breakfastseta.foodcache.recommend;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.breakfastseta.foodcache.R;
import com.breakfastseta.foodcache.recipe.ViewRecipeActivity;

public class DisplayFragment extends Fragment {

    private RecyclerView recyclerView;

    private DisplayListener listener;
    private DisplayRecipeAdapter adapter;

    public DisplayFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof DisplayListener) {
            listener = (DisplayListener) context;
        } else {
            throw new ClassCastException(context.toString()
                    + " must implement DisplayFragment.DisplayListener");
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
        return inflater.inflate(R.layout.fragment_recommended_recipes, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);

        adapter = new DisplayRecipeAdapter(((RecommendActivity) getActivity()).arrResults);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter.setOnItemClickListener(new DisplayRecipeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String path) {
                Intent intent = new Intent(getContext(), ViewRecipeActivity.class);
                intent.putExtra("path", path);
                startActivity(intent);
            }
        });
    }

    public interface DisplayListener {
        void nextDisplay();
    }
}