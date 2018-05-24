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

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity

import com.example.android.sunshine.R
import com.example.android.sunshine.data.database.WeatherEntry
import com.example.android.sunshine.databinding.ActivityDetailBinding
import com.example.android.sunshine.utilities.SunshineDateUtils
import com.example.android.sunshine.utilities.SunshineWeatherUtils

import java.util.Date
import com.example.android.sunshine.AppExecutors



/**
 * Displays single day's forecast
 */
class DetailActivity : AppCompatActivity() {

    /*
     * This field is used for data binding. Normally, we would have to call findViewById many
     * times to get references to the Views in this Activity. With data binding however, we only
     * need to call DataBindingUtil.setContentView and pass in a Context and a layout, as we do
     * in onCreate of this class. Then, we can access all of the Views in our layout
     * programmatically without cluttering up the code with findViewById.
     */
    private var mDetailBinding: ActivityDetailBinding? = null

    private var viewModel: DetailActivityViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mDetailBinding = DataBindingUtil.setContentView(this, R.layout.activity_detail)
        val timestamp = intent.getLongExtra(WEATHER_ID_EXTRA, -1)
        val date = Date(timestamp)

        viewModel = ViewModelProviders.of(this).get(DetailActivityViewModel::class.java)

        viewModel?.weather?.observe(this, Observer<WeatherEntry> { weatherEntry ->
            // Update the UI
            weatherEntry?.let { bindWeatherToUI(it) }
        })

        AppExecutors.getInstance().diskIO().execute {
            try {

                // Pretend this is the network loading data
                Thread.sleep(4000)
                val today = SunshineDateUtils.getNormalizedUtcDateForToday()
                var pretendWeatherFromDatabase = WeatherEntry(1, today, 88.0, 99.0, 71.0, 1030.0, 74.0, 5.0)
                viewModel?.setWeather(pretendWeatherFromDatabase)

                Thread.sleep(2000)
                pretendWeatherFromDatabase = WeatherEntry(1, today, 50.0, 60.0, 46.0, 1044.0, 70.0, 100.0)
                viewModel?.setWeather(pretendWeatherFromDatabase)

            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }

    }

    private fun bindWeatherToUI(weatherEntry: WeatherEntry) {
        /****************
         * Weather Icon *
         */

        val weatherId = weatherEntry.weatherIconId
        val weatherImageId = SunshineWeatherUtils.getLargeArtResourceIdForWeatherCondition(weatherId)

        /* Set the resource ID on the icon to display the art */
        mDetailBinding!!.primaryInfo?.weatherIcon?.setImageResource(weatherImageId)

        /****************
         * Weather Date *
         */
        /*
         * The date that is stored is a GMT representation at midnight of the date when the weather
         * information was loaded for.
         *
         * When displaying this date, one must add the GMT offset (in milliseconds) to acquire
         * the date representation for the local date in local time.
         * SunshineDateUtils#getFriendlyDateString takes care of this for us.
         */
        val localDateMidnightGmt = weatherEntry.date.time
        val dateText = SunshineDateUtils.getFriendlyDateString(this@DetailActivity, localDateMidnightGmt, true)
        mDetailBinding!!.primaryInfo?.date?.text = dateText

        /***********************
         * Weather Description *
         */
        /* Use the weatherId to obtain the proper description */
        val description = SunshineWeatherUtils.getStringForWeatherCondition(this@DetailActivity, weatherId)

        /* Create the accessibility (a11y) String from the weather description */
        val descriptionA11y = getString(R.string.a11y_forecast, description)

        /* Set the text and content description (for accessibility purposes) */
        mDetailBinding!!.primaryInfo?.weatherDescription?.text = description
        mDetailBinding!!.primaryInfo?.weatherDescription?.contentDescription = descriptionA11y

        /* Set the content description on the weather image (for accessibility purposes) */
        mDetailBinding!!.primaryInfo?.weatherIcon?.contentDescription = descriptionA11y

        /**************************
         * High (max) temperature *
         */

        val maxInCelsius = weatherEntry.max

        /*
         * If the user's preference for weather is fahrenheit, formatTemperature will convert
         * the temperature. This method will also append either °C or °F to the temperature
         * String.
         */
        val highString = SunshineWeatherUtils.formatTemperature(this@DetailActivity, maxInCelsius)

        /* Create the accessibility (a11y) String from the weather description */
        val highA11y = getString(R.string.a11y_high_temp, highString)

        /* Set the text and content description (for accessibility purposes) */
        mDetailBinding!!.primaryInfo?.highTemperature?.text = highString
        mDetailBinding!!.primaryInfo?.highTemperature?.contentDescription = highA11y

        /*************************
         * Low (min) temperature *
         */

        val minInCelsius = weatherEntry.min
        /*
         * If the user's preference for weather is fahrenheit, formatTemperature will convert
         * the temperature. This method will also append either °C or °F to the temperature
         * String.
         */
        val lowString = SunshineWeatherUtils.formatTemperature(this@DetailActivity, minInCelsius)

        val lowA11y = getString(R.string.a11y_low_temp, lowString)

        /* Set the text and content description (for accessibility purposes) */
        mDetailBinding!!.primaryInfo?.lowTemperature?.text = lowString
        mDetailBinding!!.primaryInfo?.lowTemperature?.contentDescription = lowA11y

        /************
         * Humidity *
         */

        val humidity = weatherEntry.humidity
        val humidityString = getString(R.string.format_humidity, humidity)
        val humidityA11y = getString(R.string.a11y_humidity, humidityString)

        /* Set the text and content description (for accessibility purposes) */
        mDetailBinding!!.extraDetails?.humidity?.text = humidityString
        mDetailBinding!!.extraDetails?.humidity?.contentDescription = humidityA11y

        mDetailBinding!!.extraDetails?.humidityLabel?.contentDescription = humidityA11y

        /****************************
         * Wind speed and direction *
         */
        /* Read wind speed (in MPH) and direction (in compass degrees)*/
        val windSpeed = weatherEntry.wind
        val windDirection = weatherEntry.degrees
        val windString = SunshineWeatherUtils.getFormattedWind(this@DetailActivity, windSpeed, windDirection)
        val windA11y = getString(R.string.a11y_wind, windString)

        /* Set the text and content description (for accessibility purposes) */
        mDetailBinding!!.extraDetails?.windMeasurement?.text = windString
        mDetailBinding!!.extraDetails?.windMeasurement?.contentDescription = windA11y
        mDetailBinding!!.extraDetails?.windLabel?.contentDescription = windA11y

        /************
         * Pressure *
         */
        val pressure = weatherEntry.pressure

        /*
         * Format the pressure text using string resources. The reason we directly access
         * resources using getString rather than using a method from SunshineWeatherUtils as
         * we have for other data displayed in this Activity is because there is no
         * additional logic that needs to be considered in order to properly display the
         * pressure.
         */
        val pressureString = getString(R.string.format_pressure, pressure)

        val pressureA11y = getString(R.string.a11y_pressure, pressureString)

        /* Set the text and content description (for accessibility purposes) */
        mDetailBinding!!.extraDetails?.pressure?.text = pressureString
        mDetailBinding!!.extraDetails?.pressure?.contentDescription = pressureA11y
        mDetailBinding!!.extraDetails?.pressureLabel?.contentDescription = pressureA11y
    }

    companion object {

        val WEATHER_ID_EXTRA = "WEATHER_ID_EXTRA"
    }
}