package org.jorge.lolin1.ui.component;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

/**
 * Derived from NotifyingScrollView from Cyril Mottier
 */
public class StickyParallaxNotifyingScrollView extends StickyHeaderParallaxScrollView {

    /**
     * @author Cyril Mottier
     */
    public interface OnScrollChangedListener {
        void onScrollChanged(ScrollView who, int l, int t, int oldl, int oldt);
    }

    private OnScrollChangedListener mOnScrollChangedListener;

    public StickyParallaxNotifyingScrollView(Context context) {
        super(context);
    }

    public StickyParallaxNotifyingScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public StickyParallaxNotifyingScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        if (mOnScrollChangedListener != null) {
            mOnScrollChangedListener.onScrollChanged(this, l, t, oldl, oldt);
        }
        super.onScrollChanged(l, t, oldl, oldt);
    }

    public void setOnScrollChangedListener(OnScrollChangedListener listener) {
        mOnScrollChangedListener = listener;
    }

}
