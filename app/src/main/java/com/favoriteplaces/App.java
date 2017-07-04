package com.favoriteplaces;

import android.app.Application;

import com.favoriteplaces.rest.WeatherMapAPI;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by vova on 03.07.17.
 */

public class App extends Application {

    private static WeatherMapAPI weatherMapAPI;
    private Retrofit retrofit;

    @Override
    public void onCreate() {
        super.onCreate();
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                .create();

        retrofit = new Retrofit.Builder()
                .baseUrl(WeatherMapAPI.BASE_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        weatherMapAPI = retrofit.create(WeatherMapAPI.class);
    }

    public static WeatherMapAPI getWeatherMapApi() {
        return weatherMapAPI;
    }
}
