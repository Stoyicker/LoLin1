package org.jorge.lolin1.service;

import android.app.IntentService;
import android.content.Intent;
import android.text.Html;

import com.squareup.okhttp.Response;

import org.apache.http.HttpStatus;
import org.jorge.lolin1.datamodel.FeedArticle;
import org.jorge.lolin1.io.net.NetworkOperations;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public abstract class FeedHarvestService extends IntentService {

    protected static final String EXTRA_SOURCE_URL = "SOURCE_URL", EXTRA_TABLE_NAME = "TABLE_NAME";
    private static final String KEY_IMG_URL = "KEY_IMG_URL", KEY_CONTENT_URL = "KEY_CONTENT_URL",
            KEY_TITLE = "KEY_TITLE", KEY_CONTENT = "KEY_CONTENT";
    private static final Integer SERVER_UPDATING_STATUS_CODE = HttpStatus.SC_CONFLICT;

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
            Response resp;
            JSONArray array;
            try {
                resp = NetworkOperations.doJSONRequest(source);
                if (resp.code() == SERVER_UPDATING_STATUS_CODE)
                    throw new IOException("Server is updating");
                array = new JSONArray(resp.body().string());
            } catch (IOException e) {
                //Just finish without any new news
                return;
            }
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
