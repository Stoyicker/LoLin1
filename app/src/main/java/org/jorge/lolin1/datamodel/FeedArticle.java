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

import org.jorge.lolin1.R;

public class FeedArticle {
    private final String title = "New free champion rotation: Akali, Quinn, Thresh and more!",
            url = "http://euw.leagueoflegends.com/es/news/champions-skins/champion-update/blog-des-actualizacion-de-campeones",
            imageUrl = "http://euw.leagueoflegends.com/sites/default/files/styles/wide_small/public/upload/viktor_0_base1_1920.jpg?itok=vdCKicyL";
    private boolean read = Boolean.FALSE;
    private final String previewText = "A pair of ne’erdowells are putting the squeeze on League of Legends. In the dead of night, they broke into the store and stole Hippalus’ most prized possessions (he really loves his socks). The two best detectives in the history of detecting are on the case but, to prevent further chaos, we’re giving in to their demands. These are dark days in League… kind of." +
            "A pair of ne’erdowells are putting the squeeze on League of Legends. In the dead of night, they broke into the store and stole Hippalus’ most prized possessions (he really loves his socks). The two best detectives in the history of detecting are on the case but, to prevent further chaos, we’re giving in to their demands. These are dark days in League… kind of." +
            "A pair of ne’erdowells are putting the squeeze on League of Legends. In the dead of night, they broke into the store and stole Hippalus’ most prized possessions (he really loves his socks). The two best detectives in the history of detecting are on the case but, to prevent further chaos, we’re giving in to their demands. These are dark days in League… kind of." +
            "A pair of ne’erdowells are putting the squeeze on League of Legends. In the dead of night, they broke into the store and stole Hippalus’ most prized possessions (he really loves his socks). The two best detectives in the history of detecting are on the case but, to prevent further chaos, we’re giving in to their demands. These are dark days in League… kind of." +
            "A pair of ne’erdowells are putting the squeeze on League of Legends. In the dead of night, they broke into the store and stole Hippalus’ most prized possessions (he really loves his socks). The two best detectives in the history of detecting are on the case but, to prevent further chaos, we’re giving in to their demands. These are dark days in League… kind of." +
            "A pair of ne’erdowells are putting the squeeze on League of Legends. In the dead of night, they broke into the store and stole Hippalus’ most prized possessions (he really loves his socks). The two best detectives in the history of detecting are on the case but, to prevent further chaos, we’re giving in to their demands. These are dark days in League… kind of." +
            "A pair of ne’erdowells are putting the squeeze on League of Legends. In the dead of night, they broke into the store and stole Hippalus’ most prized possessions (he really loves his socks). The two best detectives in the history of detecting are on the case but, to prevent further chaos, we’re giving in to their demands. These are dark days in League… kind of.";

    //TODO Uncomment this
//    public NewsArticle(@NonNull String _title, @NonNull String _url, @NonNull String _imageUrl, @NonNull String _previewText, @NonNull Date _date) {
//        this.title = _title;
//        this.url = _url;
//        this.imageUrl = _imageUrl;
//        this.previewText = _previewText;
//    }

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

    public void requestShareAction(Context context) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, getTitle());
        intent.putExtra(Intent.EXTRA_TITLE, getPreviewText());
        intent.putExtra(Intent.EXTRA_TEXT, getUrl());
        context.startActivity(Intent.createChooser(intent, context.getResources().getString(R.string.abc_shareactionprovider_share_with)));
    }

    public void requestBrowseToAction(Context context) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getUrl()));
        context.startActivity(browserIntent);
    }
}
