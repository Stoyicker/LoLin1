package org.jorge.lolin1.ui.fragment;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.view.MenuItem;

import org.jorge.lolin1.R;

public class SettingsPreferenceFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(Boolean.TRUE);

        addPreferencesFromResource(R.xml.settings);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return getActivity().onOptionsItemSelected(item);
    }
}
