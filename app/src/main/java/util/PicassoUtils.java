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

package util;

import android.content.Context;
import android.net.Uri;
import android.widget.ImageView;

import com.squareup.picasso.Downloader;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.UrlConnectionDownloader;

import java.io.IOException;

public abstract class PicassoUtils {

    public static void loadInto(Context context, String path, int errorResId, ImageView target) {
        if (ConnectionUtils.isInternetWifi(context))
            Picasso.with(context)
                    .load(path)
                    .error(errorResId)
                    .into(target);
        else
            new Picasso.Builder(context).downloader(new UrlConnectionDownloader(context) {
                @Override
                public Downloader.Response load(Uri uri, boolean localCacheOnly) throws IOException {
                    return super.load(uri, Boolean.TRUE);
                }
            }).build().load(path)
                    .error(errorResId)
                    .into(target);
    }
}
