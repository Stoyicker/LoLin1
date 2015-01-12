package org.jorge.lolin1.ui.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.widget.TextView;

import org.jorge.lolin1.LoLin1Application;
import org.jorge.lolin1.R;

import java.util.Locale;

public class LoginActivity extends ActionBarActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final ActionBar supportActionBar;
        if ((supportActionBar = getSupportActionBar()) != null)
            supportActionBar.hide();

        setContentView(R.layout.activity_login);

        ((TextView) findViewById(R.id.disclaimer_view)).setText(String.format(Locale.ENGLISH,
                getString(R.string.legal_jibber_jabber), getString(R.string.app_name)));

        final Context context = LoLin1Application.getInstance().getContext();
    }
}
