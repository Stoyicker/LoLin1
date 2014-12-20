package org.jorge.lolin1.util;

import android.os.Looper;

public abstract class Utils {

    public static Boolean isMainThread() {
        return Looper.myLooper() == Looper.getMainLooper();
    }
}
