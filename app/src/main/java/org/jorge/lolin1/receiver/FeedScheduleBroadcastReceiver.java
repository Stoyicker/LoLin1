package org.jorge.lolin1.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.jorge.lolin1.datamodel.Realm;
import org.jorge.lolin1.service.CommunityFeedHarvestService;
import org.jorge.lolin1.service.NewsFeedHarvestService;

public class FeedScheduleBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        final Intent newsIntent = new Intent(context, NewsFeedHarvestService.class);
        final Realm[] allRealms = Realm.getAllRealms();

        for (Realm realm : allRealms)
            for (String locale : realm.getLocales()) {
                newsIntent.putExtra(NewsFeedHarvestService.EXTRA_REALM, realm);
                newsIntent.putExtra(NewsFeedHarvestService.EXTRA_LOCALE, locale);
                context.startService(newsIntent);
            }

        final Intent communityIntent = new Intent(context, CommunityFeedHarvestService.class);
        context.startService(communityIntent);

        //TODO Schedule also services for school
    }
}
