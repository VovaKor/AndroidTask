package com.androidtask.domain.usecases;

import android.support.annotation.NonNull;

import com.androidtask.UseCase;
import com.androidtask.domain.models.User;
import com.androidtask.repository.UsersRepository;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Marks a task as active (not completed yet).
 */
public class UncheckUser extends UseCase<UncheckUser.RequestValues, UncheckUser.ResponseValue> {

    private final UsersRepository mUsersRepository;

    public UncheckUser(@NonNull UsersRepository usersRepository) {
        mUsersRepository = checkNotNull(usersRepository, "usersRepository cannot be null!");
    }

    @Override
    protected void executeUseCase(final RequestValues values) {
        User markedUser = values.getMarkedUser();
        mUsersRepository.uncheckUser(markedUser);
        getUseCaseCallback().onSuccess(new ResponseValue());
    }

    public static final class RequestValues implements UseCase.RequestValues {

        private final User mMarkedUser;

        public RequestValues(@NonNull User markedUser) {
            mMarkedUser = checkNotNull(markedUser, "markedUser cannot be null!");
        }

        public User getMarkedUser() {
            return mMarkedUser;
        }
    }

    public static final class ResponseValue implements UseCase.ResponseValue { }
}
