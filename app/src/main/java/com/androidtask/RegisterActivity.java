package com.androidtask;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by vova on 15.05.17.
 */

public class RegisterActivity extends Activity {
    private Button submit;
    private EditText emailView;
    private EditText passwordView;
    private EditText passwordCopyView;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set View to register.xml
        setContentView(R.layout.register);

        submit = (Button) findViewById(R.id.btnSubmitRegister);
        emailView = (EditText) findViewById(R.id.reg_email);
        passwordView = (EditText) findViewById(R.id.reg_password);
        passwordCopyView = (EditText) findViewById(R.id.repeat_password);

        submit.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                // Store values at the time of the login attempt.
                String email = emailView.getText().toString();
                String password = passwordView.getText().toString();
                String passwordCopy = passwordCopyView.getText().toString();

                boolean cancel = isInputValid(email, password, passwordCopy);

                if (!cancel) {
                    // Perform the user register attempt.
                    CredentialStorage.INSTANCE.addUser(email, password);
                    Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(i);
                    finish();
                }


            }
        });

    }

    private boolean isInputValid(String email, String password, String passwordCopy) {
        boolean cancel = false;
        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
           Toast.makeText(getApplicationContext(),
                    getString(R.string.error_field_empty), Toast.LENGTH_LONG)
                    .show();
            cancel = true;
        }else if (email.length()< 6){
            Toast.makeText(getApplicationContext(),
                    getString(R.string.error_short_email), Toast.LENGTH_LONG)
                    .show();
            cancel = true;
        }
        else if (CredentialStorage.INSTANCE.isEmailValid(email)) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.error_email_exist), Toast.LENGTH_LONG)
                    .show();
            cancel = true;
        }
        // Check for a valid password.
        if (TextUtils.isEmpty(password)||TextUtils.isEmpty(passwordCopy)) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.error_field_empty), Toast.LENGTH_LONG)
                    .show();
            cancel = true;
        }else if (password.length()< 4){
            Toast.makeText(getApplicationContext(),
                    getString(R.string.error_short_password), Toast.LENGTH_LONG)
                    .show();
            cancel = true;
        } else if (!TextUtils.equals(password, passwordCopy)) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.error_password_mismatch), Toast.LENGTH_LONG)
                    .show();
            cancel = true;
        }
        return cancel;
    }

}
