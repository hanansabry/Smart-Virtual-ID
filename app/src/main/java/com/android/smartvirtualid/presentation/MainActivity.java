package com.android.smartvirtualid.presentation;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.ButterKnife;
import butterknife.OnClick;

import android.content.Intent;
import android.os.Bundle;

import com.android.smartvirtualid.R;
import com.android.smartvirtualid.data.models.Organization;
import com.android.smartvirtualid.data.models.Person;
import com.android.smartvirtualid.presentation.admin.AdminActivity;
import com.android.smartvirtualid.presentation.organization.OrganizationActivity;
import com.android.smartvirtualid.presentation.person.PersonalInformationActivity;
import com.android.smartvirtualid.utils.Constants;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.login_as_civil_authority)
    public void onLoginAsCivilAuthorityClicked() {
        Intent intent = new Intent(this, SignInActivity.class);
        intent.putExtra(Constants.ROLE, Constants.AUTHORITY_ADMIN);
        startActivity(intent);
    }

    @OnClick(R.id.login_with_id)
    public void onLoginWithIdClicked() {
        Intent intent = new Intent(this, SignInActivity.class);
        intent.putExtra(Constants.ROLE, Person.PERSON_ROLE);
        startActivity(intent);
    }

    @OnClick(R.id.login_as_organization)
    public void onLoginAsOrganization() {
        Intent intent = new Intent(this, SignInActivity.class);
        intent.putExtra(Constants.ROLE, Organization.ORGANIZATION_ROLE);
        startActivity(intent);
    }
}