package org.jorge.lolin1.datamodel;

import android.content.Context;

import org.jorge.lolin1.R;

import java.util.EnumMap;

public class Realm {

    public static Realm[] getAllRealms() {
        return (Realm[]) singletonMap.values().toArray();
    }

    public enum RealmEnum {
        NA, EUW, EUNE, BR, LAN, LAS, TR, RU, OCE
    }

    private final String mKey;
    private final String[] mLocales;
    private static final EnumMap<RealmEnum, Realm> singletonMap = new EnumMap<>(RealmEnum.class);

    public static void initRealms(Context context) {
        RealmEnum[] enumValues = RealmEnum.values();

        for (RealmEnum x : enumValues)
            singletonMap.put(x, new Realm(context, x));

    }

    public static Realm getInstanceByRealmId(RealmEnum realmId) {
        Realm ret;

        if (!singletonMap.containsKey(realmId) || (ret = singletonMap.get(realmId)) == null) {
            throw new IllegalArgumentException(realmId + " not recognized.");
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
                        .realm_na_languages);
                break;
            case EUW:
                mLocales = context.getResources().getStringArray(R.array
                        .realm_euw_languages);
                break;
            case EUNE:
                mLocales = context.getResources().getStringArray(R.array
                        .realm_eune_languages);
                break;
            case BR:
                mLocales = context.getResources().getStringArray(R.array
                        .realm_br_languages);
                break;
            case LAN:
                mLocales = context.getResources().getStringArray(R.array
                        .realm_lan_languages);
                break;
            case LAS:
                mLocales = context.getResources().getStringArray(R.array
                        .realm_las_languages);
                break;
            case TR:
                mLocales = context.getResources().getStringArray(R.array
                        .realm_tr_languages);
                break;
            case RU:
                mLocales = context.getResources().getStringArray(R.array
                        .realm_ru_languages);
                break;
            case OCE:
                mLocales = context.getResources().getStringArray(R.array
                        .realm_oce_languages);
                break;
            default:
                throw new IllegalArgumentException(realmId + " not recognized.");
        }
    }

    public String[] getLocales() {
        return mLocales;
    }
}
