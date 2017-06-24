package com.androidtask.admin;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.androidtask.R;
import com.androidtask.UseCaseHandler;
import com.androidtask.domain.usecases.DeleteUsers;
import com.androidtask.domain.usecases.GetUsers;
import com.androidtask.domain.usecases.MarkUser;
import com.androidtask.domain.usecases.UncheckUser;
import com.androidtask.repository.UsersRepository;
import com.androidtask.repository.local.UsersLocalDataSource;
import com.androidtask.utils.ActivityUtils;

public class AdminActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_act);

//

        AdminFragment adminFragment =
                (AdminFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);
        if (adminFragment == null) {
            // Create the fragment
            adminFragment = AdminFragment.newInstance();
            ActivityUtils.addFragmentToActivity(
                    getSupportFragmentManager(), adminFragment, R.id.contentFrame);
        }

        // Create the presenter

        new AdminPresenter(
                UseCaseHandler.getInstance(),
                adminFragment,
                new GetUsers(UsersRepository.getInstance(UsersLocalDataSource.getInstance(getApplicationContext()))),
                new MarkUser(UsersRepository.getInstance(UsersLocalDataSource.getInstance(getApplicationContext()))),
                new UncheckUser(UsersRepository.getInstance(UsersLocalDataSource.getInstance(getApplicationContext()))),
                new DeleteUsers(UsersRepository.getInstance(UsersLocalDataSource.getInstance(getApplicationContext()))));

    }

}
