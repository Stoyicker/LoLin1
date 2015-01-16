package org.jorge.lolin1.account;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.support.annotation.Nullable;

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

public class AccountManagerSingleton {

    private static final Object LOCK = new Object();
    private static volatile AccountManagerSingleton mInstance;
    static String ACCOUNT_TYPE = "org.jorge.lolin1.2";
    private LoLin1Account mNullAccount = new LoLin1Account("null", "null", "NONE");

    public static AccountManagerSingleton getInstance() {
        AccountManagerSingleton ret = mInstance;
        if (ret == null) {
            synchronized (LOCK) {
                ret = mInstance;
                if (ret == null) {
                    ret = new AccountManagerSingleton();
                    mInstance = ret;
                }
            }
        }
        return ret;
    }

    @Nullable
    public Account loadFirstAccount(Context context) {
        final AccountManager accountManager = AccountManager.get(context);
        Account[] accounts = accountManager.getAccountsByType(ACCOUNT_TYPE);
        if (accounts != null && accounts.length > 0) {
            return accounts[0];
        }

        return null;
    }

    public LoLin1Account getNullAccount() {
        return mNullAccount;
    }
}
