package com.android.smartvirtualid.presentation.admin;

import androidx.lifecycle.ViewModelProvider;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dagger.android.support.DaggerAppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.Toast;

import com.android.smartvirtualid.R;
import com.android.smartvirtualid.data.models.Organization;
import com.android.smartvirtualid.di.ViewModelProviderFactory;
import com.android.smartvirtualid.presentation.viewmodels.AddOrganizationViewModel;
import com.google.android.material.textfield.TextInputEditText;

import javax.inject.Inject;

public class AddOrganizationActivity extends DaggerAppCompatActivity {

    private ProgressDialog progressDialog;

    @BindView(R.id.organization_name_edit_text)
    TextInputEditText organizationNameEditText;
    @BindView(R.id.email_edit_text)
    TextInputEditText emailNameEditText;
    @BindView(R.id.password_edit_text)
    TextInputEditText passwordNameEditText;
    @BindView(R.id.description_edit_text)
    TextInputEditText descriptionNameEditText;

    @Inject
    ViewModelProviderFactory providerFactory;
    AddOrganizationViewModel addOrganizationViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_organization);
        setTitle(getString(R.string.add_organization_title));
        ButterKnife.bind(this);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading");
        addOrganizationViewModel = new ViewModelProvider(getViewModelStore(), providerFactory).get(AddOrganizationViewModel.class);
        addOrganizationViewModel.observeAddingOrganizationStateLiveData().observe(this, success -> {
            progressDialog.dismiss();
            if (success) {
                Toast.makeText(this, "Organization is added successfully", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Can't add organization, please try again later", Toast.LENGTH_SHORT).show();
            }
        });
        addOrganizationViewModel.observeErrorState().observe(this, error -> {
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        });
    }

    @OnClick(R.id.save_button)
    public void onSaveClicked() {
        String name = organizationNameEditText.getText() == null ? "" : organizationNameEditText.getText().toString();
        String email = emailNameEditText.getText() == null ? "" : emailNameEditText.getText().toString();
        String password = passwordNameEditText.getText() == null ? "" : passwordNameEditText.getText().toString();
        String description = descriptionNameEditText.getText() == null ? "" : descriptionNameEditText.getText().toString();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, R.string.all_fields_required_msg, Toast.LENGTH_SHORT).show();
        } else {
            progressDialog.show();
            Organization organization = new Organization(name, email, password, description);
            addOrganizationViewModel.addNewOrganization(organization);
        }
    }
}