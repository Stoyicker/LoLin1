package org.jorge.lolin1.service;

import android.app.IntentService;
import android.content.Intent;

import java.net.MalformedURLException;
import java.net.URL;

public abstract class FeedHarvestService extends IntentService {

    protected static final String EXTRA_SOURCE_URL = "SOURCE_URL", EXTRA_TABLE_NAME = "TABLE_NAME";

    public FeedHarvestService(String className) {
        super(className);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        URL source;
        try {
            source = new URL(intent.getStringExtra(EXTRA_SOURCE_URL));
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Source url " + intent.getStringExtra
                    (EXTRA_SOURCE_URL) + " is malformed.");
        }
        final String tableName = intent.getStringExtra(EXTRA_TABLE_NAME);

        //TODO Fetch source and format it into table
    }
}
