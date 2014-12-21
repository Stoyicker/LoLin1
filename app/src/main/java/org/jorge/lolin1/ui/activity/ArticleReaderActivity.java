package org.jorge.lolin1.ui.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import org.jorge.lolin1.LoLin1Application;
import org.jorge.lolin1.R;
import org.jorge.lolin1.datamodel.FeedArticle;
import org.jorge.lolin1.ui.fragment.ArticleReaderFragment;

public class ArticleReaderActivity extends ActionBarActivity {

    public static final String READER_LIST_FRAGMENT_CLASS = "CLASS";
    private Fragment mArticleReaderFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_reader);

        final Context context = LoLin1Application.getInstance().getContext();

        Bundle extras = getIntent().getExtras();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(toolbar);

        if (mArticleReaderFragment == null) {
            FeedArticle article = extras.getParcelable(ArticleReaderFragment.ARTICLE_KEY);
            Class c = (Class) extras.getSerializable(ArticleReaderActivity
                    .READER_LIST_FRAGMENT_CLASS);
            mArticleReaderFragment = ArticleReaderFragment.newInstance(context, article, c);
        }
        getSupportFragmentManager().beginTransaction().replace(R.id
                .article_reader_fragment_container, mArticleReaderFragment)
                .commitAllowingStateLoss();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                overridePendingTransition(R.anim.move_in_from_bottom, R.anim.move_out_to_bottom);
                return Boolean.TRUE;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.move_in_from_bottom, R.anim.move_out_to_bottom);
    }
}
