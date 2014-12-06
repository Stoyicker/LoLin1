package org.jorge.lolin1.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.MenuItem;

public class SettingsPreferenceFragment extends SupportPreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(Boolean.TRUE);

//        addPreferencesFromResource(R.xml.settings);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return getActivity().onOptionsItemSelected(item);
    }
}
