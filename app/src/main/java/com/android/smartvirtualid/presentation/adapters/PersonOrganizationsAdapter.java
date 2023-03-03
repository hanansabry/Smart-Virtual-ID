package com.android.smartvirtualid.presentation.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.smartvirtualid.R;

import java.util.HashMap;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class PersonOrganizationsAdapter extends RecyclerView.Adapter<PersonOrganizationsAdapter.PersonOrganizationViewHolder> {

    private List<HashMap<String, String>> organizationsList;
    private PersonOrganizationCallback personOrganizationCallback;

    public PersonOrganizationsAdapter(List<HashMap<String, String>> organizationsList, PersonOrganizationCallback personOrganizationCallback) {
        this.organizationsList = organizationsList;
        this.personOrganizationCallback = personOrganizationCallback;
    }

    @NonNull
    @Override
    public PersonOrganizationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.person_organization_layout_item, parent, false);
        return new PersonOrganizationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PersonOrganizationViewHolder holder, int position) {
        HashMap<String, String> membershipDetails = organizationsList.get(position);
        holder.organizationName.setText(membershipDetails.get("organization_name"));
        holder.scanQrCode.setOnClickListener(v -> personOrganizationCallback.onScanQrCodeClicked(membershipDetails.get("qr_code")));
    }

    @Override
    public int getItemCount() {
        return organizationsList.size();
    }

    class PersonOrganizationViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.organization_name)
        TextView organizationName;
        @BindView(R.id.scan_qr_code_button)
        ImageButton scanQrCode;

        public PersonOrganizationViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface PersonOrganizationCallback {
        void onScanQrCodeClicked(String qrCodeUrl);
    }
}
