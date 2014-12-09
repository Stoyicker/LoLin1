package org.jorge.lolin1.service;

import android.content.Intent;

public class NewsFeedHarvestService extends FeedHarvestService {

    public NewsFeedHarvestService() {
        super(NewsFeedHarvestService.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        //TODO Send the right data
        intent.putExtra(EXTRA_SOURCE_URL, "http://feed43.com/lolnews_euw_en.xml");
        intent.putExtra(EXTRA_TABLE_NAME, "NEWS_EUW_EN_US");
        super.onHandleIntent(intent);
    }
}
