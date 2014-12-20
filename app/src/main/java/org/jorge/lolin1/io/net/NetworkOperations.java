package org.jorge.lolin1.io.net;

import android.support.annotation.NonNull;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.jorge.lolin1.util.Utils;

import java.io.IOException;
import java.net.URL;

public abstract class NetworkOperations {

    private static final OkHttpClient client = new OkHttpClient();

    public static Response doJSONRequest(@NonNull URL url) throws IOException {
        if (Utils.isMainThread()) {
            throw new IllegalStateException("Attempted call to doJSONRequest on main thread!");
        }

        Request request = new Request.Builder().url(url).build();

        return client.newCall(request).execute();
    }
}
