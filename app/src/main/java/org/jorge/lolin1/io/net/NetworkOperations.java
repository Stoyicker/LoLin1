package org.jorge.lolin1.io.net;

import android.support.annotation.NonNull;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.jorge.lolin1.util.Utils;

import java.io.IOException;
import java.net.URL;

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
