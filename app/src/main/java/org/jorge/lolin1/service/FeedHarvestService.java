package org.jorge.lolin1.service;

import android.app.IntentService;
import android.content.Intent;
import android.text.Html;

import com.squareup.okhttp.Response;

import org.apache.http.HttpStatus;
import org.jorge.lolin1.datamodel.FeedArticle;
import org.jorge.lolin1.io.database.SQLiteDAO;
import org.jorge.lolin1.io.net.NetworkOperations;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

abstract class FeedHarvestService extends IntentService {

    protected static final String EXTRA_SOURCE_URL = "SOURCE_URL", EXTRA_TABLE_NAME = "TABLE_NAME";
    private static final String KEY_IMG_URL = "KEY_IMG_URL", KEY_CONTENT_URL = "KEY_CONTENT_URL",
            KEY_TITLE = "KEY_TITLE", KEY_CONTENT = "KEY_CONTENT";
    private static final Integer SERVER_UPDATING_STATUS_CODE = HttpStatus.SC_CONFLICT;
    private final Object SERVICE_LOCK = new Object();

    public FeedHarvestService(String className) {
        super(className);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        synchronized (SERVICE_LOCK) {
            try {
                try {
                    final URL source = new URL(intent.getStringExtra(EXTRA_SOURCE_URL));
                    final String tableName = intent.getStringExtra(EXTRA_TABLE_NAME);
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
                        FeedArticle thisArticle = new FeedArticle(obj.getString(KEY_TITLE),
                                contentLink,
                                obj.getString(KEY_IMG_URL), Html.fromHtml(obj.getString
                                (KEY_CONTENT))
                                .toString(), Boolean.FALSE);
                        if (!remainders.contains(thisArticle))
                            remainders.add(thisArticle);
                    }
                    SQLiteDAO.getInstance().insertArticlesIntoTable(remainders, tableName);
                } catch (MalformedURLException e) {
                    throw new IllegalArgumentException("Source url " + intent.getStringExtra
                            (EXTRA_SOURCE_URL) + " is malformed.");
                } catch (JSONException e) {
                    //Some error on the communication. Just finish and hope for better luck next
                    // time
                }
            } catch (Exception e) {
                //App got stopped when service is running. It's fine
            }
        }
    }
}
