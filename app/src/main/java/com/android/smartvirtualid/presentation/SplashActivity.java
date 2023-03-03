package com.android.smartvirtualid.presentation;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.android.smartvirtualid.R;
import com.android.smartvirtualid.data.models.Organization;
import com.android.smartvirtualid.data.models.Person;
import com.android.smartvirtualid.datasource.SharedPreferencesDataSource;
import com.android.smartvirtualid.presentation.admin.AdminActivity;
import com.android.smartvirtualid.presentation.organization.OrganizationActivity;
import com.android.smartvirtualid.presentation.person.PersonalInformationActivity;
import com.android.smartvirtualid.utils.Constants;

import javax.inject.Inject;

import dagger.android.support.DaggerAppCompatActivity;

public class SplashActivity extends DaggerAppCompatActivity {
    private static final long SPLASH_TIME_OUT = 500;

    @Inject
    SharedPreferencesDataSource sharedPreferencesDataSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        new Handler().postDelayed(() -> {
            String role = sharedPreferencesDataSource.getRole();
            if (role == null) {
                startActivity(new Intent(this, MainActivity.class));
            } else {
                switch (role) {
                    case Person.PERSON_ROLE:
                        startActivity(new Intent(this, PersonalInformationActivity.class));
                        break;
                    case Organization.ORGANIZATION_ROLE:
                        startActivity(new Intent(this, OrganizationActivity.class));
                        break;
                    case Constants.AUTHORITY_ADMIN:
                        startActivity(new Intent(this, AdminActivity.class));
                        break;
                }
            }

        }, SPLASH_TIME_OUT);
    }
}