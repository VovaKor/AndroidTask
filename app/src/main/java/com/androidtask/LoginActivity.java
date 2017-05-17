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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setting default screen to login.xml
        setContentView(R.layout.login);

        CredentialStorage.INSTANCE.setContext(getApplicationContext());

        Button registerButton = (Button) findViewById(R.id.btnRegister);
        Button loginButton = (Button) findViewById(R.id.btnLogin);
        emailView = (EditText) findViewById(R.id.login_email);
        passwordView = (EditText) findViewById(R.id.login_password);

        registerButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // Switching to Register screen
                Intent i = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(i);
                finish();
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

                boolean cancel = false;
                View focusView = null;

                // Check for a valid email address.
                if (TextUtils.isEmpty(email)) {
                    emailView.setError(getString(R.string.error_field_empty));
                    focusView = emailView;
                    cancel = true;
                } else if (!CredentialStorage.INSTANCE.isEmailValid(email)) {
                    emailView.setError(getString(R.string.error_invalid_email));
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
                if (cancel) {
                    // There was an error; don't attempt login and focus the first
                    // form field with an error.
                    focusView.requestFocus();
                } else {
                    // Perform the user login attempt.
                    Intent i = new Intent(getApplicationContext(), WelcomeActivity.class);
                    i.putExtra("email",email);
                    startActivity(i);
                    finish();
                }



            }
        });
    }
}
