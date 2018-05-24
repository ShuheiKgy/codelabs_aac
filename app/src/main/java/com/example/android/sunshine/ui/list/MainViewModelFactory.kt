package com.example.android.sunshine.ui.list

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider

import com.example.android.sunshine.data.SunshineRepository

/**
 * Factory method that allows us to create a ViewModel with a constructor that takes a
 * [SunshineRepository]
 */
class MainViewModelFactory(private val mRepository: SunshineRepository) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        return MainActivityViewModel(mRepository) as T
    }
}
