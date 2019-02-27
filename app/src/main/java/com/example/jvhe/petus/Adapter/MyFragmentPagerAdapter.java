package com.example.jvhe.petus.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import java.util.ArrayList;

public class MyFragmentPagerAdapter extends FragmentPagerAdapter {

    private String[] VIEW_MAPNTOP_TITLES = {"TAB1", "TAB2", "TAB3"};
    private ArrayList<Fragment> fList;

    // 어댑터 생성자
    public MyFragmentPagerAdapter(FragmentManager fm, ArrayList<Fragment> fList) {
        super(fm);
        this.fList = fList;
    }

    @Override
    public Fragment getItem(int position) {
        return this.fList.get(position);
    }

    // tab title를 반환 함수.
    @Override
    public CharSequence getPageTitle(int position) {
        return VIEW_MAPNTOP_TITLES[position];
    }

    @Override
    public int getCount() {
        return fList.size();
    }


}
