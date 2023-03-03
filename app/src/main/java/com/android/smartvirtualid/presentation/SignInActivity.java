package com.android.smartvirtualid.presentation;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.android.smartvirtualid.R;
import com.android.smartvirtualid.data.models.Organization;
import com.android.smartvirtualid.data.models.Person;
import com.android.smartvirtualid.datasource.SharedPreferencesDataSource;
import com.android.smartvirtualid.di.ViewModelProviderFactory;
import com.android.smartvirtualid.presentation.admin.AdminActivity;
import com.android.smartvirtualid.presentation.organization.OrganizationActivity;
import com.android.smartvirtualid.presentation.person.PersonalInformationActivity;
import com.android.smartvirtualid.presentation.viewmodels.AuthenticationViewModel;
import com.android.smartvirtualid.utils.Constants;
import com.google.android.material.textfield.TextInputEditText;

import javax.inject.Inject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dagger.android.support.DaggerAppCompatActivity;

public class SignInActivity extends DaggerAppCompatActivity {

    @BindView(R.id.email_edit_text)
    TextInputEditText emailEditText;
    @BindView(R.id.password_edit_text)
    TextInputEditText passwordEditText;

    @Inject
    ViewModelProviderFactory providerFactory;
    AuthenticationViewModel authenticationViewModel;
    @Inject
    SharedPreferencesDataSource sharedPreferencesDataSource;
    private String role;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        ButterKnife.bind(this);

        role = getIntent().getStringExtra(Constants.ROLE);
        authenticationViewModel = new ViewModelProvider(getViewModelStore(), providerFactory).get(AuthenticationViewModel.class);
        switch (role) {
            case Person.PERSON_ROLE:
                authenticationViewModel.observePersonAuthenticateStateLiveDat().observe(this, person -> {
                    sharedPreferencesDataSource.saveId(person.getId());
                    sharedPreferencesDataSource.setRole(Person.PERSON_ROLE);
                    startActivity(new Intent(this, PersonalInformationActivity.class));
                });
                break;
            case Organization.ORGANIZATION_ROLE:
                authenticationViewModel.observeOrganizationAuthenticateStateLiveDat().observe(this, organization -> {
                    sharedPreferencesDataSource.saveId(organization.getId());
                    sharedPreferencesDataSource.setRole(Organization.ORGANIZATION_ROLE);
                    startActivity(new Intent(this, OrganizationActivity.class));
                });
                break;
            case Constants.AUTHORITY_ADMIN:
                authenticationViewModel.observeAdminAuthenticateStateLiveData().observe(this, success -> {
                    startActivity(new Intent(this, AdminActivity.class));
                });
                break;
        }

        authenticationViewModel.observeErrorState().observe(this, error ->
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show());
    }

    @OnClick(R.id.login_button)
    public void onLoginClicked() {
        String email = emailEditText.getText() == null ? "" : emailEditText.getText().toString();
        String password = passwordEditText.getText() == null ? "" : passwordEditText.getText().toString();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "You must enter email and password", Toast.LENGTH_SHORT).show();
        } else {
            if (role == null) {
                Toast.makeText(this, "Can't sign in, Please try again", Toast.LENGTH_SHORT).show();
            } else if (role.equals(Person.PERSON_ROLE)) {
                authenticationViewModel.signInAsPerson(email, password);
            } else if (role.equals(Organization.ORGANIZATION_ROLE)) {
                authenticationViewModel.signInAsOrganization(email, password);
            } else if (role.equals(Constants.AUTHORITY_ADMIN)) {
                authenticationViewModel.signInAsAuthorityAdmin(email, password);
            }
        }
    }
}