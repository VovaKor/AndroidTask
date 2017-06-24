package com.androidtask.login;

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
import com.androidtask.utils.HashGenerator;
import com.androidtask.utils.MD5Generator;
import com.androidtask.utils.SessionManager;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Listens to user actions from the UI ({@link LoginActivity}), retrieves the data and
 * updates the UI as required.
 */
public class LoginPresenter implements LoginContract.Presenter {

    private final LoginContract.View mLoginView;

    private final GetUser mGetUser;
    private SessionManager mSession;
    private final UseCaseHandler mUseCaseHandler;
    private User mUser;
    private String mEmail;
    private String mPassword;
    private TextView mEmailView;
    private TextView mPasswordView;
    private boolean mCancel;
    private HashGenerator mGenerator;

    /**
     * Creates a presenter for the login view.
     *
     */
    public LoginPresenter(@NonNull UseCaseHandler useCaseHandler, @NonNull TextView email, @NonNull TextView password,
                          @NonNull LoginContract.View loginView, @NonNull SessionManager session, @NonNull GetUser getUser) {
        mUseCaseHandler = checkNotNull(useCaseHandler, "useCaseHandler cannot be null!");
        mEmailView = email;
        mPasswordView = password;
        mSession = session;
        mLoginView = checkNotNull(loginView, "View cannot be null!");
        mGetUser = checkNotNull(getUser, "getUser cannot be null!");
        mGenerator = new MD5Generator();
        mLoginView.setPresenter(this);

    }

    @Override
    public void start() {
        mCancel = false;
        // Store values at the time of the login attempt.
        mEmail = mEmailView.getText().toString();
        mPassword = mPasswordView.getText().toString();
        // Check for a valid email address.
        if (TextUtils.isEmpty(mEmail)) {
            mLoginView.showEmptyEmailError();
            mCancel = true;
        } else if (TextUtils.isEmpty(mPassword)) {
            mLoginView.showEmptyPasswordError();
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
                        mUser = response.getUser();

                        if (!TextUtils.equals(mUser.getPassword(), mGenerator.generate(mPassword))) {
                            mLoginView.showInvalidPasswordError();
                            mCancel = true;
                        }
                        if (!mCancel){
                            // Perform the user login attempt.
                            mSession.createLoginSession(mEmail, mUser.getRole());

                            if (mUser.getRole()== Roles.ADMIN){
                                mLoginView.showActivity(AdminActivity.class);
                            }else {
                                mLoginView.showActivity(WelcomeActivity.class);
                            }
                        }
                    }

                    @Override
                    public void onError() {
                        mLoginView.showInvalidEmailError();
                        mCancel = true;
                    }
                });
    }

    @Override
    public void startActivity(Class activityClass) {
        mLoginView.showActivity(activityClass);
    }
}
