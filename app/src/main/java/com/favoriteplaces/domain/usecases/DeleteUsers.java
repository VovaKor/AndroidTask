/*
 * Copyright 2016, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.favoriteplaces.domain.usecases;

import android.support.annotation.NonNull;

import com.favoriteplaces.UseCase;
import com.favoriteplaces.repository.UsersRepository;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Deletes tasks marked as completed.
 */
public class DeleteUsers
        extends UseCase<DeleteUsers.RequestValues, DeleteUsers.ResponseValue> {

    private final UsersRepository mUsersRepository;

    public DeleteUsers(@NonNull UsersRepository usersRepository) {
        mUsersRepository = checkNotNull(usersRepository, "usersRepository cannot be null!");
    }

    @Override
    protected void executeUseCase(final RequestValues values) {
        mUsersRepository.deleteCheckedUsers();
        getUseCaseCallback().onSuccess(new ResponseValue());
    }

    public static class RequestValues implements UseCase.RequestValues { }

    public static class ResponseValue implements UseCase.ResponseValue { }
}
