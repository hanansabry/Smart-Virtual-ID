package com.android.smartvirtualid.di;

import com.android.smartvirtualid.di.modules.AddMemberViewModelModule;
import com.android.smartvirtualid.di.modules.OrganizationViewModelModule;
import com.android.smartvirtualid.di.modules.AddPersonViewModelModule;
import com.android.smartvirtualid.di.modules.AuthenticationViewModelModule;
import com.android.smartvirtualid.presentation.SignInActivity;
import com.android.smartvirtualid.presentation.admin.AddOrganizationActivity;
import com.android.smartvirtualid.presentation.admin.AddPersonActivity;
import com.android.smartvirtualid.presentation.organization.AddMemberActivity;
import com.android.smartvirtualid.presentation.organization.OrganizationActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class ActivityBuildersModule {

    @ContributesAndroidInjector(modules = AuthenticationViewModelModule.class)
    abstract SignInActivity contributeSignInActivity();

    @ContributesAndroidInjector(modules = AddPersonViewModelModule.class)
    abstract AddPersonActivity contributeAddPersonActivity();

    @ContributesAndroidInjector(modules = OrganizationViewModelModule.class)
    abstract AddOrganizationActivity contributeAddOrganizationActivity();

    @ContributesAndroidInjector(modules = AddMemberViewModelModule.class)
    abstract AddMemberActivity contributeAddMemberActivity();

    @ContributesAndroidInjector(modules = OrganizationViewModelModule.class)
    abstract OrganizationActivity contributeOrganizationActivity();
}
