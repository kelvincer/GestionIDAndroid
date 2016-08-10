package com.eeec.GestionEspresso;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;

import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.ImageButton;

import com.comscore.analytics.comScore;
import com.eeec.GestionEspresso.adapters.ArticlePagerAdapter;
import com.eeec.GestionEspresso.fragments.ArticleEventListener;
import com.eeec.GestionEspresso.fragments.ArticleFragment;
import com.eeec.GestionEspresso.model.KAd;
import com.eeec.GestionEspresso.model.KArticle;
import com.eeec.GestionEspresso.model.KEdition;
import com.eeec.GestionEspresso.model.KObject;
import com.eeec.GestionEspresso.util.LogUtil;
import com.eeec.GestionEspresso.views.TutorialView;

import java.util.ArrayList;
import java.util.List;

public class ArticlesActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private ArticlePagerAdapter articlePagerAdapter;
    private FloatingActionButton homeButton;
    private ViewPager viewPager;
    //private ImageButton nextNoteButton;
    private TutorialView tutorialView;
    public int currentIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_articles);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        Bundle bundle = getIntent().getExtras();
        KEdition edition = (KEdition) bundle.getSerializable("edition");

        List<KObject> articles = new ArrayList<>(edition.getArticles());

        int categoryIndex = bundle.getInt("categoryIndex", 0);
        int initialIndex = bundle.getInt("articleIndex", 0);
        for (int i = 0; i < categoryIndex && i < edition.categories.size(); i++) {
            initialIndex += edition.categories.get(i).articles.size();
        }

        /*
        nextNoteButton = (ImageButton) findViewById(R.id.nextNoteButton);
        nextNoteButton.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.d("RIX_TRACE", "nextSlide!" + (currentIndex + 1));
                viewPager.setCurrentItem(currentIndex + 1, true);
                return true;
            }
        });
        */

        ArticleEventListener articleListener = new ArticleEventListener() {
            @Override
            public void onNextNotePressed(int currentPosition) {
                viewPager.setCurrentItem(currentPosition + 1, true);
            }
        };
        articlePagerAdapter = new ArticlePagerAdapter(getSupportFragmentManager(), articleListener );
        articlePagerAdapter.edition = edition;
        articlePagerAdapter.articles = articles;

        SharedPreferences settings = getSharedPreferences("editionSettings", 0);
        long lastReset = settings.getLong("lastRest-" + edition.id, 0);
        int articleViewCount = settings.getInt("articleViewCount-" + edition.id, 0);
        if (System.currentTimeMillis() - lastReset > 5 * 60 * 1000) {
            SharedPreferences.Editor editor = settings.edit();
            editor.putInt("articleViewCount-" + edition.id, 0);
            editor.putLong("lastRest-" + edition.id, System.currentTimeMillis());
            editor.commit();
            articleViewCount = 0;
        }

        int lastIndex = -1;
        int lastPosition = -1;
        int adCount = 0;
        for (KAd ad : edition.getAds()) {
            if (articleViewCount == 0) {
                ad.shown = false;
            }
            if (!ad.shown) {
                if (lastIndex < 0) {
                    lastIndex = Math.min(Math.max(initialIndex + ad.position - articleViewCount, initialIndex), articles.size());
                } else {
                    lastIndex += ad.position - lastPosition + adCount;
                }

                lastPosition = ad.position;

                if (lastIndex <= articles.size() && lastIndex >= 0) {
                    articles.add(lastIndex, ad);
                    adCount++;
                }
            }
        }


        homeButton = (FloatingActionButton) findViewById(R.id.fab);
        hideHomeButton();
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        viewPager = (ViewPager) findViewById(R.id.container);
        viewPager.setAdapter(articlePagerAdapter);
        viewPager.setCurrentItem(initialIndex, false);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            public void onPageSelected(int pageNumber) {
                setCurrentIndex(pageNumber);
            }

            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            public void onPageScrollStateChanged(int arg0) {
            }
        });

        setCurrentIndex(initialIndex);

        View closeButton = findViewById(R.id.galleryCloseButton);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View container = findViewById(R.id.galleyContainer);
                container.setVisibility(View.GONE);
            }
        });


    }

    private void hideHomeButton(){
        homeButton.setVisibility(View.GONE);
    }

    private void animHomeVisibility(boolean flag)
    {

        homeButton.setVisibility(View.VISIBLE);
        int alpha = flag  ? 1 : 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            final Interpolator interpolador = AnimationUtils.loadInterpolator(getBaseContext(),
                    android.R.interpolator.fast_out_slow_in);
            homeButton.animate()
                    .alpha(alpha)
                    .setInterpolator(interpolador)
                    .setDuration(600)
                    .setStartDelay(0)
                    .setListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            /*fab.animate()
                                    .scaleY(0)
                                    .scaleX(0)
                                    .setInterpolator(interpolador)
                                    .setDuration(600)
                                    .start();*/
                            homeButton.clearAnimation();
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    });
        }
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            ObjectAnimator animator = ObjectAnimator.ofFloat(homeButton, View.ALPHA, alpha );
            animator.setDuration(500l);
            animator.start();
        }
    }

    @Override
    public void onBackPressed() {
        View container = findViewById(R.id.galleyContainer);
        if (container.getVisibility() != View.GONE) {
            container.setVisibility(View.GONE);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        comScore.onEnterForeground();
    }

    @Override
    protected void onPause() {
        super.onPause();
        comScore.onExitForeground();
    }

    public void setCurrentIndex(int index) {
        this.currentIndex = index;

        ArticlePagerAdapter articlePagerAdapter = ArticlesActivity.this.articlePagerAdapter;
        KEdition edition = articlePagerAdapter.edition;
        SharedPreferences settings = getSharedPreferences("editionSettings", 0);
        SharedPreferences.Editor editor = settings.edit();

        KObject el = (KObject) articlePagerAdapter.getObject(index);

        int nextPosition = calculateNextPosition(articlePagerAdapter, index);

        //nextNoteButton.setVisibility(View.GONE);

        if (el instanceof KAd) {
            ArticlesActivity.this.getSupportActionBar().hide();
            KAd ad = (KAd) el;
            ad.shown = true;
            AnalyticsTrackers.getInstance().trackEvent("Publicidad-impresiones", "" + ad.position, ad.campaignCode);
            hideHomeButton();
        } else {

            boolean showTutorial = settings.getBoolean("showTutorial", true);

            if( showTutorial ) {
                editor.putBoolean("showTutorial", false);
                tutorialView = new TutorialView(this);
                tutorialView.show();
            }

           // if( nextPosition > -1 ) nextNoteButton.setVisibility(View.VISIBLE);

            ArticlesActivity.this.getSupportActionBar().show();
            int articleViewCount = settings.getInt("articleViewCount-" + edition.id, 0);
            editor.putInt("articleViewCount-" + edition.id, articleViewCount + 1);
            trackArticleView((KArticle) articlePagerAdapter.getObject(index));

            String fragmentTag = "android:switcher:" + R.id.container + ":" + index;
            ArticleFragment fragment = (ArticleFragment) ArticlesActivity.this.getSupportFragmentManager().findFragmentByTag(fragmentTag);
            if (fragment != null) {
                fragment.showNextButton( nextPosition > -1 );
                fragment.onShow();
            }

            animHomeVisibility(true);
        }
        editor.apply();
    }

    private int calculateNextPosition(ArticlePagerAdapter articlePagerAdapter, int position){
        int nextPos = position;
        do {

            if (++nextPos >= articlePagerAdapter.getCount()) return -1;
            KObject element = articlePagerAdapter.getObject(nextPos);
            if( element instanceof KArticle ){
                return nextPos;
            }

        } while( true );

    }

    public void trackArticleView(KArticle article) {
        if (article.isLoaded) {
            AnalyticsTrackers.getInstance().trackScreenView(article.categoryName + "/" + article.title + "-" + article.id);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_articles, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        if (item != null && item.getItemId() == R.id.shareBarButton) {
            int position = this.viewPager.getCurrentItem();
            ArticlePagerAdapter adapter = (ArticlePagerAdapter) this.viewPager.getAdapter();

            try {
                KArticle article = (KArticle) adapter.getObject(position);
                String shareText = article.shareUrl;
                if (shareText.length() > 0) {

                    AnalyticsTrackers.getInstance().trackEvent("COMPARTIR-NOTA", "Click", article.categoryName + "/" + article.title + "-" + article.id);

                    Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                    sharingIntent.setType("text/plain");
                    sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, article.title);
                    sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, article.shareUrl);
                    startActivity(Intent.createChooser(sharingIntent, "Compartir"));
                    return true;
                }
            } catch (Exception e) {
                LogUtil.logException(e);
            }
        }
        return false;
    }
}


