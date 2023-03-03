package com.android.smartvirtualid.presentation.person;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.support.DaggerAppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.smartvirtualid.R;
import com.android.smartvirtualid.datasource.SharedPreferencesDataSource;
import com.android.smartvirtualid.di.ViewModelProviderFactory;
import com.android.smartvirtualid.presentation.MainActivity;
import com.android.smartvirtualid.presentation.adapters.PersonOrganizationsAdapter;
import com.android.smartvirtualid.presentation.viewmodels.PersonViewModel;
import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

import javax.inject.Inject;

public class PersonalInformationActivity extends DaggerAppCompatActivity implements PersonOrganizationsAdapter.PersonOrganizationCallback {

    @BindView(R.id.photo_image_view)
    ImageView photoImageView;
    @BindView(R.id.name_edit_text)
    TextInputEditText nameEditText;
    @BindView(R.id.birthdate_edit_text)
    TextInputEditText birthdateEditText;
    @BindView(R.id.nationality_edit_text)
    TextInputEditText nationalityEditText;
    @BindView(R.id.civil_id_edit_text)
    TextInputEditText civilIdEditText;
    @BindView(R.id.disability_edit_text)
    TextInputEditText disabilityEditText;
    @BindView(R.id.residency_edit_text)
    TextInputEditText residencyEditText;
    @BindView(R.id.email_edit_text)
    TextInputEditText emailEditText;
    @BindView(R.id.phone_edit_text)
    TextInputEditText phoneEditText;
    @BindView(R.id.martial_status_edit_text)
    TextInputEditText martialEditText;
    @BindView(R.id.job_edit_text)
    TextInputEditText jobEditText;
    @BindView(R.id.gender_textview)
    TextView genderTextView;
    @BindView(R.id.organizations_recycler_view)
    RecyclerView organizationsRecyclerView;
    @BindView(R.id.organization_list_lbl)
    TextView organizationListLbl;

    @Inject
    ViewModelProviderFactory providerFactory;
    PersonViewModel personViewModel;
    @Inject
    SharedPreferencesDataSource sharedPreferencesDataSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_information);
        setTitle("Personal Information");
        ButterKnife.bind(this);

        String id = sharedPreferencesDataSource.getId();
        personViewModel = new ViewModelProvider(getViewModelStore(), providerFactory).get(PersonViewModel.class);
        personViewModel.retrievePersonData(id);
        personViewModel.observePersonLiveData().observe(this, person -> {
            Glide.with(this).load(person.getPhotoUrl()).into(photoImageView);
            nameEditText.setText(person.getName());
            birthdateEditText.setText(person.getBirthDate());
            nationalityEditText.setText(person.getNationality());
            civilIdEditText.setText(person.getCivilId());
            disabilityEditText.setText(person.getDisability());
            residencyEditText.setText(person.getResidency());
            emailEditText.setText(person.getEmail());
            phoneEditText.setText(person.getPhone());
            martialEditText.setText(person.getMartialStatus());
            jobEditText.setText(person.getJob());
            genderTextView.setText(person.getGender());
            //set organizations list
            if (person.getOrganizationsList() != null) {
                PersonOrganizationsAdapter adapter = new PersonOrganizationsAdapter(person.getOrganizationsList(), this);
                organizationsRecyclerView.setAdapter(adapter);
            } else {
                organizationListLbl.setVisibility(View.GONE);
                organizationsRecyclerView.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.sign_out) {
            FirebaseAuth.getInstance().signOut();
            sharedPreferencesDataSource.removeAllValues();
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
        return true;
    }

    @Override
    public void onScanQrCodeClicked(String qrCodeUrl) {
        Toast.makeText(this, qrCodeUrl, Toast.LENGTH_SHORT).show();
    }
}