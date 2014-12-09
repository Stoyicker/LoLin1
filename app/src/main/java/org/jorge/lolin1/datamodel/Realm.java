package org.jorge.lolin1.datamodel;

import android.content.Context;

import org.jorge.lolin1.R;
import org.jorge.lolin1.util.LocaleUtils;

import java.util.EnumMap;
import java.util.Locale;

public class Realm {

    public static Realm[] getAllRealms() {
        return (Realm[]) singletonMap.values().toArray();
    }

    public enum RealmEnum {
        NA, EUW, EUNE, BR, LAN, LAS, TR, RU, OCE, KR
    }

    private final String mKey;
    private final Locale[] mLocales;
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

        String[] localesAsStringArray = null;

        switch (realmId) {
            case NA:
                localesAsStringArray = context.getResources().getStringArray(R.array
                        .realm_na_languages);
                break;
            case EUW:
                break;
            case EUNE:
                break;
            case BR:
                break;
            case LAN:
                break;
            case LAS:
                break;
            case TR:
                break;
            case RU:
                break;
            case OCE:
                break;
            case KR:
                break;
            default:
                throw new IllegalArgumentException(realmId + " not recognized.");
        }

        assert localesAsStringArray != null;
        mLocales = new Locale[localesAsStringArray.length];
        for (int i = 0; i < localesAsStringArray.length; i++)
            mLocales[i] = LocaleUtils.getLocaleFromString(localesAsStringArray[i]);
    }
}
