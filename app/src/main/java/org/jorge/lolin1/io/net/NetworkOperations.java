package org.jorge.lolin1.io.net;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.net.URL;

public abstract class NetworkOperations {

    private static final OkHttpClient client = new OkHttpClient();

    public static Response doJSONRequest(URL url) throws IOException {
        Request request = new Request.Builder().url(url).build();

        return client.newCall(request).execute();
    }
}
