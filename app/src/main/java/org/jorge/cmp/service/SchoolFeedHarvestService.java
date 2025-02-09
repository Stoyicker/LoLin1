package org.jorge.cmp.service;

import android.content.Intent;

import org.jorge.cmp.io.database.SQLiteDAO;

public class SchoolFeedHarvestService extends FeedHarvestService {

    private static final String COMMUNITY_URL = "http://lolin1-feed-parser.herokuapp" +
            ".com/services/school";

    public SchoolFeedHarvestService() {
        super(SchoolFeedHarvestService.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        intent.putExtra(EXTRA_SOURCE_URL, COMMUNITY_URL);
        intent.putExtra(EXTRA_TABLE_NAME, SQLiteDAO.getSchoolTableName());
        super.onHandleIntent(intent);
    }
}
