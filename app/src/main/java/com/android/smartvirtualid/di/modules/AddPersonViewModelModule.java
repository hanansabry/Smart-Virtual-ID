package com.android.smartvirtualid.di.modules;

import com.android.smartvirtualid.di.ViewModelKey;
import com.android.smartvirtualid.presentation.viewmodels.AddPersonViewModel;

import androidx.lifecycle.ViewModel;
import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

@Module
public abstract class AddPersonViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(AddPersonViewModel.class)
    public abstract ViewModel bindViewModel(AddPersonViewModel viewModel);
}
