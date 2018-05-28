package io.starter.biruk.ezymusic.view.artistsView.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by Biruk on 10/11/2017.
 */
public class ArtistViewPagerAdapter extends FragmentPagerAdapter {

    private Fragment[] fragments;
    private String[] title;

    public ArtistViewPagerAdapter(FragmentManager fm, Fragment[] fragments, String[] title) {
        super(fm);
        this.fragments = fragments;
        this.title = title;
    }

    @Override
    public Fragment getItem(int position) {
        return fragments[position];
    }

    @Override
    public int getCount() {
        return fragments.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return title[position];
    }
}
