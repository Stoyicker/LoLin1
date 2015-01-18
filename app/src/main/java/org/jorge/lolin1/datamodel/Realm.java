package org.jorge.lolin1.datamodel;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import org.jorge.lolin1.R;

import java.util.EnumMap;

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

public class Realm implements Parcelable {

    private final String mKey;
    private final String[] mLocales;

    public static Realm[] getAllRealms() {
        Realm[] ret = new Realm[singletonMap.size()];
        int i = 0;
        for (Object x : singletonMap.values()) {
            ret[i] = (Realm) x;
            i++;
        }
        return ret;
    }

    public Realm(Parcel in) {
        Realm copied = Realm.getInstanceByRealmId(RealmEnum.valueOf(in.readString()));
        mKey = copied.mKey;
        mLocales = copied.mLocales;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mKey);
    }

    public enum RealmEnum {
        NA, EUW, EUNE, BR, LAN, LAS, TR, RU, OCE, NONE
    }

    private static final EnumMap<RealmEnum, Realm> singletonMap = new EnumMap<>(RealmEnum.class);

    public static void initRealms(Context context) {
        RealmEnum[] enumValues = RealmEnum.values();

        for (RealmEnum x : enumValues)
            singletonMap.put(x, new Realm(context, x));

    }

    public static Realm getInstanceByRealmId(RealmEnum realmId) {
        Realm ret;

        if (!singletonMap.containsKey(realmId) || (ret = singletonMap.get(realmId)) == null) {
            return null;
        }

        return ret;
    }

    @Override
    public String toString() {
        return mKey;
    }

    private Realm(Context context, RealmEnum realmId) {
        mKey = realmId.name();

        switch (realmId) {
            case NA:
                mLocales = context.getResources().getStringArray(R.array
                        .realm_na_locales);
                break;
            case EUW:
                mLocales = context.getResources().getStringArray(R.array
                        .realm_euw_locales);
                break;
            case EUNE:
                mLocales = context.getResources().getStringArray(R.array
                        .realm_eune_locales);
                break;
            case BR:
                mLocales = context.getResources().getStringArray(R.array
                        .realm_br_locales);
                break;
            case LAN:
                mLocales = context.getResources().getStringArray(R.array
                        .realm_lan_locales);
                break;
            case LAS:
                mLocales = context.getResources().getStringArray(R.array
                        .realm_las_locales);
                break;
            case TR:
                mLocales = context.getResources().getStringArray(R.array
                        .realm_tr_locales);
                break;
            case RU:
                mLocales = context.getResources().getStringArray(R.array
                        .realm_ru_locales);
                break;
            case OCE:
                mLocales = context.getResources().getStringArray(R.array
                        .realm_oce_locales);
                break;
            case NONE:
                //Unused
                mLocales = new String[]{};
                break;
            default:
                throw new IllegalArgumentException(realmId + " not recognized.");
        }
    }

    public String[] getLocales() {
        return mLocales;
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Realm createFromParcel(Parcel in) {
            return new Realm(in);
        }

        public Realm[] newArray(int size) {
            return new Realm[size];
        }
    };
}
