package org.jorge.lolin1.service;

import android.content.Intent;

public class NewsFeedHarvestService extends FeedHarvestService {

    @SuppressWarnings("FieldCanBeLocal")
    private final String NEWS_URL_TEMPLATE = "http://lolin1-feed-parser.herokuapp" +
            ".com/services/news?realm=%s&locale=%s";

    public NewsFeedHarvestService() {
        super(NewsFeedHarvestService.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        final String upperCaseRealm = intent.getStringExtra(EXTRA_REALM),
                upperCaseLocale = intent.getStringExtra(EXTRA_LOCALE);
        intent.putExtra(EXTRA_SOURCE_URL, String.format(NEWS_URL_TEMPLATE, upperCaseRealm,
                upperCaseLocale));
        intent.putExtra(EXTRA_TABLE_NAME, "NEWS_" + upperCaseRealm + "_" + upperCaseLocale);
        super.onHandleIntent(intent);
    }
}
