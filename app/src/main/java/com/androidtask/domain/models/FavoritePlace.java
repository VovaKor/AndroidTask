package com.androidtask.domain.models;

import android.support.annotation.NonNull;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.DaoException;
import com.androidtask.repository.local.persistence.DaoSession;
import com.androidtask.repository.local.persistence.FavoritePlaceDao;

/**
 * Created by vova on 29.06.17.
 */
@Entity
public class FavoritePlace {
    @Id
    private String id;
    @NonNull
    private String title;
    @NonNull
    private String city;
    private String description;
    @NonNull
    private String photo;
    @NonNull
    private Double latitude;
    @NonNull
    private Double longitude;

    private String userId;
    @Generated(hash = 229035298)
    public FavoritePlace(String id, @NonNull String title, @NonNull String city,
            String description, @NonNull String photo, @NonNull Double latitude,
            @NonNull Double longitude, String userId) {
        this.id = id;
        this.title = title;
        this.city = city;
        this.description = description;
        this.photo = photo;
        this.latitude = latitude;
        this.longitude = longitude;
        this.userId = userId;
    }

    @Generated(hash = 1800900423)
    public FavoritePlace() {
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCity() {
        return this.city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPhoto() {
        return this.photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public Double getLatitude() {
        return this.latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return this.longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getUserId() {
        return this.userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
