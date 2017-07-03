package com.favoriteplaces.repository.local.persistence;

import com.favoriteplaces.domain.models.Roles;

import org.greenrobot.greendao.converter.PropertyConverter;

/**
 * Created by vova on 24.06.17.
 */

public class RoleTypeConverter implements PropertyConverter<Roles, String> {
    @Override
    public Roles convertToEntityProperty(String databaseValue) {
        return Roles.valueOf(databaseValue);
    }

    @Override
    public String convertToDatabaseValue(Roles entityProperty) {
        return entityProperty.name();
    }
}
