package org.jorge.cmp.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.jorge.cmp.datamodel.Realm;
import org.jorge.cmp.service.CommunityFeedHarvestService;
import org.jorge.cmp.service.NewsFeedHarvestService;
import org.jorge.cmp.service.SchoolFeedHarvestService;

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

        final Intent schoolIntent = new Intent(context, SchoolFeedHarvestService.class);
        context.startService(schoolIntent);
    }
}
