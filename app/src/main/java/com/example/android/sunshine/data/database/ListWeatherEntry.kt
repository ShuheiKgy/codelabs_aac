package com.example.android.sunshine.data.database

import java.util.Date

/**
 * Simplified [WeatherEntry] which only contains the details needed for the weather list in
 * the [com.example.android.sunshine.ui.list.ForecastAdapter]
 */
class ListWeatherEntry(val id: Int, val weatherIconId: Int, val date: Date, val min: Double, val max: Double)
