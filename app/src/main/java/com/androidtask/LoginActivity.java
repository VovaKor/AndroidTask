package com.androidtask;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


/**
 * Created by vova on 16.05.17.
 */

public class LoginActivity extends Activity{
    private EditText emailView;
    private EditText passwordView;
    private View focusView;
    private SessionManager session;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session = new SessionManager(getApplicationContext());

        if (session.isLoggedIn()) {
            // User is already logged in. Take him to Welcome activity
            loadActivity(WelcomeActivity.class);
        }

        setContentView(R.layout.login);

        CredentialStorage.INSTANCE.setContext(getApplicationContext());

        Button registerButton = (Button) findViewById(R.id.btnRegister);
        Button loginButton = (Button) findViewById(R.id.btnLogin);
        emailView = (EditText) findViewById(R.id.login_email);
        passwordView = (EditText) findViewById(R.id.login_password);

        registerButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // Switching to Register screen
                loadActivity(RegisterActivity.class);
            }
        });
        loginButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // Reset errors.
                emailView.setError(null);
                passwordView.setError(null);

                // Store values at the time of the login attempt.
                String email = emailView.getText().toString();
                String password = passwordView.getText().toString();

                focusView = null;
                boolean cancel = isInputValid(email, password);

                if (cancel) {
                    // There was an error; don't attempt login and focus the first
                    // form field with an error.
                    focusView.requestFocus();
                } else {
                    // Perform the user login attempt.
                    session.createLoginSession(email);
                    loadActivity(WelcomeActivity.class);
                }



            }
        });
    }

    //TODO: extract this method to separate validation abstraction
    private boolean isInputValid(String email, String password) {
        boolean cancel = false;
        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            emailView.setError(getString(R.string.error_field_empty));
            focusView = emailView;
            cancel = true;
        } else if (!CredentialStorage.INSTANCE.isEmailExist(email)) {
            emailView.setError(getString(R.string.error_email_not_registered));
            focusView = emailView;
            cancel = true;
        }
        // Check for a valid password.
        if (TextUtils.isEmpty(password)) {
            passwordView.setError(getString(R.string.error_field_empty));
            focusView = passwordView;
            cancel = true;
        } else if (!CredentialStorage.INSTANCE.isPasswordValid(email, password)) {
            passwordView.setError(getString(R.string.error_incorrect_password));
            focusView = passwordView;
            cancel = true;
        }
        return cancel;
    }

    private void loadActivity(Class clazz) {
        Intent intent = new Intent(getApplicationContext(), clazz);
        startActivity(intent);
        finish();
    }
}
