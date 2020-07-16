package com.breakfastseta.foodcache.social;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.os.Bundle;
import android.text.Layout;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.breakfastseta.foodcache.R;

public class SocialAddPostActivity extends AppCompatActivity {

    private EditText requestItem;
    private EditText requestDesc;
    private EditText requestQuan;
    private Spinner editUnits;

    private EditText blogItem;
    private EditText blogDesc;
    private ImageButton blgoImage;

    private RadioGroup radioGroup;
    private RadioButton requestButton;
    private RadioButton blogButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social_add_post);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Add Post");
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_black);

        ConstraintLayout reqLayout = findViewById(R.id.request_layout);
        ConstraintLayout blogLayout = findViewById(R.id.blogpost_layout);

        radioGroup = (RadioGroup) findViewById(R.id.type_radio_group);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.request) {
                    reqLayout.setVisibility(View.VISIBLE);
                    blogLayout.setVisibility(View.GONE);
                } else {
                    reqLayout.setVisibility(View.GONE);
                    blogLayout.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    //TODO: save post and send to Firebase, set up showing post in main Social Activity

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}