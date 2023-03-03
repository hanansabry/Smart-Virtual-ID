package com.android.smartvirtualid.presentation.person;

import android.os.Bundle;
import android.widget.ImageView;

import com.android.smartvirtualid.R;
import com.android.smartvirtualid.utils.Constants;
import com.bumptech.glide.Glide;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;

public class QrCodeActivity extends AppCompatActivity {

    @BindView(R.id.qr_code_image_view)
    ImageView qrCodeImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_code);
        setTitle("Qr Code");
        ButterKnife.bind(this);

        String qrCodeUrl = getIntent().getStringExtra(Constants.QR_CODE);
        Glide.with(this)
                .load(qrCodeUrl)
                .into(qrCodeImageView);
    }
}