package com.example.android.sunshine.ui.list

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel

import com.example.android.sunshine.data.SunshineRepository
import com.example.android.sunshine.data.database.ListWeatherEntry

/**
 * [ViewModel] for [MainActivity]
 */
internal class MainActivityViewModel(private val mRepository: SunshineRepository) : ViewModel() {
    val forecast: LiveData<List<ListWeatherEntry>>

    init {
        forecast = mRepository.getCurrentWeatherForecasts()
    }


}