package org.jorge.lolin1.ui.activity;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.jorge.lolin1.LoLin1Application;
import org.jorge.lolin1.R;

import java.util.Locale;

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

public class LoginActivity extends AccountAuthenticatorActivity {

    public static final String KEY_ACCOUNT_TYPE = "KEY_ACCOUNT_TYPE";
    public static final String KEY_NEW_ACCOUNT = "KEY_NEW_ACCOUNT";
    public static final String KEY_RESPONSE = "KEY_RESPONSE";
    private EditText mUserNameField, mPasswordField;
    private Spinner mRealmSpinner;
    private Context mContext;
    private View mLoginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final android.app.ActionBar actionBar;
        if ((actionBar = getActionBar()) != null)
            actionBar.hide();

        setContentView(R.layout.activity_login);

        ((TextView) findViewById(R.id.disclaimer_view)).setText(String.format(Locale.ENGLISH,
                getString(R.string.legal_jibber_jabber), getString(R.string.app_name)));

        mContext = LoLin1Application.getInstance().getContext();

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.realm_list, R.layout.spinner_item_realm_selected);
        adapter.setDropDownViewResource(R.layout.spinner_item_realm_dropdown);
        ((Spinner) findViewById(R.id.realm_spinner)).setAdapter(adapter);

        mUserNameField = (EditText) findViewById(R.id.user_name_field);
        mPasswordField = (EditText) findViewById(R.id.password_field);
        mRealmSpinner = (Spinner) findViewById(R.id.realm_spinner);

        mUserNameField.requestFocus();

        findViewById(R.id.user_name_field_wrapper).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager inputMethodManager = (InputMethodManager) mContext
                        .getSystemService(INPUT_METHOD_SERVICE);
                inputMethodManager.showSoftInput(mUserNameField, 0);
            }
        });

        findViewById(R.id.password_field_wrapper).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager inputMethodManager = (InputMethodManager) mContext
                        .getSystemService(INPUT_METHOD_SERVICE);
                inputMethodManager.showSoftInput(mPasswordField, 0);
            }
        });

        mLoginButton = findViewById(R.id.account_login_button);

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptAddCredentials();
            }
        });

        final TextView.OnEditorActionListener listener;

        mUserNameField.setOnEditorActionListener(listener = new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE || (event != null && event
                        .isShiftPressed() && (event
                        .getAction() == KeyEvent
                        .ACTION_DOWN &&
                        event.getKeyCode() == KeyEvent.KEYCODE_ENTER))) {
                    attemptAddCredentials();
                    return Boolean.TRUE;
                }
                return Boolean.FALSE;
            }
        });

        mPasswordField.setOnEditorActionListener(listener);
    }

    private synchronized void attemptAddCredentials() {
        final String userName, password;

        if (TextUtils.isEmpty(userName = mUserNameField.getText().toString()) || TextUtils.isEmpty
                (password = mPasswordField.getText().toString())) {
            Toast.makeText(mContext, getString(R.string.login_error_empty_username_or_password),
                    Toast.LENGTH_SHORT).show();
            return;
        }
        mLoginButton.setVisibility(View.GONE);

        final Intent parameters = new Intent();
        parameters.putExtra(AccountManager.KEY_ACCOUNT_NAME, userName);
        parameters.putExtra(AccountManager.KEY_PASSWORD, password);
        saveAccount(parameters);
    }

    private void saveAccount(Intent intent) {
        AccountManager accountManager = AccountManager.get(getApplicationContext());
        String accountName = intent.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
        String accountPassword = intent.getStringExtra(AccountManager.KEY_PASSWORD);
        final Account account =
                new Account(accountName, AccountManager.KEY_ACCOUNT_TYPE);
        if (intent.getBooleanExtra(KEY_NEW_ACCOUNT, Boolean.FALSE)) {
            accountManager.addAccountExplicitly(account, accountPassword, null);
        }
        setAccountAuthenticatorResult(intent.getExtras());
        setResult(RESULT_OK, intent);
        Toast.makeText(getApplicationContext(), R.string.account_save_success, Toast.LENGTH_SHORT)
                .show();
        finish();
    }
}
