/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.sunshine.ui.detail

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel

import com.example.android.sunshine.data.database.WeatherEntry

/**
 * [ViewModel] for [DetailActivity]
 */
class DetailActivityViewModel : ViewModel() {

    // Weather forecast the user is looking at
    var weather: MutableLiveData<WeatherEntry>? = null

    init {
        weather = MutableLiveData()
    }

    fun setWeather(weatherEntry: WeatherEntry) {
        weather?.postValue(weatherEntry)
    }

}
