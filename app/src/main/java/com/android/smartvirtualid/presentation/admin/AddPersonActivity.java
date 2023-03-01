package com.android.smartvirtualid.presentation.admin;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.smartvirtualid.R;
import com.android.smartvirtualid.data.models.Person;
import com.android.smartvirtualid.di.ViewModelProviderFactory;
import com.android.smartvirtualid.presentation.images.GalleryImagesSelector;
import com.android.smartvirtualid.presentation.images.ImagesSelector;
import com.android.smartvirtualid.presentation.viewmodels.AddPersonViewModel;
import com.android.smartvirtualid.utils.Utils;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemSelected;
import dagger.android.support.DaggerAppCompatActivity;

public class AddPersonActivity extends DaggerAppCompatActivity {

    private static final String[] REQUIRED_PERMISSIONS = {Manifest.permission.READ_EXTERNAL_STORAGE};
    private static final int REQUEST_READ_EXTERNAL_STORAGE_PERMISSIONS = 1;
    private GalleryImagesSelector imagesSelector;
    private ProgressDialog progressDialog;
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
    @BindView(R.id.password_edit_text)
    TextInputEditText passwordEditText;
    @BindView(R.id.phone_edit_text)
    TextInputEditText phoneEditText;
    @BindView(R.id.martial_status_edit_text)
    TextInputEditText martialEditText;
    @BindView(R.id.martial_status_spinner)
    Spinner martialStatusSpinner;
    @BindView(R.id.job_edit_text)
    TextInputEditText jobEditText;
    @BindView(R.id.gender_radio_group)
    RadioGroup genderRadioGroup;
    @BindView(R.id.photo_edit_text)
    TextInputEditText photoEditText;
    @BindView(R.id.save_button)
    Button saveButton;

    @Inject
    ViewModelProviderFactory providerFactory;
    AddPersonViewModel addPersonViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_person);
        setTitle(getString(R.string.add_persion_title));
        ButterKnife.bind(this);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading");
        imagesSelector = new GalleryImagesSelector(AddPersonActivity.this);
        addPersonViewModel = new ViewModelProvider(getViewModelStore(), providerFactory).get(AddPersonViewModel.class);
        addPersonViewModel.observeAddPersonStateLiveData().observe(this, success -> {
            progressDialog.dismiss();
            if (success) {
                Toast.makeText(this, "Person is added successfully", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Can't save person data, please try again later", Toast.LENGTH_SHORT).show();
            }
        });
        addPersonViewModel.observeErrorState().observe(this, error -> {
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        });
    }

    @OnClick(R.id.birthdate_edit_text)
    public void onBirthdateEditTextClicked() {
        final Calendar dateCalendar = Calendar.getInstance();
        final DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar pickedDate = Calendar.getInstance();
                pickedDate.set(year, monthOfYear, dayOfMonth);
                birthdateEditText.setText(Utils.formatDate(pickedDate.getTime()));
            }

        }, dateCalendar.get(Calendar.YEAR), dateCalendar.get(Calendar.MONTH), dateCalendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    @OnClick(R.id.martial_status_edit_text)
    public void onMartialStatusClicked() {
        martialStatusSpinner.performClick();
    }

    @OnClick(R.id.photo_edit_text)
    public void onUploadPhotoClicked() {
        if (allPermissionsGranted()) {
            openGallery();
        } else if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(AddPersonActivity.this, REQUIRED_PERMISSIONS, REQUEST_READ_EXTERNAL_STORAGE_PERMISSIONS);
        }
    }

    @OnItemSelected(R.id.martial_status_spinner)
    public void onMartialStatusSpinnerItemSelected(Spinner spinner, int position) {
        martialEditText.setText(martialStatusSpinner.getSelectedItem().toString());
    }


    @OnClick(R.id.save_button)
    public void onSaveClicked() {
        String name = nameEditText.getText() == null ? "" : nameEditText.getText().toString();
        String birthdate = birthdateEditText.getText() == null ? "" : birthdateEditText.getText().toString();
        String nationality = nationalityEditText.getText() == null ? "" : nationalityEditText.getText().toString();
        String civilId = civilIdEditText.getText() == null ? "" : civilIdEditText.getText().toString();
        String disability = disabilityEditText.getText() == null ? "" : disabilityEditText.getText().toString();
        String residency = residencyEditText.getText() == null ? "" : residencyEditText.getText().toString();
        String email = emailEditText.getText() == null ? "" : emailEditText.getText().toString();
        String password = passwordEditText.getText() == null ? "" : passwordEditText.getText().toString();
        String phone = phoneEditText.getText() == null ? "" : phoneEditText.getText().toString();
        String martialStatus = martialEditText.getText() == null ? "" : martialEditText.getText().toString();
        String job = jobEditText.getText() == null ? "" : jobEditText.getText().toString();
        String gender = genderRadioGroup.getCheckedRadioButtonId() == R.id.male_radio ? "Male" :
                genderRadioGroup.getCheckedRadioButtonId() == R.id.female_radio ? "Female" : "";
        String photoUrl = photoEditText.getText() == null ? "" : photoEditText.getText().toString();

        if (addPersonViewModel.validatePersonData(
                name, birthdate, nationality, civilId, disability, residency,
                email, password, phone, martialStatus, job, gender, photoUrl
        )) {
            if (addPersonViewModel.validateCivilId(civilId)) {
                Person person = new Person(name, gender, birthdate, nationality,
                        civilId, martialStatus, residency, email,
                        password, phone, job, disability, photoUrl);
                progressDialog.show();
                addPersonViewModel.uploadImageToFirebaseStorage(Uri.parse(photoUrl));
                addPersonViewModel.observeUploadImageState().observe(this, photoDownloadUrl -> {
                    person.setPhotoUrl(photoDownloadUrl);
                    addPersonViewModel.addNewPerson(person);
                });
            } else {
                civilIdEditText.setError("Civil id must be 10 digits");
            }
        } else {
            Toast.makeText(this, "All fileds must be entered", Toast.LENGTH_SHORT).show();
        }
    }

    private void openGallery() {
        imagesSelector.setImagesSelectorCallback(new ImagesSelector.ImagesSelectorCallback() {
            @Override
            public void onImageAdded(Uri imageUri, int position) {
                photoEditText.setText(imageUri.toString());
            }

            @Override
            public void onImageSelectedError(String error) {
                Toast.makeText(AddPersonActivity.this, error, Toast.LENGTH_SHORT).show();
            }
        });
        imagesSelector.openImagesSelector();
    }

    private boolean allPermissionsGranted() {
        if (REQUIRED_PERMISSIONS != null) {
            for (String permission : REQUIRED_PERMISSIONS) {
                if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_READ_EXTERNAL_STORAGE_PERMISSIONS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            } else {
                Toast.makeText(this, "Permissions is not granted", Toast.LENGTH_SHORT).show();
            }
        }
    }

}