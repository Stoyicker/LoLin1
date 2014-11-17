package org.jorge.lolin1.ui.util;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

import com.nirhart.parallaxscroll.views.ParallaxScrollView;

/**
 * Derived from NotifyingScrollView from Cyril Mottier
 */
public class ParallaxNotifyingScrollView extends ParallaxScrollView {

    /**
     * @author Cyril Mottier
     */
    public interface OnScrollChangedListener {
        void onScrollChanged(ScrollView who, int l, int t, int oldl, int oldt);
    }

    private OnScrollChangedListener mOnScrollChangedListener;

    public ParallaxNotifyingScrollView(Context context) {
        super(context);
    }

    public ParallaxNotifyingScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ParallaxNotifyingScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (mOnScrollChangedListener != null) {
            mOnScrollChangedListener.onScrollChanged(this, l, t, oldl, oldt);
        }
    }

    public void setOnScrollChangedListener(OnScrollChangedListener listener) {
        mOnScrollChangedListener = listener;
    }

}
