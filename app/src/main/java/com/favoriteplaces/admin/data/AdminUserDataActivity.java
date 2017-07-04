package com.favoriteplaces.admin.data;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.TextView;

import com.favoriteplaces.R;
import com.favoriteplaces.UseCaseHandler;
import com.favoriteplaces.domain.models.User;
import com.favoriteplaces.domain.models.UserDetails;
import com.favoriteplaces.domain.usecases.GetUser;
import com.favoriteplaces.repository.UsersRepository;
import com.favoriteplaces.repository.local.UsersLocalDataSource;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by vova on 25.06.17.
 */

public class AdminUserDataActivity extends Activity implements AdminUserDataContract.View{
    private AdminUserDataContract.Presenter mPresenter;
    private TextView mNickname;
    private TextView mFirstName;
    private TextView mPatronimic;
    private TextView mLastName;
    private TextView mPhone;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_user_data);

        String userId = getIntent().getStringExtra(getString(R.string.EXTRA_USER_ID));
        mNickname = (TextView) findViewById(R.id.nickname);
        mFirstName = (TextView) findViewById(R.id.first_name);
        mPatronimic = (TextView) findViewById(R.id.patronimic);
        mLastName = (TextView) findViewById(R.id.last_name);
        mPhone = (TextView) findViewById(R.id.user_phone);

        mPresenter = new AdminUserDataPresenter(
                UseCaseHandler.getInstance(),
                this,
                userId,
                new GetUser(UsersRepository.getInstance(UsersLocalDataSource.getInstance(getApplicationContext()))));
        mPresenter.start();
    }

    @Override
    public void showUserDetails(User user) {
        UserDetails userDetails = user.getUserDetails();
        mNickname.setText(user.getNick_name());
        if (userDetails!=null) {
            mFirstName.setText(userDetails.getFirst_name());
            mPatronimic.setText(userDetails.getPatronymic());
            mLastName.setText(userDetails.getLast_name());
            mPhone.setText(userDetails.getPhone());
        }
    }

    @Override
    public void setPresenter(AdminUserDataContract.Presenter presenter) {
        mPresenter = checkNotNull(presenter);
    }

}
