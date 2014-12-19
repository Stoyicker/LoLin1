package org.jorge.lolin1.service;

import android.app.IntentService;
import android.content.Intent;
import android.text.Html;

import org.jorge.lolin1.datamodel.FeedArticle;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public abstract class FeedHarvestService extends IntentService {

    protected static final String EXTRA_SOURCE_URL = "SOURCE_URL", EXTRA_TABLE_NAME = "TABLE_NAME";
    private static final String KEY_IMG_URL = "KEY_IMG_URL", KEY_CONTENT_URL = "KEY_CONTENT_URL",
            KEY_TITLE = "KEY_TITLE", KEY_CONTENT = "KEY_CONTENT";

    public FeedHarvestService(String className) {
        super(className);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            final URL source = new URL(intent.getStringExtra(EXTRA_SOURCE_URL));
            final String tableName = intent.getStringExtra(EXTRA_TABLE_NAME),
                    mostRecentContentLinkLowerCase = "".toLowerCase(); //TODO Fetch
            // mostRecentContentLinkLowerCase
            final List<FeedArticle> remainders = new ArrayList<>();
            JSONArray array = null; //TODO Fix NewApi warning in new org.json.JSONArray(source);
            for (int i = 0; i < array.length(); i++) {
                final JSONObject obj = array.getJSONObject(i);
                final String contentLink = obj.getString(KEY_CONTENT_URL);
                if (contentLink.toLowerCase().contentEquals(mostRecentContentLinkLowerCase))
                    break;
                else {
                    remainders.add(new FeedArticle(obj.getString(KEY_TITLE), contentLink,
                            obj.getString(KEY_IMG_URL), Html.fromHtml(obj.getString(KEY_CONTENT))
                            .toString()));
                }
            }
            //TODO Add remainders to db's tableName
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Source url " + intent.getStringExtra
                    (EXTRA_SOURCE_URL) + " is malformed.");
        } catch (JSONException e) {
            //Some error on the communication. Just finish and hope for better luck next time
        }
    }
}
