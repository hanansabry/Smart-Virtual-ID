package com.android.smartvirtualid.organization;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.android.smartvirtualid.R;

public class OrganizationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organization);
        setTitle(getString(R.string.list_of_members));
    }
}