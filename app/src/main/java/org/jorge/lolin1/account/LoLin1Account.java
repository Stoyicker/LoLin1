package org.jorge.lolin1.account;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.crashlytics.android.Crashlytics;

import org.jorge.lolin1.datamodel.Realm;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

/*
 * This file is part of LoLin1.
 *
 * LoLin1 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * LoLin1 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with LoLin1. If not, see <http://www.gnu.org/licenses/>.
 *
 * Created by Jorge Antonio Diaz-Benito Soriano.
 */

public class LoLin1Account {

    private final String mUsername, mPassword;
    private final Realm.RealmEnum mRealmEnum;

    private final static String TOKEN_TYPE_USERNAME = "TOKEN_TYPE_USERNAME",
            TOKEN_TYPE_PASSWORD = "TOKEN_TYPE_PASSWORD", TOKEN_TYPE_REALM = "TOKEN_TYPE_REALM";

    public LoLin1Account(@NonNull String userName, @NonNull String password,
                         @NonNull String realm) {
        this.mUsername = userName;
        this.mPassword = password;
        this.mRealmEnum = Realm.RealmEnum.valueOf(realm.toUpperCase(Locale.ENGLISH));
    }

    public LoLin1Account(@NonNull Context context, @NonNull Account account) {
        final AsyncTask<Object, Void, String[]> credentialsTask = new AsyncTask<Object, Void,
                String[]>() {

            @Override
            protected String[] doInBackground(Object... params) {
                final String[] ret = new String[2];
                final Context innerContext = (Context) params[0];
                final Account innerAccount = (Account) params[1];
                try {
                    ret[0] =
                            AccountManager.get(innerContext)
                                    .blockingGetAuthToken(innerAccount, TOKEN_TYPE_PASSWORD,
                                            Boolean.TRUE);
                    ret[1] =
                            AccountManager.get(innerContext)
                                    .blockingGetAuthToken(innerAccount, TOKEN_TYPE_REALM,
                                            Boolean.TRUE);
                    return ret;
                } catch (OperationCanceledException | IOException | AuthenticatorException e) {
                    Crashlytics.logException(e);
                }

                return null;
            }
        };
        credentialsTask.executeOnExecutor(Executors.newSingleThreadExecutor(), context, account);
        try {
            final String[] authData = credentialsTask.get();
            final List<String> asString = Arrays.asList(authData);
            if (asString.contains(null))
                throw new IllegalStateException("Retrieved account contains null data at index "
                        + asString.indexOf(null) + " (at least)");
            mUsername = account.name;
            mPassword = authData[0];
            mRealmEnum = Realm.RealmEnum.valueOf(authData[1].toUpperCase(Locale.ENGLISH));
        } catch (InterruptedException | ExecutionException e) {
            Crashlytics.logException(e);
            throw new IllegalStateException("Unexpected exception when constructing a " +
                    "LoLin1Account from a regular Account");
        }
    }

    public String getPassword() {
        return mPassword;
    }

    public String getUsername() {
        return mUsername;
    }

    public Realm.RealmEnum getRealmEnum() {
        return mRealmEnum;
    }

    private void saveAccount(@NonNull Context context) {
        final AccountManager accountManager = AccountManager.get(context);
        final Account account =
                new Account(mUsername, AccountManagerSingleton.ACCOUNT_TYPE);
        accountManager.setAuthToken(account, TOKEN_TYPE_PASSWORD, mPassword);
        accountManager.setAuthToken(account, TOKEN_TYPE_REALM, mRealmEnum.name());
    }
}
