package org.jorge.cmp.service;

import android.content.Intent;

import org.jorge.cmp.datamodel.Realm;
import org.jorge.cmp.io.database.SQLiteDAO;

import java.util.Locale;

public class NewsFeedHarvestService extends FeedHarvestService {

    private static final String NEWS_URL_TEMPLATE = "http://lolin1-feed-parser.herokuapp" +
            ".com/services/news?realm=%s&locale=%s";
    public static final String EXTRA_REALM = "REALM", EXTRA_LOCALE = "LOCALE";

    public NewsFeedHarvestService() {
        super(NewsFeedHarvestService.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        final Realm realm = intent.getParcelableExtra(EXTRA_REALM);
        final String locale = intent.getStringExtra(EXTRA_LOCALE);
        intent.putExtra(EXTRA_SOURCE_URL, String.format(Locale.ENGLISH, NEWS_URL_TEMPLATE,
                realm.toString(),
                locale));
        intent.putExtra(EXTRA_TABLE_NAME, String.format(Locale.ENGLISH,
                SQLiteDAO.getNewsTableName(realm, locale)));
        super.onHandleIntent(intent);
    }
}
