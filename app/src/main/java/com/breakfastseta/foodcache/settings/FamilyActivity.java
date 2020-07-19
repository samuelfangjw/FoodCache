package com.breakfastseta.foodcache.settings;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;

import com.breakfastseta.foodcache.App;
import com.breakfastseta.foodcache.R;
import com.breakfastseta.foodcache.Util;
import com.breakfastseta.foodcache.profile.Profile;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FamilyActivity extends AppCompatActivity {
    private static final String TAG = "SettingsActivity";

    LinearLayout layout_no_family;
    LinearLayout layout_with_family;
    TextView toggle_text;
    SwitchCompat toggle;
    TextView id_text;

    CollectionReference familyRef = FirebaseFirestore.getInstance().collection("Family");
    Profile profile = App.getProfile();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_family);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Family Sharing");
        Util.createToolbar(this, toolbar);

        layout_no_family = findViewById(R.id.layout_no_family);
        layout_with_family = findViewById(R.id.layout_with_family);
        toggle_text = findViewById(R.id.toggle_text);
        toggle = findViewById(R.id.toggle);
        id_text = findViewById(R.id.id_text);

        toggle_text.setTextColor(Color.RED);

        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    familyRef.whereEqualTo("ownerUID", profile.getFamilyUID()).limit(1).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot snapshots) {
                            for (DocumentSnapshot d : snapshots) {
                                Map<String, Boolean> s = (Map<String, Boolean>) d.get("memberStatus");
                                if (s.get(App.getUID())) {
                                    toggle_text.setText("Family Sharing: On");
                                    toggle_text.setTextColor(Color.parseColor("#037d50"));
                                    profile.setUseFamilySharing(true);
                                    App.getProfileRef().update("useFamilySharing", true);
                                } else {
                                    Toast.makeText(FamilyActivity.this, "Please wait for the group owner to grant permission to access", Toast.LENGTH_SHORT).show();
                                    toggle.setChecked(false);
                                }
                            }
                        }
                    });

                } else {
                    if (profile.getUseFamilySharing()) {
                        profile.setUseFamilySharing(false);
                        App.getProfileRef().update("useFamilySharing", false);
                    }
                    toggle_text.setText("Family Sharing: Off");
                    toggle_text.setTextColor(Color.RED);
                }
            }
        });

        if (profile.getUseFamilySharing()) {
            toggle.setChecked(true);
        }

        checkFamily();
    }

    private void setFamilyRefListener(String name) {
        familyRef.document(name).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                checkFamily();
            }
        });
    }

    private void checkFamily() {
        String familyUID = profile.getFamilyUID();
        if (familyUID == null) {
            id_text.setText("Create or Join a family to get started!");
            toggleDisplay(false);
        } else {
            familyRef.whereEqualTo("ownerUID", familyUID).limit(1).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot snapshots) {
                    for (DocumentSnapshot d : snapshots) {
                        id_text.setText("Group Name: " + d.getString("name"));
                        toggleDisplay(true);
                    }
                }
            });
        }
    }

    private void toggleDisplay(boolean isFamily) {
        if (isFamily) {
            layout_with_family.setVisibility(View.VISIBLE);
            layout_no_family.setVisibility(View.GONE);

            populateMembersView();
        } else {
            layout_with_family.setVisibility(View.GONE);
            layout_no_family.setVisibility(View.VISIBLE);
        }
    }

    private void populateMembersView() {

    }

    public void join(View view) {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);

        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(48, 8, 48, 8);
        final EditText edittext = new EditText(this);
        edittext.setLayoutParams(lp);
        edittext.setGravity(Gravity.CENTER);
        edittext.setInputType(InputType.TYPE_CLASS_TEXT);
        container.addView(edittext);

        dialogBuilder.setTitle("Join a Family Group")
                .setMessage("Enter the group name below to request to join a family group. You will only be able to join when the owner has confirmed your request.")
                .setPositiveButton(android.R.string.ok, null)
                .setNegativeButton(android.R.string.cancel, null)
                .create();
        dialogBuilder.setView(container);

        AlertDialog dialog = dialogBuilder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String name = edittext.getText().toString().trim();

                        if (name.isEmpty()) {
                            Toast.makeText(FamilyActivity.this, "Text cannot be empty", Toast.LENGTH_SHORT).show();
                        } else {
                            String nameLowercase = name.toLowerCase();
                            familyRef.whereEqualTo("nameLowercase", nameLowercase).limit(1).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot snapshots) {
                                    if (!snapshots.isEmpty()) {
                                        for (DocumentSnapshot d : snapshots) {
                                            dialog.dismiss();
                                            Map<String, Boolean> map = (Map<String, Boolean>) d.get("memberStatus");
                                            map.put(App.getUID(), false);
                                            d.getReference().update("memberStatus", map);
                                            setFamilyRefListener(d.getString("name"));
                                            profile.setFamilyUID(d.getString("ownerUID"));
                                            App.getProfileRef().update("familyUID", d.getString("ownerUID"));
                                        }
                                    } else {
                                        Toast.makeText(FamilyActivity.this, "Family Group not found, Please try again", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                        }
                    }
                });
            }
        });
        dialog.show();
    }

    public void create(View view) {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);

        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(48, 8, 48, 8);
        final EditText edittext = new EditText(this);
        edittext.setLayoutParams(lp);
        edittext.setGravity(Gravity.CENTER);
        edittext.setInputType(InputType.TYPE_CLASS_TEXT);
        container.addView(edittext);

        dialogBuilder.setTitle("Create Family Group")
                .setMessage("Give your Family Group a Name!")
                .setPositiveButton(android.R.string.ok, null)
                .setNegativeButton(android.R.string.cancel, null)
                .create();
        dialogBuilder.setView(container);

        AlertDialog dialog = dialogBuilder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String name = edittext.getText().toString().trim();
                        
                        if (name.isEmpty()) {
                            Toast.makeText(FamilyActivity.this, "Please Choose Another Name", Toast.LENGTH_SHORT).show();
                        } else {
                            String nameLowercase = name.toLowerCase();
                            familyRef.whereEqualTo("nameLowercase", nameLowercase).limit(1).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot snapshots) {
                                    if (!snapshots.isEmpty()) {
                                        Toast.makeText(FamilyActivity.this, "Name Taken. Please Choose Another Name", Toast.LENGTH_SHORT).show();
                                    } else {
                                        setFamilyRefListener(name);
                                        createFamily(name);
                                        dialog.dismiss();
                                    }
                                }
                            });
                            
                        }
                    }
                });
            }
        });
        dialog.show();
    }

    private void createFamily(String name) {
        Family family = new Family(name, App.getUID());
        familyRef.document(name).set(family);
        profile.setFamilyUID(App.getUID());
        App.getProfileRef().update("familyUID", App.getUID());
    }

    public void leave(View view) {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);

        dialogBuilder.setTitle("Leave Family Group")
                .setMessage("Are you sure you want to leave? If you are the owner of this group, the group will be permanently deleted.")
                .setPositiveButton(android.R.string.ok, null)
                .setNegativeButton(android.R.string.cancel, null)
                .create();

        AlertDialog dialog = dialogBuilder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        familyRef.whereEqualTo("ownerUID", profile.getFamilyUID()).limit(1).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot snapshots) {
                                for (DocumentSnapshot d : snapshots) {
                                    String ownerID = d.getString("ownerUID");
                                    if (ownerID.equals(App.getUID())) {
                                        d.getReference().delete();
                                    } else {
                                        Map<String, Boolean> s = (Map<String, Boolean>) d.get("memberStatus");
                                        s.remove(App.getUID());
                                        ArrayList<String> members = (ArrayList<String>) d.get("members");
                                        members.remove(App.getUID());
                                        Map<String, Object> updates = new HashMap<>();
                                        updates.put("members", members);
                                        updates.put("memberStatus", s);
                                        d.getReference().update(updates);
                                    }

                                    setFamilyRefListener(d.getString("name"));
                                    profile.setFamilyUID(null);
                                    profile.setUseFamilySharing(false);
                                    App.getProfileRef().update("useFamilySharing", false);
                                }
                            }
                        });
                        dialog.dismiss();
                    }
                });
            }
        });
        dialog.show();
    }
}