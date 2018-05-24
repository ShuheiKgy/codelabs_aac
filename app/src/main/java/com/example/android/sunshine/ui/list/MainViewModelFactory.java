package com.example.android.sunshine.ui.list;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import com.example.android.sunshine.data.SunshineRepository;

/**
 * Factory method that allows us to create a ViewModel with a constructor that takes a
 * {@link SunshineRepository}
 */
public class MainViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final SunshineRepository mRepository;

    public MainViewModelFactory(SunshineRepository repository) {
        this.mRepository = repository;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        //noinspection unchecked
        return (T) new MainActivityViewModel(mRepository);
    }
}
