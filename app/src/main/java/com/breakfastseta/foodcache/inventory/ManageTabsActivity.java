package com.breakfastseta.foodcache.inventory;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.breakfastseta.foodcache.App;
import com.breakfastseta.foodcache.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ManageTabsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;

    ArrayList<String> tabs;
    TabsAdapter adapter;
    DocumentReference tabsRef = FirebaseFirestore.getInstance().collection("Users").document(App.getFamilyUID());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_tabs);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Manage Tabs");
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_black);

        recyclerView = findViewById(R.id.recycler_view);

        setUpRecyclerView();
    }

    private void setUpRecyclerView() {
        tabs = App.getTabs();

        adapter = new TabsAdapter(tabs, this);

        ItemTouchHelper.Callback callback = new TabsItemTouchHelper(adapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        adapter.setItemTouchHelper(itemTouchHelper);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        adapter.setOnTabListener(new TabsAdapter.OnTabListener() {
            @Override
            public void onClick(int position) {
                String tab = tabs.get(position);

                final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(ManageTabsActivity.this);

                LinearLayout container = new LinearLayout(ManageTabsActivity.this);
                container.setOrientation(LinearLayout.VERTICAL);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                lp.setMargins(48, 8, 48, 8);
                final EditText edittext = new EditText(ManageTabsActivity.this);
                edittext.setLayoutParams(lp);
                edittext.setGravity(Gravity.CENTER);
                edittext.setInputType(InputType.TYPE_CLASS_TEXT);
                container.addView(edittext);

                edittext.setText(tab);

                dialogBuilder.setTitle("Edit tab name")
                        .setMessage("Enter tab name")
                        .setPositiveButton(android.R.string.ok, null)
                        .setNegativeButton(android.R.string.cancel, null)
                        .create();
                dialogBuilder.setView(container);

                AlertDialog dialog = dialogBuilder.create();
                dialog.setOnShowListener((DialogInterface.OnShowListener) dialogInterface -> {
                    Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                    button.setOnClickListener(view1 -> {
                        String name = edittext.getText().toString().trim();

                        if (name.isEmpty()) {
                            Toast.makeText(ManageTabsActivity.this, "Name cannot be blank", Toast.LENGTH_SHORT).show();
                        } else {
                            tabs.remove(position);
                            tabs.add(position, name);
                            adapter.notifyItemChanged(position);
                            dialog.dismiss();
                        }
                    });
                });
                dialog.show();
            }
        });

        recyclerView.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
    }

    public void save() {
        Map<String, ArrayList<String>> map = new HashMap<>();
        map.put("tabs", tabs);
        tabsRef.set(map);
    }

    public void addTab(View view) {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);

        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(48, 8, 48, 8);
        final EditText edittext = new EditText(this);
        edittext.setLayoutParams(lp);
        edittext.setGravity(Gravity.CENTER);
        edittext.setInputType(InputType.TYPE_CLASS_TEXT);
        container.addView(edittext);

        dialogBuilder.setTitle("Add tab")
                .setMessage("Enter tab name")
                .setPositiveButton(android.R.string.ok, null)
                .setNegativeButton(android.R.string.cancel, null)
                .create();
        dialogBuilder.setView(container);

        AlertDialog dialog = dialogBuilder.create();
        dialog.setOnShowListener((DialogInterface.OnShowListener) dialogInterface -> {
            Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
            button.setOnClickListener(view1 -> {
                String name = edittext.getText().toString().trim();

                if (name.isEmpty()) {
                    Toast.makeText(ManageTabsActivity.this, "Name cannot be blank", Toast.LENGTH_SHORT).show();
                } else if (tabs.contains(name)) {
                    Toast.makeText(ManageTabsActivity.this, "Tabs cannot have the same name and capitalisation", Toast.LENGTH_SHORT).show();
                } else if (tabs.size() >= 8) {
                    Toast.makeText(ManageTabsActivity.this, "You cannot have more than 8 tabs", Toast.LENGTH_SHORT).show();
                } else{
                    int position = adapter.getItemCount();
                    tabs.add(name);
                    adapter.notifyItemInserted(position);
                    dialog.dismiss();
                }
            });
        });
        dialog.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        save();
        Intent intent = new Intent();
        setResult(0, intent);
        finish();
    }
}