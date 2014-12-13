package org.jorge.lolin1.service;

import android.content.Intent;

public class NewsFeedHarvestService extends FeedHarvestService {

    private static final String NEWS_URL_TEMPLATE = "http://lolin1-feed-parser.herokuapp" +
            ".com/services/news?realm=%s&locale=%s", NEWS_TABLE_NAME_TEMPLATE = "NEWS_%s_%s";
    private static final String EXTRA_REALM = "REALM", EXTRA_LOCALE = "LOCALE";

    public NewsFeedHarvestService() {
        super(NewsFeedHarvestService.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        final String upperCaseRealm = intent.getStringExtra(EXTRA_REALM),
                upperCaseLocale = intent.getStringExtra(EXTRA_LOCALE);
        intent.putExtra(EXTRA_SOURCE_URL, String.format(NEWS_URL_TEMPLATE, upperCaseRealm,
                upperCaseLocale));
        intent.putExtra(EXTRA_TABLE_NAME, String.format(NEWS_TABLE_NAME_TEMPLATE, upperCaseRealm,
                upperCaseLocale));
        super.onHandleIntent(intent);
    }
}
