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

package org.jorge.lolin1.datamodel;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import org.jorge.lolin1.R;

public class FeedArticle implements Parcelable {
    private final String title, url, imageUrl, previewText;
    private boolean read = Boolean.FALSE;

    public FeedArticle(@NonNull String _title, @NonNull String _url, @NonNull String _imageUrl,
                       @NonNull String _previewText) {
        this.title = _title;
        this.url = _url;
        this.imageUrl = _imageUrl;
        this.previewText = _previewText;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public Boolean isRead() {
        return read;
    }

    public void markAsRead() {
        if (read)
            throw new IllegalStateException("Article was already marked as read.");
        read = Boolean.TRUE;
    }

    public String getPreviewText() {
        return previewText;
    }

    public FeedArticle(Parcel in) {
        this.title = in.readString();
        this.url = in.readString();
        this.imageUrl = in.readString();
        this.previewText = in.readString();
        boolean[] aux = new boolean[1];
        in.readBooleanArray(aux);
        read = aux[0];
    }

    public void requestShareAction(Context context) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, getTitle());
        intent.putExtra(Intent.EXTRA_TITLE, getPreviewText());
        intent.putExtra(Intent.EXTRA_TEXT, getUrl());
        context.startActivity(Intent.createChooser(intent, context.getString(R.string
                .abc_shareactionprovider_share_with)));
    }

    public void requestBrowseToAction(Context context) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getUrl()));
        context.startActivity(browserIntent);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(getTitle());
        dest.writeString(getUrl());
        dest.writeString(getImageUrl());
        dest.writeString(getPreviewText());
        dest.writeBooleanArray(new boolean[]{isRead()});
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public FeedArticle createFromParcel(Parcel in) {
            return new FeedArticle(in);
        }

        public FeedArticle[] newArray(int size) {
            return new FeedArticle[size];
        }
    };
}
