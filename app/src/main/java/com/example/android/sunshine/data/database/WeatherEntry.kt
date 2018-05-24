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

package com.example.android.sunshine.data.database

import android.arch.persistence.room.Entity
import android.arch.persistence.room.Index
import android.arch.persistence.room.PrimaryKey
import java.util.Date

@Entity(tableName = "weather", indices = arrayOf(Index(value = arrayOf("date"), unique = true)))
data class WeatherEntry
/**
 * This constructor is used by OpenWeatherJsonParser. When the network fetch has JSON data, it
 * converts this data to WeatherEntry objects using this constructor.
 * @param weatherIconId Image id for weather
 * @param date Date of weather
 * @param min Min temperature
 * @param max Max temperature
 * @param humidity Humidity for the day
 * @param pressure Barometric pressure
 * @param wind Wind speed
 * @param degrees Wind direction
 */
(
        val weatherIconId: Int,
        val date: Date,
        val min: Double,
        val max: Double,
        val humidity: Double,
        val pressure: Double,
        val wind: Double,
        val degrees: Double
) {

    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}
