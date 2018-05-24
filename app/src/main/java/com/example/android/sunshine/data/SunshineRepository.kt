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

package com.example.android.sunshine.data

import android.util.Log

import com.example.android.sunshine.AppExecutors
import com.example.android.sunshine.data.database.WeatherDao
import com.example.android.sunshine.data.network.WeatherNetworkDataSource
import android.arch.lifecycle.LiveData
import com.example.android.sunshine.utilities.SunshineDateUtils
import com.example.android.sunshine.data.database.ListWeatherEntry
import com.example.android.sunshine.data.database.WeatherEntry
import java.util.Date


/**
 * Handles data operations in Sunshine. Acts as a mediator between [WeatherNetworkDataSource]
 * and [WeatherDao]
 */
class SunshineRepository private constructor(private val mWeatherDao: WeatherDao,
                                             private val mWeatherNetworkDataSource: WeatherNetworkDataSource,
                                             private val mExecutors: AppExecutors) {
    private var mInitialized = false


    init {
        val networkData = mWeatherNetworkDataSource.currentWeatherForecasts
        networkData.observeForever { newForecastsFromNetwork ->
            mExecutors.diskIO().execute {
                // Insert our new weather data into Sunshine's database
                mWeatherDao.bulkInsert(newForecastsFromNetwork?.first()!!)
                Log.d(LOG_TAG, "New values inserted")
            }
        }
    }

    /**
     * Creates periodic sync tasks and checks to see if an immediate sync is required. If an
     * immediate sync is required, this method will take care of making sure that sync occurs.
     */
    @Synchronized
    fun initializeData() {

        // Only perform initialization once per app lifetime. If initialization has already been
        // performed, we have nothing to do in this method.
        if (mInitialized) return
        mInitialized = true

        startFetchWeatherService()
    }

    /**
     * Database related operations
     */

    fun getCurrentWeatherForecasts(): LiveData<List<ListWeatherEntry>> {
        initializeData()
        val today = SunshineDateUtils.getNormalizedUtcDateForToday()
        return mWeatherDao.getCurrentWeatherForecasts(today)
    }

    fun getWeatherByDate(date: Date): LiveData<WeatherEntry> {
        initializeData()
        return mWeatherDao.getWeatherByDate(date)
    }

    /**
     * Deletes old weather data because we don't need to keep multiple days' data
     */
    private fun deleteOldData() {
        val today = SunshineDateUtils.getNormalizedUtcDateForToday()
        mWeatherDao.deleteOldWeather(today)
    }

    /**
     * Checks if there are enough days of future weather for the app to display all the needed data.
     *
     * @return Whether a fetch is needed
     */
    private fun isFetchNeeded(): Boolean {
        val today = SunshineDateUtils.getNormalizedUtcDateForToday()
        val count = mWeatherDao.countAllFutureWeather(today)
        return count < WeatherNetworkDataSource.NUM_DAYS
    }

    /**
     * Network related operation
     */

    private fun startFetchWeatherService() {
        mWeatherNetworkDataSource.startFetchWeatherService()
    }

    companion object {
        private val LOG_TAG = SunshineRepository::class.java.simpleName

        // For Singleton instantiation
        private val LOCK = Any()
        private var sInstance: SunshineRepository? = null

        @Synchronized
        @JvmStatic
        fun getInstance(
                weatherDao: WeatherDao, weatherNetworkDataSource: WeatherNetworkDataSource,
                executors: AppExecutors): SunshineRepository {
            Log.d(LOG_TAG, "Getting the repository")
            if (sInstance == null) {
                synchronized(LOCK) {
                    sInstance = SunshineRepository(weatherDao, weatherNetworkDataSource,
                            executors)
                    Log.d(LOG_TAG, "Made new repository")
                }
            }
            return sInstance!!
        }
    }

}