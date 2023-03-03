package com.android.smartvirtualid.presentation.organization;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dagger.android.support.DaggerAppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.smartvirtualid.R;
import com.android.smartvirtualid.data.models.Member;
import com.android.smartvirtualid.datasource.SharedPreferencesDataSource;
import com.android.smartvirtualid.di.ViewModelProviderFactory;
import com.android.smartvirtualid.presentation.MainActivity;
import com.android.smartvirtualid.presentation.adapters.MembersAdapter;
import com.android.smartvirtualid.presentation.viewmodels.OrganizationViewModel;
import com.android.smartvirtualid.utils.Constants;
import com.google.firebase.auth.FirebaseAuth;

import javax.inject.Inject;

public class OrganizationActivity extends DaggerAppCompatActivity implements MembersAdapter.OrganizationCallback {

    @BindView(R.id.members_recycler_view)
    RecyclerView membersRecyclerView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    @Inject
    ViewModelProviderFactory providerFactory;
    OrganizationViewModel organizationViewModel;
    @Inject
    SharedPreferencesDataSource sharedPreferencesDataSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organization);
        setTitle(getString(R.string.list_of_members));
        ButterKnife.bind(this);

        String organizationId = sharedPreferencesDataSource.getId();
        organizationViewModel = new ViewModelProvider(getViewModelStore(), providerFactory).get(OrganizationViewModel.class);
        organizationViewModel.retrieveOrganizationMembers(organizationId);
        organizationViewModel.observeOrganizationMembersLiveData().observe(this, members -> {
            progressBar.setVisibility(View.GONE);
            if (members == null || members.isEmpty()) {
                Toast.makeText(this, "No available members", Toast.LENGTH_SHORT).show();
            } else {
                membersRecyclerView.setVisibility(View.VISIBLE);
                MembersAdapter membersAdapter = new MembersAdapter(members, this);
                membersRecyclerView.setAdapter(membersAdapter);
            }
        });
    }

    @OnClick(R.id.add_member_fab)
    public void onAddMemberClicked() {
        startActivity(new Intent(this, AddMemberActivity.class));
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
    public void onEditMember(Member member) {
        Intent intent = new Intent(this, AddMemberActivity.class);
        intent.putExtra(Constants.IS_EDITABLE, true);
        intent.putExtra(Constants.MEMBER, member);
        startActivity(intent);
    }
}