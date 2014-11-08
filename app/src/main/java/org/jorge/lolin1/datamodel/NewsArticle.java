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

import android.graphics.drawable.Drawable;

import org.jorge.lolin1.LoLin1Application;
import org.jorge.lolin1.R;

public class NewsArticle {
    private final String title = "Champion Spotlight: Karthus, the Deathsinger", url = "http://euw.leagueoflegends.com/es/news/champions-skins/champion-update/blog-des-actualizacion-de-campeones";

    //TODO Uncomment this
//    public NewsArticle(@NonNull String _title, @NonNull String _url) {
//        this.title = _title;
//        this.url = _url;
//    }

    public Drawable getImageAsDrawable() {
        //TODO getImageAsDrawable()
        return LoLin1Application.getInstance().getContext().getResources().getDrawable(R.drawable.news_article_image_stub);
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }
}
