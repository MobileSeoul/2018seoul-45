package com.threes.scenespotinseoul.ui.main

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import com.threes.scenespotinseoul.R
import com.threes.scenespotinseoul.data.model.Media
import com.threes.scenespotinseoul.ui.main.adapter.MediaCategoryAdapter
import com.threes.scenespotinseoul.ui.media.MediaDetailActivity
import com.threes.scenespotinseoul.ui.search.SearchActivity
import com.threes.scenespotinseoul.utilities.DIR_BOTTOM
import com.threes.scenespotinseoul.utilities.EXTRA_MEDIA_ID
import com.threes.scenespotinseoul.utilities.ItemOffsetDecoration
import com.threes.scenespotinseoul.utilities.OFFSET_NORMAL
import kotlinx.android.synthetic.main.fragment_main.*

class MainFragment : Fragment() {

    private lateinit var viewModel: MainViewModel
    private lateinit var mediaCategoryAdapter: MediaCategoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
            inflater.inflate(R.layout.fragment_main, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        initViews()
        viewModel.mediaCount.observe(this, Observer {
            if (it != null && it > 0) {
                viewModel.loadMediaCategory()
            }
        })
        viewModel.mediaCategory.observe(this, Observer {
            mediaCategoryAdapter.submitList(it)
        })
    }

    override fun onResume() {
        super.onResume()
        activity?.invalidateOptionsMenu()
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater?.inflate(R.menu.menu_main, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_search -> startActivity(Intent(activity, SearchActivity::class.java))
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initViews() {
        mediaCategoryAdapter = MediaCategoryAdapter()
        mediaCategoryAdapter.innerItemSelectListener = {
            navigateMediaDetail(it)
        }

        list_media_category.setHasFixedSize(true)
        list_media_category.addItemDecoration(ItemOffsetDecoration(DIR_BOTTOM, OFFSET_NORMAL))
        list_media_category.layoutManager = LinearLayoutManager(context)
        list_media_category.adapter = mediaCategoryAdapter
    }

    private fun navigateMediaDetail(it: Media) {
        val intent = Intent(context, MediaDetailActivity::class.java)
        intent.putExtra(EXTRA_MEDIA_ID, it.uuid)
        startActivity(intent)
    }
}