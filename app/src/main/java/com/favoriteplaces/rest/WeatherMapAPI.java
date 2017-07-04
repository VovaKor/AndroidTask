package com.favoriteplaces.rest;

import com.favoriteplaces.weathermap.CoordResponse;

import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by vova on 03.07.17.
 */

public interface WeatherMapAPI {
    String BASE_URL = "http://api.openweathermap.org/data/2.5/";

    /**
     *Temperature in Celsius
     */
    @GET("weather?units=metric")
    Single<CoordResponse> getForecastByCoord(
            @Query("lat") Double latitude,
            @Query("lon") Double longitude,
            @Query("appid") String appid);
}
