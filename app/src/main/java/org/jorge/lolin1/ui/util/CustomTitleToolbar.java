package org.jorge.lolin1.ui.util;

import android.content.Context;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.widget.TextView;

import org.jorge.lolin1.R;

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
        ((TextView) findViewById(R.id.toolbar_title)).setText(super.getContext().getResources().getString(resId));
    }

    @Override
    public void setTitle(CharSequence title) {
        ((TextView) findViewById(R.id.toolbar_title)).setText(title);
    }
}
