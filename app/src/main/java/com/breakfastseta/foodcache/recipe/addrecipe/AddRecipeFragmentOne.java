package com.breakfastseta.foodcache.recipe.addrecipe;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.breakfastseta.foodcache.R;
import com.google.firebase.auth.FirebaseAuth;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import static android.app.Activity.RESULT_OK;

public class AddRecipeFragmentOne extends Fragment {

    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 200;
    private static final int GALLERY_REQUEST_CODE = 300;
    private static final int TAKE_IMAGE_CODE = 400;

    ImageView recipeImage;
    TextView textView_name;
    EditText editText_name;
    EditText editText_author;
    Button nextButton;
    Button addPhoto;
    Spinner cuisine;
    Switch switchPublic;
    TextView tv_switch;
    EditText editText_description;

    Uri resultUri;

    private FragmentOneListener listener;

    public AddRecipeFragmentOne() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof FragmentOneListener) {
            listener = (FragmentOneListener) context;
        } else {
            throw new ClassCastException(context.toString()
                    + " must implement AddRecipeFragmentOne.FragmentOneListener");
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
        return inflater.inflate(R.layout.fragment_add_recipe_one, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recipeImage = (ImageView) view.findViewById(R.id.recipe_image);
        textView_name = (TextView) view.findViewById(R.id.recipe_name);
        editText_name = (EditText) view.findViewById(R.id.edit_text_recipe_name);
        editText_author = (EditText) view.findViewById(R.id.edit_text_author);
        nextButton = (Button) view.findViewById(R.id.next_button_one);
        addPhoto = (Button) view.findViewById(R.id.add_recipe_photo_button);
        cuisine = (Spinner) view.findViewById(R.id.cuisine);
        switchPublic = (Switch) view.findViewById(R.id.public_switch);
        tv_switch = (TextView) view.findViewById(R.id.tv_switch);
        editText_description = (EditText) view.findViewById(R.id.description);

        switchPublic.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    tv_switch.setText(switchPublic.getTextOn().toString());
                } else {
                    tv_switch.setText(switchPublic.getTextOff().toString());
                }
            }
        });

        ArrayAdapter<CharSequence> adapterUnits;
        adapterUnits = ArrayAdapter.createFromResource(getContext(),
                R.array.cuisines, android.R.layout.simple_spinner_item);
        adapterUnits.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cuisine.setAdapter(adapterUnits);

        editText_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                textView_name.setText(editText_name.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // Pass Variables to activity
        nextButton.setOnClickListener(v -> {
            String name = editText_name.getText().toString();
            String author = editText_author.getText().toString();
            String tab = cuisine.getSelectedItem().toString();
            String description = editText_description.getText().toString();
            if (name.isEmpty() || author.isEmpty() || description.isEmpty()) {
                Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            } else {
                listener.nextFragmentOne(name , author, resultUri, tab, switchPublic.isChecked(), description);
            }
        });

        addPhoto.setOnClickListener(v -> editPhoto());

        // Set author name
        String name = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        if (!name.isEmpty()) {
            editText_author.setText(name);
        }

    }

    public interface FragmentOneListener {
        void nextFragmentOne(String name, String author, Uri resultUri, String cuisine, boolean isPublic, String description);
    }

    public void editPhoto() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON_TOUCH)
                .setInitialCropWindowPaddingRatio(0)
                .setFixAspectRatio(true)
                .setAutoZoomEnabled(true)
                .setAspectRatio(1, 1)
                .start(getContext(), this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                resultUri = result.getUri();
                recipeImage.setImageURI(resultUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}