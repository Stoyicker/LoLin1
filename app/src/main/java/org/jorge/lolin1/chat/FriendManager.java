package org.jorge.lolin1.chat;

import com.github.theholywaffle.lolchatapi.wrapper.Friend;

import org.jorge.lolin1.service.ChatIntentService;
import org.jorge.lolin1.util.Utils;

import java.util.Collection;
import java.util.Collections;
import java.util.TreeSet;


/**
 * This file is part of LoLin1.
 * <p/>
 * LoLin1 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p/>
 * LoLin1 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU General Public License
 * along with LoLin1. If not, see <http://www.gnu.org/licenses/>.
 * <p/>
 * Created by Jorge Antonio Diaz-Benito Soriano.
 */
public class FriendManager {

    private static volatile FriendManager mInstance;
    private static final Object LOCK = new Object();
    private final Collection<Friend> ONLINE_FRIENDS = Collections.synchronizedSortedSet(new
            TreeSet<Friend>());

    public static FriendManager getInstance() {
        FriendManager ret = mInstance;
        if (ret == null)
            synchronized (LOCK) {
                ret = mInstance;
                if (ret == null) {
                    ret = new FriendManager();
                    mInstance = ret;
                }
            }
        return ret;
    }

    private FriendManager() {
    }

    public Friend findFriendByName(String friendName) {
        for (Friend f : ONLINE_FRIENDS)
            if (f.getName().contentEquals(friendName)) {
                return f;
            }
        return null;
    }

    public synchronized void updateOnlineFriends() {
        Collection<Friend> onlineFriends = ChatIntentService.getOnlineFriends();
        ONLINE_FRIENDS.clear();
        if (!ChatIntentService.isLoggedIn() || !Utils.isInternetReachable())
            return;
        for (Friend f : onlineFriends) {
            if (f.getChatMode() != null && f.isOnline() && !f.isNull()) { //Prevention check
                ONLINE_FRIENDS.add(f);
            }
        }
    }

    public Collection<Friend> getOnlineFriends() {
        return ONLINE_FRIENDS;
    }
}
