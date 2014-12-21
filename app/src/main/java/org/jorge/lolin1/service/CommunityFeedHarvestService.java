package org.jorge.lolin1.service;

import android.content.Intent;

import org.jorge.lolin1.io.database.SQLiteDAO;

import java.util.Locale;

public class CommunityFeedHarvestService extends FeedHarvestService {

    private static final String COMMUNITY_URL = "http://lolin1-feed-parser.herokuapp" +
            ".com/services/community";

    public CommunityFeedHarvestService() {
        super(CommunityFeedHarvestService.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        intent.putExtra(EXTRA_SOURCE_URL, COMMUNITY_URL);
        intent.putExtra(EXTRA_TABLE_NAME, String.format(Locale.ENGLISH,
                SQLiteDAO.getCommunityTableName()));
        super.onHandleIntent(intent);
    }
}
