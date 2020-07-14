package com.breakfastseta.foodcache.recipe;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.breakfastseta.foodcache.R;
import com.breakfastseta.foodcache.Util;
import com.google.firebase.auth.FirebaseAuth;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

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

    Bitmap bitmap;

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
            byte[] b = null;
            if (bitmap != null) {
                bitmap = Util.cropToSquare(bitmap);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                b = baos.toByteArray();
            }
            String name = editText_name.getText().toString();
            String author = editText_author.getText().toString();
            String tab = cuisine.getSelectedItem().toString();
            String description = editText_description.getText().toString();
            if (name.isEmpty() || author.isEmpty() || description.isEmpty()) {
                Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            } else {
                listener.nextFragmentOne(name , author, b, tab, switchPublic.isChecked(), description);
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
        void nextFragmentOne(String name, String author, byte[] picture, String cuisine, boolean isPublic, String description);
    }

    public void editPhoto() {
        // Show Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Take a picture or choose from gallery?")
                .setPositiveButton("Take Photo", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        imageFromCamera();
                    }
                })
                .setNegativeButton("Gallery", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        imageFromGallery();
                    }
                })
                .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
        builder.show();
    }

    public void imageFromCamera() {
        //Check if camera permissions granted
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA},
                    CAMERA_REQUEST_CODE);
        } else {
            captureImage();
        }
    }

    public void imageFromGallery() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    STORAGE_REQUEST_CODE);
        } else {
            pickFromGallery();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case CAMERA_REQUEST_CODE:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    captureImage();
                } else {
                    Toast.makeText(getContext(), "Camera Permissions Required", Toast.LENGTH_SHORT).show();
                }
                return;
            case STORAGE_REQUEST_CODE:
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    pickFromGallery();
                } else {
                    Toast.makeText(getContext(), "External Storage Permissions Required", Toast.LENGTH_SHORT).show();
                }
                return;
        }
    }

    private void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(intent, TAKE_IMAGE_CODE);
        }
    }

    private void pickFromGallery() {
        //Create an Intent with action as ACTION_PICK
        Intent intent = new Intent(Intent.ACTION_PICK);
        // Sets the type as image/*. This ensures only components of type image are selected
        intent.setType("image/*");
        //We pass an extra array with the accepted mime types. This will ensure only components with these MIME types as targeted.
        String[] mimeTypes = {"image/jpeg", "image/png"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        // Launching the Intent
        startActivityForResult(intent, GALLERY_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == TAKE_IMAGE_CODE) {
            if (resultCode == RESULT_OK) {
                bitmap = (Bitmap) data.getExtras().get("data");
                recipeImage.setImageBitmap(bitmap);
            }
        } else if (requestCode == GALLERY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Uri selectedImage = data.getData();
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImage);
                    recipeImage.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            Toast.makeText(getContext(), "Error Please Try Again", Toast.LENGTH_SHORT).show();
        }
    }
}