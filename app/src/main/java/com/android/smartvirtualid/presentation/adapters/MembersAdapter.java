package com.android.smartvirtualid.presentation.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.smartvirtualid.R;
import com.android.smartvirtualid.data.models.Member;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MembersAdapter extends RecyclerView.Adapter<MembersAdapter.MemberViewHolder> {

    private List<Member> memberList;
    private OrganizationCallback organizationCallback;

    public MembersAdapter(List<Member> memberList, OrganizationCallback organizationCallback) {
        this.memberList = memberList;
        this.organizationCallback = organizationCallback;
    }

    @NonNull
    @Override
    public MemberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.member_item_layout, parent, false);
        return new MemberViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MemberViewHolder holder, int position) {
        Member member = memberList.get(position);
        holder.civilId.setText(member.getCivilId());
        holder.statusTextView.setText(member.getStatus());
        holder.editButton.setOnClickListener(v -> organizationCallback.onEditMember(member));
    }

    @Override
    public int getItemCount() {
        return memberList.size();
    }

    class MemberViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.civil_id_textview)
        TextView civilId;
        @BindView(R.id.status_textview)
        TextView statusTextView;
        @BindView(R.id.edit_button)
        ImageButton editButton;

        public MemberViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface OrganizationCallback {
        void onEditMember(Member member);
    }
}
