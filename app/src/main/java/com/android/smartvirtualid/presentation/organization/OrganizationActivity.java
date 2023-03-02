package com.android.smartvirtualid.presentation.organization;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.ButterKnife;
import butterknife.OnClick;

import android.content.Intent;
import android.os.Bundle;

import com.android.smartvirtualid.R;

public class OrganizationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organization);
        setTitle(getString(R.string.list_of_members));
        ButterKnife.bind(this);
    }

    @OnClick(R.id.add_member_fab)
    public void onAddMemberClicked() {
        startActivity(new Intent(this, AddMemberActivity.class));
    }
}