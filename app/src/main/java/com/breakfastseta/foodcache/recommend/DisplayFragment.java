package com.breakfastseta.foodcache.recommend;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.breakfastseta.foodcache.R;

public class DisplayFragment extends Fragment {

    Button button;

    private DisplayListener listener;

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

        button = (Button) view.findViewById(R.id.button);


        // Pass Variables to activity
        button.setOnClickListener(v -> {
                listener.nextDisplay();
        });

    }

    public interface DisplayListener {
        void nextDisplay();
    }
}