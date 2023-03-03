package com.android.smartvirtualid.presentation.person;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.support.DaggerAppCompatActivity;

import android.media.Image;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.smartvirtualid.R;
import com.android.smartvirtualid.di.ViewModelProviderFactory;
import com.android.smartvirtualid.presentation.viewmodels.MemberViewModel;
import com.android.smartvirtualid.utils.Constants;
import com.bumptech.glide.Glide;

import javax.inject.Inject;

public class IdentificationCardActivity extends DaggerAppCompatActivity {

    @BindView(R.id.identification_card_view)
    ImageView identificationCardView;
    @BindView(R.id.status_textview)
    TextView statusTextView;
    @BindView(R.id.status_imageview)
    ImageView statusImageView;
    @Inject
    ViewModelProviderFactory providerFactory;
    MemberViewModel memberViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_identification_card);
        setTitle(getString(R.string.identification_card));
        ButterKnife.bind(this);

        String memberId = getIntent().getStringExtra(Constants.MEMBER_ID);
        memberViewModel = new ViewModelProvider(getViewModelStore(), providerFactory).get(MemberViewModel.class);
        memberViewModel.retrieveMemberData(memberId, null);
        memberViewModel.observeMemberLiveData().observe(this, member -> {
            setTitle(member.getOrganizationName() + " - Identification Card");
            Glide.with(this).load(member.getPhotoIdUrl()).into(identificationCardView);
            statusTextView.setText(member.getStatus());
            if (member.getStatus().equalsIgnoreCase("active")) {
                Glide.with(this).load(R.drawable.active_icon).into(statusImageView);
            } else {
                Glide.with(this).load(R.drawable.inactive_icon).into(statusImageView);
            }
        });
    }
}