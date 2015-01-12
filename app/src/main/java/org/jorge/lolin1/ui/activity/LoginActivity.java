package org.jorge.lolin1.ui.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;

import org.jorge.lolin1.LoLin1Application;
import org.jorge.lolin1.R;

public class LoginActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final ActionBar supportActionBar;
        if ((supportActionBar = getSupportActionBar()) != null)
            supportActionBar.hide();

        setContentView(R.layout.activity_login);
        final Context context = LoLin1Application.getInstance().getContext();
    }
}
