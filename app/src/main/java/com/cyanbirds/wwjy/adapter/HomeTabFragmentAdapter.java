package com.cyanbirds.wwjy.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.cyanbirds.wwjy.fragment.FindLoveFragment;

import java.util.List;

/**
 * @author: wangyb
 * @datetime: 2016-01-02 16:31 GMT+8
 * @email: 395044952@qq.com
 * @description:
 */
public class HomeTabFragmentAdapter extends FragmentPagerAdapter {

    private List<String> mTitles;

    public HomeTabFragmentAdapter(FragmentManager fm, List<String> titles) {
        super(fm);
        mTitles = titles;
    }

    @Override
    public Fragment getItem(int position) {
        return FindLoveFragment.newInstance(position);
    }

    @Override
    public int getCount() {
        return mTitles.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTitles.get(position);
    }
}
