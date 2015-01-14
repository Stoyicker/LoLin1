package org.jorge.lolin1.account;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.support.annotation.Nullable;

public class AccountManagerSingleton {

    private static final Object LOCK = new Object();
    private static volatile AccountManagerSingleton mInstance;
    static String ACCOUNT_TYPE = "org.jorge.lolin1.2";

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
}
