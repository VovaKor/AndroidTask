package com.androidtask;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by vova on 16.05.17.
 */

public class WelcomeActivity extends Activity {
    private Button logoutButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setting default screen to welcome.xml
        setContentView(R.layout.welcome);

        String emailFromLogin = getIntent().getStringExtra("email");
        TextView textView = (TextView) findViewById(R.id.email);
        textView.setText(emailFromLogin);

        logoutButton = (Button) findViewById(R.id.btnLogout);
        logoutButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(i);
                finish();
            }
        });
    }
}
