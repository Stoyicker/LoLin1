package org.jorge.lolin1.datamodel;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import org.jorge.lolin1.auth.LoLin1AccountAuthenticator;

import java.util.Locale;

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

public class LoLin1Account implements Parcelable {
    private final String mUsername, mPassword;
    private final Realm.RealmEnum mRealmEnum;

    public LoLin1Account(@NonNull String _userName, @NonNull String _password,
                         @NonNull String _realm) {
        mUsername = _userName;
        mPassword = _password;
        mRealmEnum = Realm.RealmEnum.valueOf(_realm.toUpperCase(Locale.ENGLISH));
    }

    public LoLin1Account(@NonNull Context context, @NonNull final Account account) {
        final AccountManager accountManager = AccountManager.get(context);
        mUsername = account.name;
        mPassword = accountManager.getPassword(account);
        mRealmEnum = Realm.RealmEnum.valueOf(AccountManager.get(context)
                .getUserData(account, LoLin1AccountAuthenticator.ACCOUNT_DATA_REALM));
    }

    public String getPassword() {
        return mPassword;
    }

    public String getUsername() {
        return mUsername;
    }

    public Realm.RealmEnum getRealmEnum() {
        return mRealmEnum;
    }

    public LoLin1Account(Parcel in) {
        mUsername = in.readString();
        mPassword = in.readString();
        mRealmEnum = Realm.RealmEnum.valueOf(in.readString().toUpperCase(Locale.ENGLISH));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mUsername);
        dest.writeString(mPassword);
        dest.writeString(mRealmEnum.name());
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public LoLin1Account createFromParcel(Parcel in) {
            return new LoLin1Account(in);
        }

        public LoLin1Account[] newArray(int size) {
            return new LoLin1Account[size];
        }
    };
}
