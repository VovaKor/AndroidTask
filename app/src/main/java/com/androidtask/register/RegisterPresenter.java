package com.androidtask.register;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.widget.TextView;

import com.androidtask.UseCase;
import com.androidtask.UseCaseHandler;
import com.androidtask.WelcomeActivity;
import com.androidtask.admin.AdminActivity;
import com.androidtask.domain.models.Roles;
import com.androidtask.domain.models.User;
import com.androidtask.domain.usecases.GetUser;
import com.androidtask.domain.usecases.SaveUser;
import com.androidtask.login.LoginActivity;
import com.androidtask.utils.HashGenerator;
import com.androidtask.utils.MD5Generator;
import com.androidtask.utils.SessionManager;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Listens to user actions from the UI ({@link RegisterActivity}), retrieves the data and
 * updates the UI as required.
 */
public class RegisterPresenter implements RegisterContract.Presenter {

    private final RegisterContract.View mRegisterView;
    private final int EMAIL_MIN_LENGTH = 6;
    private final int EMAIL_MAX_LENGTH = 129;
    private final int PASSWORD_MIN_LENGTH = 4;
    private final String EMAIL_REGEXP = "[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?";

    private final GetUser mGetUser;
    private SaveUser mSaveUser;
    private final UseCaseHandler mUseCaseHandler;
    private String mPassword;
    private String mEmail;
    private RegisterActivity.ViewHolder mViewHolder;
    private boolean mCancel;
    private HashGenerator mGenerator;

    /**
     * Creates a presenter for the login view.
     *
     */
    public RegisterPresenter(@NonNull UseCaseHandler useCaseHandler, @NonNull RegisterActivity.ViewHolder holder,
                             @NonNull RegisterContract.View registerView, @NonNull SaveUser saveUser, @NonNull GetUser getUser) {
        mUseCaseHandler = checkNotNull(useCaseHandler, "useCaseHandler cannot be null!");
        mSaveUser = checkNotNull(saveUser, "saveUser cannot be null!");
        mRegisterView = checkNotNull(registerView, "View cannot be null!");
        mGetUser = checkNotNull(getUser, "getUser cannot be null!");
        mViewHolder = checkNotNull(holder, "holder cannot be null!");
        mGenerator = new MD5Generator();
        mRegisterView.setPresenter(this);

    }

    @Override
    public void start() {

        mCancel = false;

        String passwordCopy = mViewHolder.passwordCopyView.getText().toString();
        mEmail = mViewHolder.emailView.getText().toString();
        mPassword = mViewHolder.passwordView.getText().toString();

        if (TextUtils.isEmpty(mEmail)) {
            mRegisterView.showEmptyEmailError();
            mCancel = true;
        }else if (mEmail.length()< EMAIL_MIN_LENGTH){
            mRegisterView.showTooShortEmailError();
            mCancel = true;
        }else if (mEmail.length()> EMAIL_MAX_LENGTH){
            mRegisterView.showTooLongEmailError();
            mCancel = true;
        }else if (!mEmail.matches(EMAIL_REGEXP)){
            mRegisterView.showInvalidEmailError();
            mCancel = true;
        } else if (TextUtils.isEmpty(mPassword)) {
            mRegisterView.showEmptyPasswordError();
            mCancel = true;
        }else if (mPassword.length()< PASSWORD_MIN_LENGTH){
            mRegisterView.showTooShortPasswordError();
            mCancel = true;
        }else if (!TextUtils.equals(mPassword, passwordCopy)) {
            mRegisterView.showPasswordMismatchError();
            mCancel = true;
        }
        if (!mCancel) {
            getUser();

        }

    }

    @Override
    public void getUser() {
        mUseCaseHandler.execute(mGetUser, new GetUser.RequestValues(mEmail),
                new UseCase.UseCaseCallback<GetUser.ResponseValue>() {
                    @Override
                    public void onSuccess(GetUser.ResponseValue response) {

                        mRegisterView.showEmailExistError();

                    }

                    @Override
                    public void onError() {
                        saveUser();
                    }
                });
    }

    @Override
    public void saveUser() {
        User user = new User(mEmail,mGenerator.generate(mPassword),Roles.USER,false);
        mUseCaseHandler.execute(mSaveUser, new SaveUser.RequestValues(user),
                new UseCase.UseCaseCallback<SaveUser.ResponseValue>() {
                    @Override
                    public void onSuccess(SaveUser.ResponseValue response) {
                        startActivity(LoginActivity.class);
                    }

                    @Override
                    public void onError() {
                        mRegisterView.showSaveUserError();
                    }
                });
    }

    @Override
    public void startActivity(Class activityClass) {
        mRegisterView.showActivity(activityClass);
    }
}
