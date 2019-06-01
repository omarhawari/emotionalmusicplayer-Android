package com.eltrio.emotionalmusicplayer.my_classes

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import java.util.ArrayList

class MyPagerAdapter : FragmentStatePagerAdapter {

    private val fragmentList = ArrayList<MyFragment>()
    private val titles = ArrayList<String>()

    constructor(fm: FragmentManager, fragmentList: List<MyFragment>, titles: List<String>) : super(fm) {
        this.fragmentList.addAll(fragmentList)
        this.titles.addAll(titles)
    }

    constructor(fm: FragmentManager, fragmentList: List<MyFragment>) : super(fm) {
        this.fragmentList.addAll(fragmentList)
    }

    override fun getItem(position: Int): Fragment {
        return fragmentList[position]
    }

    override fun getCount(): Int {
        return fragmentList.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return titles[position]
    }


}
