package com.android.smartvirtualid.di.modules;

import com.android.smartvirtualid.di.ViewModelKey;
import com.android.smartvirtualid.presentation.viewmodels.AddPersonViewModel;
import com.android.smartvirtualid.presentation.viewmodels.AuthenticationViewModel;

import androidx.lifecycle.ViewModel;
import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

@Module
public abstract class AuthenticationViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(AuthenticationViewModel.class)
    public abstract ViewModel bindViewModel(AuthenticationViewModel viewModel);
}
