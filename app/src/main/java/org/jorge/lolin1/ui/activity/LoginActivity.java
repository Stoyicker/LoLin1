package org.jorge.lolin1.ui.activity;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
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
import org.jorge.lolin1.auth.LoLin1AccountAuthenticator;
import org.jorge.lolin1.datamodel.LoLin1Account;
import org.jorge.lolin1.service.ChatIntentService;

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
    public static final String KEY_NEW_LOLIN1_ACCOUNT = "KEY_NEW_LOLIN1_ACCOUNT";
    public static final String KEY_RESPONSE = "KEY_RESPONSE";
    static final String LAUNCH_APP = "LAUNCH_APP";
    private EditText mUserNameField, mPasswordField;
    private Spinner mRealmSpinner;
    private Context mContext;

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

        final View mLoginButton = findViewById(R.id.account_login_button);

        stopChatServiceIfAlreadyRunning(mContext);

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addAccount();
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
                    addAccount();
                    return Boolean.TRUE;
                }
                return Boolean.FALSE;
            }
        });

        mPasswordField.setOnEditorActionListener(listener);
    }

    private void stopChatServiceIfAlreadyRunning(Context context) {
        Intent intent = new Intent(context, ChatIntentService.class);
        if (ChatIntentService.isLoggedIn()) {
            stopService(intent);
        }
    }

    private synchronized void addAccount() {
        final String userName, password;

        if (TextUtils.isEmpty(userName = mUserNameField.getText().toString()) || TextUtils.isEmpty
                (password = mPasswordField.getText().toString())) {
            Toast emptyCredentialsToast = Toast.makeText(mContext,
                    getString(R.string.login_error_empty_username_or_password),
                    Toast.LENGTH_SHORT);
            emptyCredentialsToast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0,
                    0);
            emptyCredentialsToast.show();
            return;
        }

        final Intent parameters = new Intent();
        parameters.putExtra(AccountManager.KEY_ACCOUNT_NAME, userName);
        parameters.putExtra(AccountManager.KEY_PASSWORD, password);
        parameters.putExtra(AccountManager.KEY_USERDATA, mRealmSpinner.getSelectedItem().toString
                ());
        parameters.putExtra(KEY_NEW_LOLIN1_ACCOUNT, Boolean.TRUE);
        saveAccount(parameters);
        finish();
        if (getIntent().getBooleanExtra(LoginActivity.LAUNCH_APP, Boolean.FALSE)) {
            final LoLin1Account acc = new LoLin1Account(parameters.getStringExtra(AccountManager
                    .KEY_ACCOUNT_NAME), parameters.getStringExtra(AccountManager.KEY_PASSWORD),
                    parameters.getStringExtra(AccountManager.KEY_USERDATA));
            final Intent nextActivityIntent;
            nextActivityIntent = new Intent(mContext, MainActivity.class);
            nextActivityIntent.putExtra(MainActivity.KEY_LOLIN1_ACCOUNT, acc);
            finish();
            startActivity(nextActivityIntent);
        }
    }

    private void saveAccount(Intent intent) {
        AccountManager accountManager = AccountManager.get(mContext);
        final String accountName = intent.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
        final String accountPassword = intent.getStringExtra(AccountManager.KEY_PASSWORD);
        final String accountRealm = intent.getStringExtra(AccountManager.KEY_USERDATA);
        final Account account =
                new Account(accountName, getResources().getString(R.string.account_type));
        if (intent.getBooleanExtra(KEY_NEW_LOLIN1_ACCOUNT, Boolean.FALSE)) {
            final Bundle userData = new Bundle();
            userData.putString(LoLin1AccountAuthenticator.ACCOUNT_DATA_REALM,
                    accountRealm.toUpperCase(Locale.ENGLISH));
            accountManager.addAccountExplicitly(account, accountPassword, userData);
        }
        setAccountAuthenticatorResult(intent.getExtras());
        setResult(RESULT_OK, intent);
        Toast.makeText(mContext, R.string.account_save_success, Toast.LENGTH_SHORT)
                .show();
    }
}
