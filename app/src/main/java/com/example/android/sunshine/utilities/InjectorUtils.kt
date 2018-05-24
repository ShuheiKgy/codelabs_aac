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

package com.example.android.sunshine.utilities

import android.content.Context

import com.example.android.sunshine.AppExecutors
import com.example.android.sunshine.data.SunshineRepository
import com.example.android.sunshine.data.database.SunshineDatabase
import com.example.android.sunshine.data.network.WeatherNetworkDataSource
import com.example.android.sunshine.ui.detail.DetailViewModelFactory
import com.example.android.sunshine.ui.list.MainViewModelFactory

import java.util.Date

/**
 * Provides static methods to inject the various classes needed for Sunshine
 */
object InjectorUtils {

    fun provideRepository(context: Context): SunshineRepository {
        val database = SunshineDatabase.getInstance(context.applicationContext)
        val executors = AppExecutors.instance
        val networkDataSource = WeatherNetworkDataSource.getInstance(context.applicationContext, executors)
        return SunshineRepository.getInstance(database.weatherDao(), networkDataSource, executors)
    }

    fun provideNetworkDataSource(context: Context): WeatherNetworkDataSource {
        val executors = AppExecutors.instance
        return WeatherNetworkDataSource.getInstance(context.applicationContext, executors)
    }

    fun provideDetailViewModelFactory(context: Context, date: Date): DetailViewModelFactory {
        val repository = provideRepository(context.applicationContext)
        return DetailViewModelFactory(repository, date)
    }

    fun provideMainActivityViewModelFactory(context: Context): MainViewModelFactory {
        val repository = provideRepository(context.applicationContext)
        return MainViewModelFactory(repository)
    }

}