package com.breakfastseta.foodcache.recipe.addeditrecipe;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.breakfastseta.foodcache.R;
import com.bumptech.glide.Glide;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.angmarch.views.NiceSpinner;

import java.util.Arrays;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class EditRecipeFragmentOne extends Fragment {

    ImageView recipeImage;
    TextView textView_name;
    EditText editText_name;
    EditText editText_author;
    Button nextButton;
    Button addPhoto;
    NiceSpinner cuisineSpinner;
    EditText editText_description;
    TextView textView_description;
    List<String> cuisinesArr;

    Uri resultUri;

    private FragmentOneListener listener;

    String name;
    String author;
    String description;
    String cuisine;
    String imageUrl;

    public EditRecipeFragmentOne(String name, String author, String description, String cuisine, String imageUrl) {
        this.name = name;
        this.author = author;
        this.description = description;
        this.cuisine = cuisine;
        this.imageUrl = imageUrl;
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
        return inflater.inflate(R.layout.fragment_edit_recipe_one, container, false);
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
        cuisineSpinner = (NiceSpinner) view.findViewById(R.id.cuisine);
        editText_description = (EditText) view.findViewById(R.id.description);
        textView_description = (TextView) view.findViewById(R.id.recipe_description);

        cuisinesArr = Arrays.asList(getResources().getStringArray(R.array.cuisines));
        cuisineSpinner.attachDataSource(cuisinesArr);

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

        editText_description.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                textView_description.setText(editText_description.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // Pass Variables to activity
        nextButton.setOnClickListener(v -> {
            String name = editText_name.getText().toString();
            String author = editText_author.getText().toString();
            String tab = cuisineSpinner.getSelectedItem().toString();
            String description = editText_description.getText().toString();
            if (name.isEmpty() || author.isEmpty() || description.isEmpty()) {
                Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            } else {
                listener.nextFragmentOne(name , author, resultUri, tab, description);
            }
        });

        addPhoto.setOnClickListener(v -> editPhoto());

        setViews();
    }

    private void setViews() {
        editText_name.setText(name);
        editText_author.setText(author);
        if (!description.isEmpty()) {
            editText_description.setText(description);
        }
        int index = cuisinesArr.indexOf(cuisine);
        cuisineSpinner.setSelectedIndex(index);

        if (imageUrl!= null) {
            Uri image_path = Uri.parse(imageUrl);
            Glide.with(this)
                    .load(image_path)
                    .into(recipeImage);
        }
    }

    public interface FragmentOneListener {
        void nextFragmentOne(String name, String author, Uri resultUri, String cuisine, String description);
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