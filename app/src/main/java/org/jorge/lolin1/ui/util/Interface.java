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

package org.jorge.lolin1.ui.util;

import org.jorge.lolin1.datamodel.FeedArticle;

public abstract class Interface {
    public interface IOnFeedArticleClickedListener {
        public void onFeedArticleClicked(FeedArticle item, Class c);
    }

    public interface IOnBackPressed {

        public Boolean onBackPressed();
    }

    public interface IOnItemInteractionListener {

        public void setSelectedIndex(int selectedIndex);

        public void clearSelection();

        void onItemClick(FeedArticle item);
    }
}