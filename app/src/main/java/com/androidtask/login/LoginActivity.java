package com.androidtask.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.androidtask.R;
import com.androidtask.register.RegisterActivity;
import com.androidtask.UseCaseHandler;
import com.androidtask.domain.models.Roles;
import com.androidtask.domain.usecases.GetUser;
import com.androidtask.repository.UsersRepository;
import com.androidtask.repository.local.UsersLocalDataSource;
import com.androidtask.utils.SessionManager;
import com.androidtask.WelcomeActivity;
import com.androidtask.admin.AdminActivity;

import static com.google.common.base.Preconditions.checkNotNull;


/**
 * Created by vova on 16.05.17.
 */

public class LoginActivity extends Activity implements LoginContract.View{
    private EditText emailView;
    private EditText passwordView;
    private View focusView;
    private SessionManager session;
    private LoginContract.Presenter mPresenter;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session = SessionManager.getInstance(getApplicationContext());
        setContentView(R.layout.login);

        emailView = (EditText) findViewById(R.id.login_email);
        passwordView = (EditText) findViewById(R.id.login_password);

// Create the presenter
        mPresenter = new LoginPresenter(UseCaseHandler.getInstance(),
                emailView,
                passwordView,
                this,
                session,
                new GetUser(UsersRepository.getInstance(UsersLocalDataSource.getInstance(getApplicationContext()))));

        if (session.isLoggedIn()){
            if (session.getUserRole()==Roles.ADMIN){
            mPresenter.startActivity(AdminActivity.class);
            }else {
            mPresenter.startActivity(WelcomeActivity.class);
            }
        }

        Button registerButton = (Button) findViewById(R.id.btnRegister);
        registerButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // Switching to Register screen
                mPresenter.startActivity(RegisterActivity.class);
            }
        });
        Button loginButton = (Button) findViewById(R.id.btnLogin);
        loginButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // Reset errors.
                emailView.setError(null);
                passwordView.setError(null);

                focusView = null;
                mPresenter.start();
            }
        });
    }

    public void showActivity(Class clazz) {
        Intent intent = new Intent(getApplicationContext(), clazz);
        startActivity(intent);

    }

    @Override
    public void showEmptyEmailError() {
        emailView.setError(getString(R.string.error_field_empty));
        focusView = emailView;
        focusView.requestFocus();
    }

    @Override
    public void showEmptyPasswordError() {
        passwordView.setError(getString(R.string.error_field_empty));
        focusView = passwordView;
        focusView.requestFocus();
    }

    @Override
    public void showInvalidEmailError() {
        emailView.setError(getString(R.string.error_email_not_registered));
        focusView = emailView;
        focusView.requestFocus();
    }

    @Override
    public void showInvalidPasswordError() {
        passwordView.setError(getString(R.string.error_incorrect_password));
        focusView = passwordView;
        focusView.requestFocus();
    }

    @Override
    public void setPresenter(LoginContract.Presenter presenter) {
        mPresenter = checkNotNull(presenter);
    }
}
