package com.android.smartvirtualid.presentation.organization;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.smartvirtualid.R;
import com.android.smartvirtualid.data.models.Member;
import com.android.smartvirtualid.data.models.Person;
import com.android.smartvirtualid.datasource.SharedPreferencesDataSource;
import com.android.smartvirtualid.di.ViewModelProviderFactory;
import com.android.smartvirtualid.presentation.images.GalleryImagesSelector;
import com.android.smartvirtualid.presentation.images.ImagesSelector;
import com.android.smartvirtualid.presentation.viewmodels.AddMemberViewModel;
import com.android.smartvirtualid.utils.Constants;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dagger.android.support.DaggerAppCompatActivity;

public class AddMemberActivity extends DaggerAppCompatActivity {

    private static final String[] REQUIRED_PERMISSIONS = {Manifest.permission.READ_EXTERNAL_STORAGE};
    private static final int REQUEST_READ_EXTERNAL_STORAGE_PERMISSIONS = 1;
    private ProgressDialog progressDialog;
    private String selectedCivilId;
    private ArrayList<String> civilIdsList;
    private GalleryImagesSelector imagesSelector;
    private Person selectedPerson;
    private String organizationId;

    @BindView(R.id.name_layout)
    TextInputLayout nameLayout;
    @BindView(R.id.name_edit_text)
    TextInputEditText nameEditText;
    @BindView(R.id.civil_id_edit_text)
    AutoCompleteTextView civilIdAutoCompleteTextView;
    @BindView(R.id.status_radio_group)
    RadioGroup statusRadioGroup;
    @BindView(R.id.upload_photo_layout)
    TextInputLayout uploadPhotoLayout;
    @BindView(R.id.upload_id_photo_edit_text)
    TextInputEditText uploadIdEditText;
    @BindView(R.id.description_edit_text)
    TextInputEditText descriptionEditText;
    @BindView(R.id.active_radio)
    RadioButton activeRadio;
    @BindView(R.id.inactive_radio)
    RadioButton inactiveRadio;

    @Inject
    ViewModelProviderFactory providerFactory;
    AddMemberViewModel addMemberViewModel;
    @Inject
    SharedPreferencesDataSource sharedPreferencesDataSource;
    private boolean isEditable;
    private Member member;
    private String organizationName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_member);
        setTitle(getString(R.string.add_member));
        ButterKnife.bind(this);
        imagesSelector = new GalleryImagesSelector(AddMemberActivity.this);
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading");

        isEditable = getIntent().getBooleanExtra(Constants.IS_EDITABLE, false);
        member = getIntent().getParcelableExtra(Constants.MEMBER);
        organizationId = sharedPreferencesDataSource.getId();
        organizationName = sharedPreferencesDataSource.getName();


        addMemberViewModel = new ViewModelProvider(getViewModelStore(), providerFactory).get(AddMemberViewModel.class);
        if (isEditable && member != null) {
            uploadPhotoLayout.setVisibility(View.GONE);
            civilIdAutoCompleteTextView.setText(member.getCivilId());
            civilIdAutoCompleteTextView.setEnabled(false);
            if (member.getStatus().equalsIgnoreCase("Active")) {
                activeRadio.setChecked(true);
            } else {
                inactiveRadio.setChecked(true);
            }
            descriptionEditText.setText(member.getDescription());
        } else {
            addMemberViewModel.retrievePersonsData();
            addMemberViewModel.observePersonsLiveData().observe(this, persons -> {
                civilIdsList = new ArrayList<>();
                for (Person person : persons) {
                    civilIdsList.add(person.getCivilId());
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, civilIdsList);
                civilIdAutoCompleteTextView.setThreshold(1);
                civilIdAutoCompleteTextView.setAdapter(adapter);
                civilIdAutoCompleteTextView.setOnItemClickListener(
                        (parent, view, position, id) -> selectedPerson = persons.get(civilIdsList.indexOf(adapter.getItem(position))));
            });
        }
        addMemberViewModel.observeAddMemberStateLiveData().observe(this, success -> {
            progressDialog.dismiss();
            if (success) {
                if (isEditable) {
                    Toast.makeText(this, "Member is edited successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Member is added successfully", Toast.LENGTH_SHORT).show();
                }
                finish();
            } else {
                Toast.makeText(this, "Can't save member, please try again later", Toast.LENGTH_SHORT).show();
            }
        });
        addMemberViewModel.observeErrorState().observe(this, error -> {
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        });
    }

    @OnClick(R.id.upload_id_photo_edit_text)
    public void onUploadPhotoClicked() {
        if (allPermissionsGranted()) {
            openGallery();
        } else if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(AddMemberActivity.this, REQUIRED_PERMISSIONS, REQUEST_READ_EXTERNAL_STORAGE_PERMISSIONS);
        }
    }

    @OnClick(R.id.save_button)
    public void onSaveClicked() {
        progressDialog.show();
        String civilId = civilIdAutoCompleteTextView.getText().toString();
        String status = statusRadioGroup.getCheckedRadioButtonId() == R.id.active_radio ? "Active" :
                statusRadioGroup.getCheckedRadioButtonId() == R.id.inactive_radio ? "Inactive" : "";
        String photoUrl = uploadIdEditText.getText() == null ? "" : uploadIdEditText.getText().toString();
        String description = descriptionEditText.getText() == null ? "" : descriptionEditText.getText().toString();

        if (isEditable && member != null) {
            member.setDescription(description);
            member.setStatus(status);
            addMemberViewModel.updateOrganizationMember(member, organizationId);
        } else {
            if (civilId.isEmpty() || status.isEmpty() || photoUrl.isEmpty()) {
                Toast.makeText(this, "You must enter all fields", Toast.LENGTH_SHORT).show();
            } else {
                if (isValidCivilId(civilId)) {
                    Member member = new Member(selectedPerson.getId(), civilId, organizationId, organizationName, status, description, photoUrl);
                    addMemberViewModel.uploadImageToFirebaseStorage(Uri.parse(photoUrl));
                    addMemberViewModel.observeUploadImageState().observe(this, photoDownloadUrl -> {
                        member.setPhotoIdUrl(photoDownloadUrl);
                        addMemberViewModel.addNewOrganizationMember(member, organizationId);
                    });
                } else {
                    Toast.makeText(this, "Not valid Civil Id", Toast.LENGTH_SHORT).show();
                }

            }
        }
    }

    private boolean isValidCivilId(String civilId) {
        return civilIdsList.contains(civilId);
    }

    private void openGallery() {
        imagesSelector.setImagesSelectorCallback(new ImagesSelector.ImagesSelectorCallback() {
            @Override
            public void onImageAdded(Uri imageUri, int position) {
                uploadIdEditText.setText(imageUri.toString());
            }

            @Override
            public void onImageSelectedError(String error) {
                Toast.makeText(AddMemberActivity.this, error, Toast.LENGTH_SHORT).show();
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