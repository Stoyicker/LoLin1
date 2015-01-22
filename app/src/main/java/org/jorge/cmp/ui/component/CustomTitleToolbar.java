package org.jorge.cmp.ui.component;

import android.content.Context;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.widget.TextView;

import org.jorge.cmp.R;

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

public class CustomTitleToolbar extends Toolbar {

    public CustomTitleToolbar(Context context) {
        super(context);
    }

    public CustomTitleToolbar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomTitleToolbar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setTitle(int resId) {
        ((TextView) findViewById(R.id.toolbar_title)).setText(getContext().getResources()
                .getString(resId));
    }

    @Override
    public void setTitle(CharSequence title) {
        ((TextView) findViewById(R.id.toolbar_title)).setText(title);
    }
}
