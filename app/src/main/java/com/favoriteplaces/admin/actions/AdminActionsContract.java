package com.favoriteplaces.admin.actions;

import com.favoriteplaces.BasePresenter;
import com.favoriteplaces.BaseView;

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
