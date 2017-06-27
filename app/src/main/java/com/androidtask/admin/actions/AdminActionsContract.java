package com.androidtask.admin.actions;

import android.support.annotation.NonNull;

import com.androidtask.BasePresenter;
import com.androidtask.BaseView;
import com.androidtask.domain.models.User;

import java.util.List;

/**
 * This specifies the contract between the view and the presenter.
 */
public interface AdminActionsContract {

    interface View extends BaseView<Presenter> {

        void showBanUI(String id);

        void showDetailsUI(String id);
    }

    interface Presenter extends BasePresenter {

        void openBanActivity();

        void openDetailsActivity();

        void cancel();
    }
}
