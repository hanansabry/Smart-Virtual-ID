package com.android.smartvirtualid.presentation;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.ButterKnife;
import butterknife.OnClick;

import android.content.Intent;
import android.os.Bundle;

import com.android.smartvirtualid.R;
import com.android.smartvirtualid.presentation.admin.AdminActivity;
import com.android.smartvirtualid.presentation.organization.OrganizationActivity;
import com.android.smartvirtualid.presentation.person.PersonalInformationActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.login_as_civil_authority)
    public void onLoginAsCivilAuthorityClicked() {
        startActivity(new Intent(this, AdminActivity.class));
    }

    @OnClick(R.id.login_with_id)
    public void onLoginWithIdClicked() {
        startActivity(new Intent(this, PersonalInformationActivity.class));
    }

    @OnClick(R.id.login_as_organization)
    public void onLoginAsOrganization() {
        startActivity(new Intent(this, OrganizationActivity.class));
    }
}