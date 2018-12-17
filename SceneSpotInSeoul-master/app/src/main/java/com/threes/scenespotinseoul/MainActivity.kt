package com.threes.scenespotinseoul

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v7.app.AppCompatActivity
import com.threes.scenespotinseoul.ui.gallery.GalleryFragment
import com.threes.scenespotinseoul.ui.main.MainFragment
import com.threes.scenespotinseoul.ui.map.MapFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initViews()
    }

    private fun initViews() {
        setTitle(R.string.title_home)
        view_pager.adapter = MainPagerAdapter(supportFragmentManager, listOf(
            MainFragment(),
            MapFragment(),
            GalleryFragment()
        ))

        bottom_nav.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    setTitle(R.string.title_home)
                    view_pager.setCurrentItem(0, false)
                    true
                }
                R.id.navigation_map -> {
                    setTitle(R.string.title_map)
                    view_pager.setCurrentItem(1, false)
                    true
                }
                R.id.navigation_gallery -> {
                    setTitle(R.string.title_gallery)
                    view_pager.setCurrentItem(2, false)
                    true
                }
                else -> false
            }
        }
    }

    inner class MainPagerAdapter(
        fragmentManager: FragmentManager,
        private val fragments: List<Fragment>
    ) :
        FragmentPagerAdapter(fragmentManager) {
        override fun getItem(position: Int): Fragment = fragments[position]

        override fun getCount(): Int = fragments.size
    }
}
