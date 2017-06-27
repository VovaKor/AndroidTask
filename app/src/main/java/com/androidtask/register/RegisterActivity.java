package com.androidtask.register;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.androidtask.R;
import com.androidtask.UseCaseHandler;
import com.androidtask.domain.usecases.GetUser;
import com.androidtask.domain.usecases.InsertUser;
import com.androidtask.repository.UsersRepository;
import com.androidtask.repository.local.UsersLocalDataSource;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by vova on 15.05.17.
 */

public class RegisterActivity extends Activity implements RegisterContract.View {
      private ViewHolder mHolder;
   // private View focusView;
    private RegisterContract.Presenter mPresenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.register);
        mHolder = new ViewHolder();
        mHolder.emailView = (EditText) findViewById(R.id.reg_email);
        mHolder.passwordView = (EditText) findViewById(R.id.reg_password);
        mHolder.passwordCopyView = (EditText) findViewById(R.id.repeat_password);
        Button submit = (Button) findViewById(R.id.btnSubmitRegister);
        // Create the presenter
        mPresenter = new RegisterPresenter(UseCaseHandler.getInstance(), mHolder, this,
                new InsertUser(UsersRepository.getInstance(UsersLocalDataSource.getInstance(getApplicationContext()))),
                new GetUser(UsersRepository.getInstance(UsersLocalDataSource.getInstance(getApplicationContext()))));

        submit.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // Reset errors.
                mHolder.emailView.setError(null);
                mHolder.passwordView.setError(null);
                mHolder.passwordCopyView.setError(null);
               // focusView = null;
                mPresenter.start();
            }
        });

    }

    @Override
    public void showEmptyEmailError() {
        mHolder.emailView.setError(getString(R.string.error_field_empty));

    }

    @Override
    public void showPasswordMismatchError() {
        mHolder.passwordCopyView.setError(getString(R.string.error_password_mismatch));
    }

    @Override
    public void showTooShortPasswordError() {
        mHolder.passwordView.setError(getString(R.string.error_short_password));
    }

    @Override
    public void showEmptyPasswordError() {
        mHolder.passwordView.setError(getString(R.string.error_field_empty));
    }

    @Override
    public void showTooShortEmailError() {
        mHolder.emailView.setError(getString(R.string.error_short_email));
    }

    @Override
    public void showTooLongEmailError() {
        mHolder.emailView.setError(getString(R.string.error_long_email));
    }

    @Override
    public void showInvalidEmailError() {
        mHolder.emailView.setError(getString(R.string.error_email_invalid));
    }

    @Override
    public void showEmailExistError() {
        mHolder.emailView.setError(getString(R.string.error_email_exist));
    }

    @Override
    public void showActivity(Class clazz) {
        Intent intent = new Intent(getApplicationContext(), clazz);
        startActivity(intent);
        finish();
    }

    @Override
    public void showSaveUserError() {
        Snackbar.make(mHolder.emailView, getString(R.string.save_user_error), Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void setPresenter(RegisterContract.Presenter presenter) {
        mPresenter = checkNotNull(presenter);
    }

    class ViewHolder{

         EditText emailView;
         EditText passwordView;
         EditText passwordCopyView;
    }

}
