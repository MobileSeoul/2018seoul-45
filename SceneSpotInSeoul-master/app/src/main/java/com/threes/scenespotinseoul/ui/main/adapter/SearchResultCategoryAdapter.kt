package com.threes.scenespotinseoul.ui.main.adapter

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.TextView
import com.threes.scenespotinseoul.R
import com.threes.scenespotinseoul.data.model.Location
import com.threes.scenespotinseoul.data.model.Media
import com.threes.scenespotinseoul.data.model.Scene
import com.threes.scenespotinseoul.utilities.DIR_RIGHT
import com.threes.scenespotinseoul.utilities.ItemOffsetDecoration
import com.threes.scenespotinseoul.utilities.OFFSET_NORMAL
import kotlinx.android.synthetic.main.item_search_result_category.view.*

class SearchResultCategoryAdapter(
    private var media: List<Media> = listOf(),
    private var scenes: List<Scene> = listOf(),
    private var locations: List<Location> = listOf()
) :
    RecyclerView.Adapter<SearchResultCategoryAdapter.SearchResultCategoryViewHolder>() {

    private val viewPool = RecyclerView.RecycledViewPool()
    var innerItemSelectListener: ((Any) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchResultCategoryViewHolder {
        val viewHolder = SearchResultCategoryViewHolder(parent)
        viewHolder.listResult.setRecycledViewPool(viewPool)
        return viewHolder
    }

    override fun onBindViewHolder(holder: SearchResultCategoryViewHolder, position: Int) {
        val context = holder.itemView.context
        with(holder) {
            when (position) {
                TYPE_MEDIA -> {
                    tvCategoryName.text = context.getString(R.string.category_media)
                    if (media.isEmpty()) {
                        tvNoResults.visibility = VISIBLE
                        listResult.adapter = null
                    } else {
                        tvNoResults.visibility = INVISIBLE
                        val adapter = SearchResultAdapter(media)
                        adapter.itemSelectListener = innerItemSelectListener
                        listResult.adapter = adapter
                    }
                }
                TYPE_SCENE -> {
                    tvCategoryName.text = context.getString(R.string.category_scene)
                    if (scenes.isEmpty()) {
                        tvNoResults.visibility = VISIBLE
                        listResult.adapter = null
                    } else {
                        tvNoResults.visibility = INVISIBLE
                        val adapter = SearchResultAdapter(scenes)
                        adapter.itemSelectListener = innerItemSelectListener
                        listResult.adapter = adapter
                    }
                }
                TYPE_LOCATION -> {
                    tvCategoryName.text = context.getString(R.string.category_location)
                    if (locations.isEmpty()) {
                        tvNoResults.visibility = VISIBLE
                        listResult.adapter = null
                    } else {
                        tvNoResults.visibility = INVISIBLE
                        val adapter = SearchResultAdapter(locations)
                        adapter.itemSelectListener = innerItemSelectListener
                        listResult.adapter = adapter
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int = 3

    fun submitData(newMedia: List<Media>, newScenes: List<Scene>, newLocations: List<Location>) {
        media = newMedia.toList()
        scenes = newScenes.toList()
        locations = newLocations.toList()
        notifyDataSetChanged()
    }

    inner class SearchResultCategoryViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context)
            .inflate(R.layout.item_search_result_category, parent, false)
    ) {
        val tvCategoryName: TextView = itemView.tv_category_name
        val tvNoResults: TextView = itemView.tv_no_results
        val listResult: RecyclerView = itemView.list_result.apply {
            setHasFixedSize(true)
            addItemDecoration(ItemOffsetDecoration(DIR_RIGHT, OFFSET_NORMAL))
            layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        }
    }

    companion object {
        const val TYPE_MEDIA = 0
        const val TYPE_SCENE = 1
        const val TYPE_LOCATION = 2
    }
}