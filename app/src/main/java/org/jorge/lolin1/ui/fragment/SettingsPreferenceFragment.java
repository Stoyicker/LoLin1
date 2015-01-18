package org.jorge.lolin1.ui.fragment;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceFragment;
import android.view.MenuItem;

import org.jorge.lolin1.LoLin1Application;
import org.jorge.lolin1.R;
import org.jorge.lolin1.auth.LoLin1AccountAuthenticator;
import org.jorge.lolin1.datamodel.LoLin1Account;
import org.jorge.lolin1.datamodel.Realm;
import org.jorge.lolin1.io.prefs.PreferenceAssistant;

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

public class SettingsPreferenceFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(Boolean.TRUE);

        addPreferencesFromResource(R.xml.settings);

        final ListPreference langPreference = (ListPreference) findPreference(PreferenceAssistant
                .PREF_LANG);

        final Context context = LoLin1Application.getInstance().getContext();
        final Resources resources = context.getResources();

        final Integer entriesArray, valuesArray;
        final LoLin1Account account = LoLin1AccountAuthenticator.loadAccount(context);

        final Realm.RealmEnum realmEnum = account == null ? Realm.RealmEnum.NONE : account
                .getRealmEnum();

        switch (realmEnum) {
            case NONE:
                valuesArray = R.array.realm_na_locales;
                entriesArray = R.array.realm_na_languages;
                break;
            case BR:
                valuesArray = R.array.realm_br_locales;
                entriesArray = R.array.realm_br_languages;
                break;
            case EUNE:
                valuesArray = R.array.realm_eune_locales;
                entriesArray = R.array.realm_eune_languages;
                break;
            case EUW:
                valuesArray = R.array.realm_euw_locales;
                entriesArray = R.array.realm_euw_languages;
                break;
            case LAN:
                valuesArray = R.array.realm_lan_locales;
                entriesArray = R.array.realm_lan_languages;
                break;
            case LAS:
                valuesArray = R.array.realm_las_locales;
                entriesArray = R.array.realm_las_languages;
                break;
            case NA:
                valuesArray = R.array.realm_na_locales;
                entriesArray = R.array.realm_na_languages;
                break;
            case OCE:
                valuesArray = R.array.realm_oce_locales;
                entriesArray = R.array.realm_oce_languages;
                break;
            case RU:
                valuesArray = R.array.realm_ru_locales;
                entriesArray = R.array.realm_ru_languages;
                break;
            case TR:
                valuesArray = R.array.realm_tr_locales;
                entriesArray = R.array.realm_tr_languages;
                break;
            default:
                throw new IllegalArgumentException("Illegal realm parsed during pref_lang setup: " +
                        "" + realmEnum);
        }

        langPreference.setEntries(resources.getStringArray(entriesArray));
        langPreference.setEntryValues(resources.getStringArray(valuesArray));
        langPreference.setDefaultValue(resources.getStringArray(valuesArray)[0]);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return getActivity().onOptionsItemSelected(item);
    }
}
