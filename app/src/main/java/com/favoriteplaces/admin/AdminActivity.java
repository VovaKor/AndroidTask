package com.favoriteplaces.admin;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.favoriteplaces.R;
import com.favoriteplaces.UseCaseHandler;
import com.favoriteplaces.domain.usecases.DeleteUsers;
import com.favoriteplaces.domain.usecases.GetUsers;
import com.favoriteplaces.domain.usecases.MarkUser;
import com.favoriteplaces.domain.usecases.UncheckUser;
import com.favoriteplaces.repository.UsersRepository;
import com.favoriteplaces.repository.local.UsersLocalDataSource;
import com.favoriteplaces.utils.ActivityUtils;

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
