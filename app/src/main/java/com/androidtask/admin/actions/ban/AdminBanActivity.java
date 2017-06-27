package com.androidtask.admin.actions.ban;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.androidtask.R;
import com.androidtask.UseCaseHandler;
import com.androidtask.admin.actions.AdminActionsActivity;
import com.androidtask.domain.models.User;
import com.androidtask.domain.usecases.GetUser;
import com.androidtask.domain.usecases.UpdateUser;
import com.androidtask.repository.UsersRepository;
import com.androidtask.repository.local.UsersLocalDataSource;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by vova on 25.06.17.
 */

public class AdminBanActivity extends Activity implements AdminBanContract.View{
    private AdminBanContract.Presenter mPresenter;
    private EditText mBanDays;
    private TextView mBannedTime;
    private TextView mBannedReason;
    private EditText mBanReason;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_ban);

        String userId = getIntent().getStringExtra(getString(R.string.EXTRA_USER_ID));
        mBannedTime = (TextView) findViewById(R.id.banned_time);
        mBannedReason = (TextView) findViewById(R.id.banned_reason);
        mBanDays = (EditText) findViewById(R.id.ban_time);
        mBanReason = (EditText) findViewById(R.id.ban_reason);

        mPresenter = new AdminBanPresenter(
                UseCaseHandler.getInstance(),
                this,
                userId,
                new GetUser(UsersRepository.getInstance(UsersLocalDataSource.getInstance(getApplicationContext()))),
                new UpdateUser(UsersRepository.getInstance(UsersLocalDataSource.getInstance(getApplicationContext()))));
        mPresenter.start();
        final Button updateB = (Button) findViewById(R.id.btnUpdate);
        updateB.setEnabled(false);
        mBanDays.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                boolean enable = s.length() != 0;
                updateB.setEnabled(enable);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        updateB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.updateUser(mBanDays.getText().toString(),mBanReason.getText().toString());
            }
        });

        Button exitB = (Button) findViewById(R.id.btnExit);
        exitB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.cancel();
            }
        });
    }

    @Override
    public void showUserDetails(User user) {
        if (user.getBan_date()!=null){
            mBannedTime.setText(user.getBan_date().toString());
        }
        if (user.getBan_reason()!=null){
            mBannedReason.setText(user.getBan_reason());
        }
    }

    @Override
    public void showUserBannedSuccess() {
        Snackbar.make(mBannedTime,getString(R.string.user_banned),Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void setPresenter(AdminBanContract.Presenter presenter) {
        mPresenter = checkNotNull(presenter);
    }
}
