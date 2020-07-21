package com.breakfastseta.foodcache.recipe.recommend;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.breakfastseta.foodcache.R;

import org.angmarch.views.NiceSpinner;

import java.util.ArrayList;
import java.util.Arrays;

public class ParametersFragment extends Fragment {

    Button button;
    NiceSpinner spinner;
    RadioGroup radioGroup;

    private ParametersListener listener;

    public ParametersFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof ParametersListener) {
            listener = (ParametersListener) context;
        } else {
            throw new ClassCastException(context.toString()
                    + " must implement ParametersFragment.ParametersListener");
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
        return inflater.inflate(R.layout.fragment_parameters, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        button = (Button) view.findViewById(R.id.button);
        spinner = (NiceSpinner) view.findViewById(R.id.spinner);
        radioGroup = (RadioGroup) view.findViewById(R.id.radio_group);

        // Set Spinner
        ArrayList<String> cuisines = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.cuisines)));
        cuisines.add(0, "All");
        spinner.attachDataSource(cuisines);

        // Pass Variables to activity
        button.setOnClickListener(v -> {
            int id = radioGroup.getCheckedRadioButtonId();
            int settings = -1;
            if (id == R.id.radio_default) {
                settings = RecommendActivity.DEFAULT;
            } else if (id == R.id.radio_fresh) {
                settings = RecommendActivity.FRESH;
            }
                listener.nextParameters(spinner.getSelectedItem().toString(), settings);
        });

    }

    public interface ParametersListener {
        void nextParameters(String cuisine, int settings);
    }
}