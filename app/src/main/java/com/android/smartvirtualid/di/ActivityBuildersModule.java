package com.android.smartvirtualid.di;

import com.android.smartvirtualid.di.modules.AddPersonViewModelModule;
import com.android.smartvirtualid.presentation.admin.AddPersonActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class ActivityBuildersModule {

    @ContributesAndroidInjector(modules = AddPersonViewModelModule.class)
    abstract AddPersonActivity contributeAddPersonActivity();
}
