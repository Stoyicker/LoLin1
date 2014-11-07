package org.jorge.lolin1.ui.activity;

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

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.crashlytics.android.Crashlytics;

import org.jorge.lolin1.LoLin1Application;
import org.jorge.lolin1.utils.FileManager;

import java.io.File;

import io.fabric.sdk.android.Fabric;

public class InitialActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initContext();
        Fabric.with(LoLin1Application.getInstance().getContext(), new Crashlytics());
        flushCacheIfNecessary();
        launchHomeActivity();
    }

    private void launchHomeActivity() {
        final Intent splashIntent = new Intent(getApplicationContext(), HomeActivity.class);
        finish();
        startActivity(splashIntent);
    }

    private void initContext() {
        LoLin1Application.getInstance().setContext(getSupportActionBar().getThemedContext());
    }

    private void flushCacheIfNecessary() {
        File cacheDir;
        int CACHE_SIZE_LIMIT_BYTES = 1048576;
        if ((cacheDir = LoLin1Application.getInstance().getContext().getCacheDir()).length() > CACHE_SIZE_LIMIT_BYTES) {
            FileManager.recursivelyDelete(cacheDir);
        }
    }
}
