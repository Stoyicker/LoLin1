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

public class FeedArticle {
    private final String title = "New free champion rotation: Akali, Quinn, Thresh and more!",
            url = "http://euw.leagueoflegends.com/es/news/champions-skins/champion-update/blog-des-actualizacion-de-campeones",
            imageUrl = "http://euw.leagueoflegends.com/sites/default/files/styles/wide_small/public/upload/viktor_0_base1_1920.jpg?itok=vdCKicyL";
    private boolean read = Boolean.FALSE;
    private final String previewText = "<com.nirhart.parallaxscroll.views.ParallaxScrollView xmlns:android=\"http://schemas.android.com/apk/res/android\"\n" +
            "    xmlns:tools=\"http://schemas.android.com/tools\"\n" +
            "    xmlns:app=\"http://schemas.android.com/apk/res-auto\"\n" +
            "    android:layout_width=\"match_parent\"\n" +
            "    android:layout_height=\"match_parent\"\n" +
            "    app:parallax_factor=\"1.9\"\n" +
            "    tools:context=\".MainActivity\" >\n" +
            "\n" +
            "    <LinearLayout\n" +
            "        android:layout_width=\"match_parent\"\n" +
            "        android:layout_height=\"wrap_content\"\n" +
            "        android:orientation=\"vertical\" >\n" +
            "\n" +
            "        <TextView\n" +
            "            android:layout_width=\"match_parent\"\n" +
            "            android:layout_height=\"200dp\"\n" +
            "            android:background=\"@drawable/item_background\"\n" +
            "            android:gravity=\"center\"\n" +
            "            android:text=\"PARALLAXED\"\n" +
            "            android:textSize=\"50sp\"\n" +
            "            tools:ignore=\"HardcodedText\" />\n" +
            "\n" +
            "        <TextView\n" +
            "            android:layout_width=\"match_parent\"\n" +
            "            android:layout_height=\"wrap_content\"\n" +
            "            android:text=\"Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.    Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.\"\n" +
            "            android:textSize=\"26sp\"\n" +
            "            android:background=\"@android:color/white\"\n" +
            "            android:padding=\"5dp\"\n" +
            "            tools:ignore=\"HardcodedText\" />\n" +
            "    </LinearLayout>\n" +
            "\n" +
            "</com.nirhart.parallaxscroll.views.ParallaxScrollView><com.nirhart.parallaxscroll.views.ParallaxScrollView xmlns:android=\"http://schemas.android.com/apk/res/android\"\n" +
            "    xmlns:tools=\"http://schemas.android.com/tools\"\n" +
            "    xmlns:app=\"http://schemas.android.com/apk/res-auto\"\n" +
            "    android:layout_width=\"match_parent\"\n" +
            "    android:layout_height=\"match_parent\"\n" +
            "    app:parallax_factor=\"1.9\"\n" +
            "    tools:context=\".MainActivity\" >\n" +
            "\n" +
            "    <LinearLayout\n" +
            "        android:layout_width=\"match_parent\"\n" +
            "        android:layout_height=\"wrap_content\"\n" +
            "        android:orientation=\"vertical\" >\n" +
            "\n" +
            "        <TextView\n" +
            "            android:layout_width=\"match_parent\"\n" +
            "            android:layout_height=\"200dp\"\n" +
            "            android:background=\"@drawable/item_background\"\n" +
            "            android:gravity=\"center\"\n" +
            "            android:text=\"PARALLAXED\"\n" +
            "            android:textSize=\"50sp\"\n" +
            "            tools:ignore=\"HardcodedText\" />\n" +
            "\n" +
            "        <TextView\n" +
            "            android:layout_width=\"match_parent\"\n" +
            "            android:layout_height=\"wrap_content\"\n" +
            "            android:text=\"Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.    Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.\"\n" +
            "            android:textSize=\"26sp\"\n" +
            "            android:background=\"@android:color/white\"\n" +
            "            android:padding=\"5dp\"\n" +
            "            tools:ignore=\"HardcodedText\" />\n" +
            "    </LinearLayout>\n" +
            "\n" +
            "</com.nirhart.parallaxscroll.views.ParallaxScrollView>";

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
}
