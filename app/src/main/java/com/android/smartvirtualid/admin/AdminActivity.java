package com.android.smartvirtualid.admin;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.ButterKnife;
import butterknife.OnClick;

import android.content.Intent;
import android.os.Bundle;

import com.android.smartvirtualid.R;

public class AdminActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        setTitle(getString(R.string.admin_activity_title));
        ButterKnife.bind(this);
    }

    @OnClick(R.id.add_file_cardview)
    public void onAddFileClicked() {
        startActivity(new Intent(this, AddPersonActivity.class));
    }
}