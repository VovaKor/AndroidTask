package com.favoriteplaces.admin.actions;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;

import com.favoriteplaces.R;
import com.favoriteplaces.admin.actions.ban.AdminBanActivity;
import com.favoriteplaces.admin.actions.data.AdminUserDataActivity;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by vova on 25.06.17.
 */

public class AdminActionsActivity extends Activity implements AdminActionsContract.View{

    private AdminActionsContract.Presenter mPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_actions);

        String userId = getIntent().getStringExtra(getString(R.string.EXTRA_USER_ID));

        mPresenter = new AdminActionsPresenter(this,userId);

        Button banButton = (Button) findViewById(R.id.btnBan);
        banButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.openBanActivity();
            }
        });
        Button dataButton = (Button) findViewById(R.id.btnData);
        dataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.openDetailsActivity();
            }
        });
        Button cancelButton = (Button) findViewById(R.id.btnCancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.cancel();
            }
        });
    }

    @Override
    public void showBanUI(String id) {
        Intent intent = new Intent(getApplicationContext(), AdminBanActivity.class);
        intent.putExtra(getString(R.string.EXTRA_USER_ID), id);
        startActivity(intent);
    }

    @Override
    public void showDetailsUI(String id) {
        Intent intent = new Intent(getApplicationContext(), AdminUserDataActivity.class);
        intent.putExtra(getString(R.string.EXTRA_USER_ID), id);
        startActivity(intent);
    }

    @Override
    public void setPresenter(AdminActionsContract.Presenter presenter) {
        mPresenter = checkNotNull(presenter);
    }
}
