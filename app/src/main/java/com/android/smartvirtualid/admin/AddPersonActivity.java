package com.android.smartvirtualid.admin;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.android.smartvirtualid.R;

public class AddPersonActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_person);
        setTitle(getString(R.string.add_persion_title));
    }
}