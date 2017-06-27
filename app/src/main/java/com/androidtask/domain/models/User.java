package com.androidtask.domain.models;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.androidtask.repository.local.persistence.DaoSession;
import com.androidtask.repository.local.persistence.RoleTypeConverter;
import com.androidtask.repository.local.persistence.UserDao;
import com.androidtask.repository.local.persistence.UserDetailsDao;
import com.google.common.base.Strings;

import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.ToOne;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.DaoException;

/**
 * Created by vova on 22.06.17.
 *
 * Immutable model class for a User.
 *
 */
@Entity(active = true)
public class User {
    @Id
    @NonNull
    private String mEmail;

    @NonNull
    private String mPassword;

    @NonNull
    @Convert(converter = RoleTypeConverter.class, columnType = String.class)
    private Roles mRole;

    @NonNull
    private Boolean mMarked;
    @NonNull
    private String nick_name;

    private java.util.Date ban_date;
    private String ban_reason;
    private String  id_user_details;

    @ToOne(joinProperty = "id_user_details")
    private UserDetails userDetails;

    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    @Generated(hash = 1507654846)
    private transient UserDao myDao;

    @Generated(hash = 1016781585)
    private transient String userDetails__resolvedKey;

    public User(@NonNull String mEmail, @NonNull String mPassword, @NonNull Roles mRole, @NonNull boolean mMarked) {
        this.mEmail = mEmail;
        this.mPassword = mPassword;
        this.mRole = mRole;
        this.mMarked = mMarked;
        this.nick_name = generateNickname();
    }

    @Generated(hash = 1191603949)
    public User(@NonNull String mEmail, @NonNull String mPassword, @NonNull Roles mRole, @NonNull Boolean mMarked,
            @NonNull String nick_name, java.util.Date ban_date, String ban_reason, String id_user_details) {
        this.mEmail = mEmail;
        this.mPassword = mPassword;
        this.mRole = mRole;
        this.mMarked = mMarked;
        this.nick_name = nick_name;
        this.ban_date = ban_date;
        this.ban_reason = ban_reason;
        this.id_user_details = id_user_details;
    }

    @Generated(hash = 586692638)
    public User() {
    }

    @NonNull
    public Roles getRole() {
        return mRole;
    }

    @NonNull
    public String getPassword() {
        return mPassword;
    }

    @NonNull
    public String getId() {
        return mEmail;
    }

    @Nullable
    public String generateNickname() {
        if (!Strings.isNullOrEmpty(mEmail)) {
            return mEmail.substring(0,mEmail.indexOf("@"));
        } else {
            return mRole.toString();
        }
    }

    public boolean isMarked() {
        return mMarked;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (!mEmail.equals(user.mEmail)) return false;
        if (!mPassword.equals(user.mPassword)) return false;
        if (mRole != user.mRole) return false;
        if (!mMarked.equals(user.mMarked)) return false;
        if (!nick_name.equals(user.nick_name)) return false;
        if (ban_date != null ? !ban_date.equals(user.ban_date) : user.ban_date != null)
            return false;
        if (ban_reason != null ? !ban_reason.equals(user.ban_reason) : user.ban_reason != null)
            return false;
        if (id_user_details != null ? !id_user_details.equals(user.id_user_details) : user.id_user_details != null)
            return false;
        return userDetails != null ? userDetails.equals(user.userDetails) : user.userDetails == null;

    }

    @Override
    public int hashCode() {
        int result = mEmail.hashCode();
        result = 31 * result + mPassword.hashCode();
        result = 31 * result + mRole.hashCode();
        result = 31 * result + mMarked.hashCode();
        result = 31 * result + nick_name.hashCode();
        result = 31 * result + (ban_date != null ? ban_date.hashCode() : 0);
        result = 31 * result + (ban_reason != null ? ban_reason.hashCode() : 0);
        result = 31 * result + (id_user_details != null ? id_user_details.hashCode() : 0);
        result = 31 * result + (userDetails != null ? userDetails.hashCode() : 0);
        return result;
    }

    public String getMEmail() {
        return this.mEmail;
    }

    public void setMEmail(String mEmail) {
        this.mEmail = mEmail;
    }

    public String getMPassword() {
        return this.mPassword;
    }

    public void setMPassword(String mPassword) {
        this.mPassword = mPassword;
    }

    public Roles getMRole() {
        return this.mRole;
    }

    public void setMRole(Roles mRole) {
        this.mRole = mRole;
    }

    public Boolean getMMarked() {
        return this.mMarked;
    }

    public void setMMarked(Boolean mMarked) {
        this.mMarked = mMarked;
    }

    public String getNick_name() {
        return this.nick_name;
    }

    public void setNick_name(String nick_name) {
        this.nick_name = nick_name;
    }

    public java.util.Date getBan_date() {
        return this.ban_date;
    }

    public void setBan_date(java.util.Date ban_date) {
        this.ban_date = ban_date;
    }

    public String getBan_reason() {
        return this.ban_reason;
    }

    public void setBan_reason(String ban_reason) {
        this.ban_reason = ban_reason;
    }

    public String getId_user_details() {
        return this.id_user_details;
    }

    public void setId_user_details(String id_user_details) {
        this.id_user_details = id_user_details;
    }

    /** To-one relationship, resolved on first access. */
    @Generated(hash = 667580943)
    public UserDetails getUserDetails() {
        String __key = this.id_user_details;
        if (userDetails__resolvedKey == null || userDetails__resolvedKey != __key) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            UserDetailsDao targetDao = daoSession.getUserDetailsDao();
            UserDetails userDetailsNew = targetDao.load(__key);
            synchronized (this) {
                userDetails = userDetailsNew;
                userDetails__resolvedKey = __key;
            }
        }
        return userDetails;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 1596412591)
    public void setUserDetails(UserDetails userDetails) {
        synchronized (this) {
            this.userDetails = userDetails;
            id_user_details = userDetails == null ? null : userDetails.getId();
            userDetails__resolvedKey = id_user_details;
        }
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#delete(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 128553479)
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.delete(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#refresh(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 1942392019)
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.refresh(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#update(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 713229351)
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.update(this);
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 2059241980)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getUserDao() : null;
    }
}
