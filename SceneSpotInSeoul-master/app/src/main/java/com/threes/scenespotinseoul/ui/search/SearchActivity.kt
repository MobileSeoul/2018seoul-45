package com.threes.scenespotinseoul.ui.search

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import com.threes.scenespotinseoul.R
import com.threes.scenespotinseoul.data.model.Location
import com.threes.scenespotinseoul.data.model.Media
import com.threes.scenespotinseoul.data.model.Scene
import com.threes.scenespotinseoul.ui.location.LocationDetailActivity
import com.threes.scenespotinseoul.ui.main.adapter.SearchResultCategoryAdapter
import com.threes.scenespotinseoul.ui.media.MediaDetailActivity
import com.threes.scenespotinseoul.ui.scene.SceneDetailActivity
import com.threes.scenespotinseoul.ui.search.SearchViewModel.Companion.TYPE_EXACTLY
import com.threes.scenespotinseoul.ui.search.SearchViewModel.Companion.TYPE_SIMILAR
import com.threes.scenespotinseoul.utilities.DIR_BOTTOM
import com.threes.scenespotinseoul.utilities.EXTRA_LOCATION_ID
import com.threes.scenespotinseoul.utilities.EXTRA_MEDIA_ID
import com.threes.scenespotinseoul.utilities.EXTRA_SCENE_ID
import com.threes.scenespotinseoul.utilities.EXTRA_SEARCH_KEYWORD
import com.threes.scenespotinseoul.utilities.ItemOffsetDecoration
import com.threes.scenespotinseoul.utilities.OFFSET_NORMAL
import kotlinx.android.synthetic.main.activity_search.*

class SearchActivity : AppCompatActivity() {

    private lateinit var viewModel: SearchViewModel
    private lateinit var searchResultCategoryAdapter: SearchResultCategoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        viewModel = ViewModelProviders.of(this).get(SearchViewModel::class.java)
        initViews()
        viewModel.showSearchResult.observe(this, Observer {
            searchResultCategoryAdapter.submitData(
                    viewModel.searchResultMediaData,
                    viewModel.searchResultSceneData,
                    viewModel.searchResultLocationData
            )
            if (list_search_result.adapter == null) {
                list_search_result.adapter = searchResultCategoryAdapter
            }
        })
        if (intent.hasExtra(EXTRA_SEARCH_KEYWORD)) {
            val keyword = intent.getStringExtra(EXTRA_SEARCH_KEYWORD)
            search_view.setAutoCompletedText(keyword)
            viewModel.requestSearch(TYPE_EXACTLY, keyword)
        } else {
            search_view.requestEditTextFocus()
        }
    }

    private fun initViews() {
        list_search_result.setHasFixedSize(true)
        list_search_result.addItemDecoration(ItemOffsetDecoration(DIR_BOTTOM, OFFSET_NORMAL))
        list_search_result.layoutManager = LinearLayoutManager(this)

        searchResultCategoryAdapter = SearchResultCategoryAdapter()
        searchResultCategoryAdapter.innerItemSelectListener = {
            when (it) {
                is Media -> navigateMediaDetail(it)
                is Scene -> navigateSceneDetail(it)
                is Location -> navigateLocationDetail(it)
            }
        }

        search_view.setAutoCompleteData(this, viewModel.tagAutoCompleteData)
        search_view.editOnTextChangeListener = { keyword, _, _, _ ->
            viewModel.loadTagAutoComplete(keyword)
        }
        search_view.editActionListener = {
            viewModel.requestSearch(TYPE_SIMILAR, it)
        }
        search_view.autoCompleteSelectListener = {
            viewModel.requestSearch(TYPE_EXACTLY, it)
        }
        search_view.backButtonClickListener = {
            finish()
        }
    }

    private fun navigateMediaDetail(it: Media) {
        val intent = Intent(this, MediaDetailActivity::class.java)
        intent.putExtra(EXTRA_MEDIA_ID, it.uuid)
        startActivity(intent)
    }

    private fun navigateSceneDetail(it: Scene) {
        val intent = Intent(this, SceneDetailActivity::class.java)
        intent.putExtra(EXTRA_SCENE_ID, it.uuid)
        startActivity(intent)
    }

    private fun navigateLocationDetail(it: Location) {
        val intent = Intent(this, LocationDetailActivity::class.java)
        intent.putExtra(EXTRA_LOCATION_ID, it.uuid)
        startActivity(intent)
    }
}