package org.jorge.lolin1.ui.component;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

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

public class ChainableSwipeRefreshLayout extends SwipeRefreshLayout {

    private RecyclerView mRecyclerView;

    public ChainableSwipeRefreshLayout(Context context) {
        super(context);
    }

    public ChainableSwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setRecyclerView(RecyclerView _recyclerView) {
        mRecyclerView = _recyclerView;
    }

    @Override
    public boolean canChildScrollUp() {
        if (mRecyclerView == null)
            return super.canChildScrollUp();
        else
            return mRecyclerView.canScrollVertically(-1);
    }
}
