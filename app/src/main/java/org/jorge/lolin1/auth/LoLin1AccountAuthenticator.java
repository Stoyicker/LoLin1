package org.jorge.lolin1.auth;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import org.jorge.lolin1.R;
import org.jorge.lolin1.datamodel.LoLin1Account;
import org.jorge.lolin1.ui.activity.LoginActivity;

public class LoLin1AccountAuthenticator extends AbstractAccountAuthenticator {

    public static final String ACCOUNT_DATA_REALM = "ACCOUNT_DATA_REALM";
    private final Context mContext;
    public static final String TOKEN_GENERATION_JOINT = "AtnY8Y9vJAgE0t6cpG60"; //Generated
    // with Random.org

    public LoLin1AccountAuthenticator(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    public Bundle editProperties(AccountAuthenticatorResponse response, String accountType) {
        return null;
    }

    @Override
    public Bundle addAccount(AccountAuthenticatorResponse response, String accountType,
                             String authTokenType, String[] requiredFeatures, Bundle options)
            throws NetworkErrorException {
        final Intent intent = new Intent(mContext, LoginActivity.class);
        intent.putExtra(LoginActivity.KEY_ACCOUNT_TYPE, accountType);
        intent.putExtra(LoginActivity.KEY_NEW_ACCOUNT, Boolean.TRUE);
        intent.putExtra(LoginActivity.KEY_RESPONSE, response);
        final Bundle bundle = new Bundle();
        bundle.putParcelable(AccountManager.KEY_INTENT, intent);
        return bundle;
    }

    @Override
    public Bundle confirmCredentials(AccountAuthenticatorResponse response, Account account,
                                     Bundle options) throws NetworkErrorException {
        return null;
    }

    @Override
    public Bundle getAuthToken(AccountAuthenticatorResponse response, Account account,
                               String authTokenType, Bundle options) throws NetworkErrorException {
        AccountManager accountManager = AccountManager.get(mContext);
        String username, password;
        final Bundle bundle = new Bundle();

        username = accountManager.peekAuthToken(account, authTokenType);
        if (!TextUtils.isEmpty(username)) {
            password = accountManager.getPassword(account);
            bundle.putString(AccountManager.KEY_ACCOUNT_NAME, username);
            bundle.putString(AccountManager.KEY_ACCOUNT_TYPE, authTokenType);
            bundle.putString(AccountManager.KEY_AUTH_TOKEN_LABEL,
                    username + TOKEN_GENERATION_JOINT + password);
        } else {
            final Intent intent = new Intent(mContext, LoginActivity.class);
            intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
            bundle.putParcelable(AccountManager.KEY_INTENT, intent);
        }

        return bundle;
    }

    @Override
    public String getAuthTokenLabel(String authTokenType) {
        return mContext.getString(R.string.auth_token_label);
    }

    @Override
    public Bundle updateCredentials(AccountAuthenticatorResponse response, Account account,
                                    String authTokenType, Bundle options)
            throws NetworkErrorException {
        return null;
    }

    @Override
    public Bundle hasFeatures(AccountAuthenticatorResponse response, Account account,
                              String[] features) throws NetworkErrorException {
        return null;
    }

    @Nullable
    public static LoLin1Account loadAccount(Context context) {
        final AccountManager accountManager = AccountManager.get(context);
        Account[] accounts = accountManager.getAccountsByType(context.getString(R.string
                .account_type));
        if (accounts != null && accounts.length > 0) {
            return new LoLin1Account(context, accounts[0]);
        }

        return null;
    }
}
