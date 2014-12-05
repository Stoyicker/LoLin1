package org.jorge.lolin1.ui.activity;

import android.preference.PreferenceActivity;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;

import org.jorge.lolin1.R;

public class SettingsPreferenceActivity extends PreferenceActivity {

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                overridePendingTransition(R.anim.move_in_from_bottom, R.anim.move_out_to_bottom);
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
