package com.androidtask.user.data;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.androidtask.R;
import com.androidtask.UseCaseHandler;
import com.androidtask.admin.actions.AdminActionsActivity;
import com.androidtask.domain.models.User;
import com.androidtask.domain.models.UserDetails;
import com.androidtask.domain.usecases.GetUser;
import com.androidtask.domain.usecases.UpdateUser;
import com.androidtask.repository.UsersRepository;
import com.androidtask.repository.local.UsersLocalDataSource;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by vova on 27.06.17.
 */

public class UserDataActivity extends Activity implements UserDataContract.View {
    private UserDataContract.Presenter mPresenter;
    private EditText mNickname;
    private EditText mFirstName;
    private EditText mPatronimic;
    private EditText mLastName;
    private EditText mPhone;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_data);

        String userId = getIntent().getStringExtra(getString(R.string.EXTRA_USER_ID));
        mNickname = (EditText) findViewById(R.id.nickname_edit);
        mFirstName = (EditText) findViewById(R.id.first_name_edit);
        mPatronimic = (EditText) findViewById(R.id.patronimic_edit);
        mLastName = (EditText) findViewById(R.id.last_name_edit);
        mPhone = (EditText) findViewById(R.id.user_phone_edit);
        EditTextHolder holder = new EditTextHolder();
        holder.mNickname = mNickname;
        holder.mFirstname = mFirstName;
        holder.mPatronimic = mPatronimic;
        holder.mLastname = mLastName;
        holder.mPhone = mPhone;
        mPresenter = new UserDataPresenter(
                UseCaseHandler.getInstance(),
                this,
                holder,
                userId,
                new GetUser(UsersRepository.getInstance(UsersLocalDataSource.getInstance(getApplicationContext()))),
                new UpdateUser(UsersRepository.getInstance(UsersLocalDataSource.getInstance(getApplicationContext()))));
        mPresenter.start();
        final Button updateB = (Button) findViewById(R.id.btnUpdateUserData);
        updateB.setEnabled(false);
        TextWatcher textWatcher = new UserDataTextWatcher(updateB);
        mFirstName.addTextChangedListener(textWatcher);
        mPatronimic.addTextChangedListener(textWatcher);
        mLastName.addTextChangedListener(textWatcher);
        mPhone.addTextChangedListener(textWatcher);

        updateB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mPresenter.updateUser();
            }
        });

        Button exitB = (Button) findViewById(R.id.btnExitToUserActions);
        exitB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.cancel();
            }
        });
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
    public void showUserUpdatedSuccess() {
        Snackbar.make(mNickname,getString(R.string.user_updated),Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void setPresenter(UserDataContract.Presenter presenter) {
        mPresenter = checkNotNull(presenter);
    }

    private class UserDataTextWatcher implements TextWatcher{
        private final Button mButton;

        public UserDataTextWatcher(Button button) {
            mButton = button;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            boolean enable = s.length() != 0;
            mButton.setEnabled(enable);
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }

    class EditTextHolder {
        public EditText mNickname;
        public EditText mFirstname;
        public EditText mPatronimic;
        public EditText mLastname;
        public EditText mPhone;
    }
}
