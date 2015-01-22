package org.jorge.cmp.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.squareup.picasso.Callback;

import org.jorge.cmp.LoLin1Application;
import org.jorge.cmp.R;
import org.jorge.cmp.datamodel.LoLin1Account;
import org.jorge.cmp.io.net.NetworkOperations;
import org.jorge.cmp.io.prefs.PreferenceAssistant;
import org.jorge.cmp.ui.activity.SettingsActivity;
import org.jorge.cmp.ui.adapter.NavigationDrawerAdapter;
import org.jorge.cmp.ui.adapter.NavigationDrawerAdapter.NavigationItem;
import org.jorge.cmp.util.PicassoUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Queue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;

/**
 * @author poliveira
 *         24/10/2014
 */
public class NavigationDrawerFragment extends Fragment implements NavigationDrawerAdapter
        .NavigationDrawerCallbacks {
    private final String TAG = NavigationDrawerFragment.class.getName();
    private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";
    private NavigationDrawerAdapter.NavigationDrawerCallbacks mCallbacks;
    private RecyclerView mDrawerList;
    private View mFragmentContainerView;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mActionBarDrawerToggle;
    private boolean mUserLearnedDrawer;
    private boolean mFromSavedInstanceState;
    private int mCurrentSelectedPosition;
    private ActionBarActivity mActivity;
    private Context mContext;
    private ImageView mUserImageView;
    private final Queue<Runnable> mWhenClosedTasks = new LinkedList<>();

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return mActionBarDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected
                (item);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        PicassoUtils.cancel(mContext, TAG, mUserImageView);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_navigation_drawer, container, Boolean.FALSE);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Required to not to transfer the click to the view behind.
            }
        });
        mDrawerList = (RecyclerView) view.findViewById(R.id.drawerList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mActivity);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mDrawerList.setLayoutManager(layoutManager);
        mDrawerList.setHasFixedSize(Boolean.TRUE);
        mDrawerList.setOverScrollMode(View.OVER_SCROLL_NEVER);

        View helpAction = view.findViewById(R.id.action_help), settingsAction = view.findViewById
                (R.id.action_settings);


        settingsAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeDrawer(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(
                                new Intent(mContext, SettingsActivity.class));
                        mActivity.overridePendingTransition(R.anim.move_in_from_bottom,
                                R.anim.move_out_to_bottom);
                    }
                });
            }
        });

        helpAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeDrawer(new Runnable() {
                    @Override
                    public void run() {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                                Uri.parse(mContext.getString(R.string.help_url)));
                        startActivity(browserIntent);
                    }
                });
            }
        });

        final List<NavigationItem> navigationItems = readMenuItems();
        NavigationDrawerAdapter adapter = new NavigationDrawerAdapter(mContext, navigationItems);
        adapter.setNavigationDrawerCallbacks(this);
        mDrawerList.setAdapter(adapter);
        selectItem(mCurrentSelectedPosition);
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUserLearnedDrawer = PreferenceAssistant.readSharedBoolean(mContext,
                PreferenceAssistant.PREF_USER_LEARNED_DRAWER, Boolean.FALSE);
        if (savedInstanceState != null) {
            mCurrentSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION);
            mFromSavedInstanceState = Boolean.TRUE;
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallbacks = (NavigationDrawerAdapter.NavigationDrawerCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
        }
        mActivity = (ActionBarActivity) activity;
        mContext = LoLin1Application.getInstance().getContext();
    }

    public void setup(int fragmentId, DrawerLayout drawerLayout, Toolbar toolbar,
                      LoLin1Account acc) {
        mFragmentContainerView = mActivity.findViewById(fragmentId);
        mDrawerLayout = drawerLayout;
        mActionBarDrawerToggle = new ActionBarDrawerToggle(mActivity, mDrawerLayout, toolbar,
                R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if (!isAdded()) return;
                mActivity.invalidateOptionsMenu();
                if (!PreferenceAssistant.readSharedBoolean(mContext,
                        PreferenceAssistant.PREF_PULL_TO_REFRESH_LEARNED, Boolean.FALSE)) {
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(mActivity, R.string.pull_to_refresh,
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                    PreferenceAssistant.writeSharedBoolean(mContext,
                            PreferenceAssistant.PREF_PULL_TO_REFRESH_LEARNED, Boolean.TRUE);
                }
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if (!isAdded()) return;
                if (!mUserLearnedDrawer) {
                    mUserLearnedDrawer = Boolean.TRUE;
                    PreferenceAssistant.writeSharedBoolean(mContext,
                            PreferenceAssistant.PREF_USER_LEARNED_DRAWER,
                            Boolean.TRUE);
                }

                mActivity.invalidateOptionsMenu();
            }
        };

        if (!mUserLearnedDrawer && !mFromSavedInstanceState)
            mDrawerLayout.openDrawer(mFragmentContainerView);

        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mActionBarDrawerToggle.syncState();
            }
        });

        mDrawerLayout.setDrawerListener(mActionBarDrawerToggle);

        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);

        mUserImageView = (ImageView) mDrawerLayout.findViewById(R.id.user_photo);

        ((TextView) mDrawerLayout.findViewById(R.id.user_name)).setText(acc.getUsername());
        ((TextView) mDrawerLayout.findViewById(R.id.realm_name)).setText(acc.getRealmEnum().name
                ().toUpperCase(Locale.ENGLISH));
    }

    public void asyncLoadUserImage(LoLin1Account acc) {
        new AsyncTask<Object, Void, String>() {

            private ImageView iv;
            private String realm
                    ,
                    username;
            private final String PROFILE_ICON_URL_PATTERN = NavigationDrawerFragment.this
                    .mContext.getString(R.string.profile_icon_url_pattern)
                    ,
                    REALM_INFO_URL_PATTERN = NavigationDrawerFragment.this
                            .mContext.getString(R.string.realm_info_url_pattern)
                    ,
                    USER_INFO_URL_PATTERN = NavigationDrawerFragment.this
                            .mContext.getString(R.string.profile_icon_id_url_pattern);

            final String KEY_CONTENTS = "CONTENTS";
            final Bundle mVersionBundle = new Bundle()
                    ,
                    mIdBundle = new Bundle();

            @Override
            protected String doInBackground(Object... params) {
                iv = (ImageView) params[0];
                realm = (String) params[1];
                username = (String) params[2];

                final CountDownLatch urlBuilderLatch = new CountDownLatch(2);

                findProfileIconVersion(urlBuilderLatch);
                findProfileIconId(urlBuilderLatch);

                try {
                    urlBuilderLatch.await();
                } catch (InterruptedException e) {
                    //Should never happen
                    Crashlytics.logException(e);
                }

                PreferenceAssistant.writeSharedString(mContext,
                        PreferenceAssistant.PREF_LAST_PROFILE_ICON_VERSION,
                        mVersionBundle.getString(KEY_CONTENTS));

                return String.format(Locale.ENGLISH, PROFILE_ICON_URL_PATTERN,
                        mVersionBundle.getString(KEY_CONTENTS), mIdBundle.getString(KEY_CONTENTS));
            }

            private void findProfileIconVersion(CountDownLatch latch) {
                new AsyncTask<Object, Void, String>() {

                    private CountDownLatch countDownLatch;
                    private String realm;
                    private Bundle retBag;

                    @Override
                    protected String doInBackground(Object... params) {
                        countDownLatch = (CountDownLatch) params[0];
                        realm = (String) params[1];
                        retBag = (Bundle) params[2];

                        try {
                            return new JSONObject(NetworkOperations.performGETRequest(new URL(String
                                    .format(Locale.ENGLISH,
                                            REALM_INFO_URL_PATTERN, realm))).body().string())
                                    .getJSONObject("n")
                                    .getString("profileicon");
                            //If the body is malformed Picasso will find an error and hide the
                            // ImageView,
                            // so it's fine
                        } catch (IOException | JSONException e) {
                            return "null";
                        }
                    }

                    @Override
                    protected void onPostExecute(String s) {
                        retBag.putString(KEY_CONTENTS, s);
                        countDownLatch.countDown();
                    }
                }.executeOnExecutor(Executors.newSingleThreadExecutor(), latch, realm,
                        mVersionBundle);
            }

            private void findProfileIconId(CountDownLatch latch) {
                new AsyncTask<Object, Void, String>() {

                    private CountDownLatch countDownLatch;
                    private String realm;
                    private Bundle retBag;
                    private String username;

                    @Override
                    protected String doInBackground(Object... params) {
                        countDownLatch = (CountDownLatch) params[0];
                        username = (String) params[1];
                        realm = (String) params[2];
                        retBag = (Bundle) params[3];

                        try {
                            return new JSONObject(NetworkOperations.performGETRequest(new URL(String
                                            .format(Locale.ENGLISH, USER_INFO_URL_PATTERN, username,
                                                    realm))
                            ).body().string()).getJSONObject(username.toLowerCase(Locale.ENGLISH)
                            ).getString
                                    ("profileIconId");
                        } catch (JSONException | IOException e) {
                            return "null";
                        }
                    }

                    @Override
                    protected void onPostExecute(String s) {
                        retBag.putString(KEY_CONTENTS, s);
                        countDownLatch.countDown();
                    }
                }.executeOnExecutor(Executors.newSingleThreadExecutor(), latch, username, realm,
                        mIdBundle);
            }

            @Override
            protected void onPostExecute(String imageUrl) {
                PicassoUtils.loadInto(mContext, imageUrl, new Callback() {
                    @Override
                    public void onSuccess() {
                        iv.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onError() {
                        if (iv.isShown())
                            iv.setVisibility(View.GONE);
                    }
                }, iv, TAG);
            }
        }.executeOnExecutor(Executors.newSingleThreadExecutor(), mUserImageView,
                acc.getRealmEnum().name().toUpperCase(Locale.ENGLISH), acc.getUsername());
    }

    public void closeDrawer(Runnable... runnables) {
        mDrawerLayout.closeDrawer(mFragmentContainerView);
        Collections.addAll(mWhenClosedTasks, runnables);
        while (!mWhenClosedTasks.isEmpty()) {
            mActivity.runOnUiThread(mWhenClosedTasks.poll());
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public List<NavigationItem> readMenuItems() {
        List<NavigationItem> items = new ArrayList<>();
        Resources resources = mContext.getResources();
        final String NAVIGATION_TITLE_STANDARD_DRAWABLE_PATTERN = mContext.getString(R.string
                .navigation_title_standard_resource_pattern),
                NAVIGATION_TITLE_SELECTED_DRAWABLE_PATTERN = mContext.getString(R.string
                        .navigation_title_selected_resource_pattern);
        final String[] itemNames = resources.getStringArray(R.array.navigation_drawer_items);
        final List<Drawable> standardItemIcons = new ArrayList<>(),
                selectedItemIcons = new ArrayList<>();
        for (int i = 0; i < itemNames.length; i++) {
            final String standardDrawableResourceName = String.format
                    (Locale.ENGLISH, NAVIGATION_TITLE_STANDARD_DRAWABLE_PATTERN, i),
                    selectedDrawableResourceName = String.format
                            (Locale.ENGLISH, NAVIGATION_TITLE_SELECTED_DRAWABLE_PATTERN, i);
            try {
                final Field standardDrawableResourceField = R.drawable.class.getDeclaredField
                        (standardDrawableResourceName),
                        selectedDrawableResourceField = R.drawable.class.getDeclaredField
                                (selectedDrawableResourceName);
                standardItemIcons.add(resources.getDrawable(standardDrawableResourceField.getInt
                        (standardDrawableResourceField)));
                selectedItemIcons.add(resources.getDrawable(selectedDrawableResourceField.getInt
                        (selectedDrawableResourceField)));
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new IllegalStateException("Expected drawable resource " +
                        standardDrawableResourceName + " not found.");
            }
        }
        if (standardItemIcons.size() < itemNames.length) {
            throw new IllegalStateException("Not enough icons for this many navigation choices");
        }
        for (int i = 0; i < itemNames.length; i++) {
            items.add(new NavigationItem(itemNames[i], standardItemIcons.get(i),
                    selectedItemIcons.get(i)));
        }
        return items;
    }

    public void selectItem(int position) {
        mCurrentSelectedPosition = position;
        if (mDrawerLayout != null) {
            closeDrawer();
        }
        if (mCallbacks != null) {
            mCallbacks.onNavigationDrawerItemSelected(position);
        }
        ((NavigationDrawerAdapter) mDrawerList.getAdapter()).selectPosition(position);
    }

    public boolean isDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(mFragmentContainerView);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mActionBarDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_SELECTED_POSITION, mCurrentSelectedPosition);
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        selectItem(position);
    }

    public int getPosition() {
        return ((NavigationDrawerAdapter) mDrawerList.getAdapter()).getSelectedPosition();
    }

    public void lockDrawerClosed() {
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }

    public void unlockDrawer() {
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
    }
}
