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

package org.jorge.lolin1.ui.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;

import org.jorge.lolin1.LoLin1Application;
import org.jorge.lolin1.R;
import org.jorge.lolin1.ui.fragment.NewsListFragment;

public class MainActivity extends ActionBarActivity {

    private Context mContext;
    private Fragment[] mContentFragments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = LoLin1Application.getInstance().getApplicationContext();
        showInitialFragment();
    }

    private void showInitialFragment() {
        if (mContentFragments == null)
            mContentFragments = new Fragment[1];
        getSupportFragmentManager().beginTransaction().add(R.id.content_fragment_container, findNewsListFragment()).commit();
    }

    private Fragment findNewsListFragment() {
        if (mContentFragments[0] == null)
            mContentFragments[0] = Fragment.instantiate(mContext, NewsListFragment.class.getName());
        return mContentFragments[0];
    }
}
