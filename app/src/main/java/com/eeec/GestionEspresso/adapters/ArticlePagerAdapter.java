package com.eeec.GestionEspresso.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.eeec.GestionEspresso.fragments.AdFragment;
import com.eeec.GestionEspresso.fragments.ArticleEventListener;
import com.eeec.GestionEspresso.fragments.ArticleFragment;
import com.eeec.GestionEspresso.model.KAd;
import com.eeec.GestionEspresso.model.KArticle;
import com.eeec.GestionEspresso.model.KEdition;
import com.eeec.GestionEspresso.model.KObject;

import java.util.List;

public class ArticlePagerAdapter extends FragmentPagerAdapter {
    public KEdition edition;
    public List<KObject> articles;
    private ArticleEventListener articleListener;

    public ArticlePagerAdapter(FragmentManager fm, ArticleEventListener listener) {
        super(fm);
        this.articleListener = listener;
    }

    @Override
    public Fragment getItem(int position) {
        KObject element = this.getObject(position);

        if (element instanceof KAd) {
            return AdFragment.newInstance(position, (KAd) element);
        } else {
            return ArticleFragment.newInstance(position, (KArticle) element, articleListener );
        }
    }



    @Override
    public int getCount() {
        return this.articles.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return null;
    }

    public KObject getObject(int position) {
        return this.articles.get(position);
    }

}