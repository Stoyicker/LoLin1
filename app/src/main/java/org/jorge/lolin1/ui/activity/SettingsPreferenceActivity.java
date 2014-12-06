package org.jorge.lolin1.ui.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import org.jorge.lolin1.LoLin1Application;
import org.jorge.lolin1.R;
import org.jorge.lolin1.ui.fragment.SettingsPreferenceFragment;

public class SettingsPreferenceActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(toolbar);

        final Context context = LoLin1Application.getInstance().getContext();

        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(Boolean.TRUE);
        actionBar.setTitle(context.getResources().getString(R.string
                .nav_drawer_aux_entry_settings));

        getSupportFragmentManager().beginTransaction().replace(android.R.id.content,
                SettingsPreferenceFragment.instantiate(context,
                        SettingsPreferenceFragment.class.getName()))
                .commitAllowingStateLoss();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return Boolean.TRUE;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.move_in_from_bottom, R.anim.move_out_to_bottom);
    }
}
