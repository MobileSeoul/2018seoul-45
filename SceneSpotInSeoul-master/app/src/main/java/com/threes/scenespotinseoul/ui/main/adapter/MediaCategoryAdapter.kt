package com.threes.scenespotinseoul.ui.main.adapter

import android.support.v7.recyclerview.extensions.ListAdapter
import android.support.v7.util.DiffUtil
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.HORIZONTAL
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import com.threes.scenespotinseoul.R
import com.threes.scenespotinseoul.data.model.Media
import com.threes.scenespotinseoul.utilities.DIR_RIGHT
import com.threes.scenespotinseoul.utilities.ItemOffsetDecoration
import com.threes.scenespotinseoul.utilities.OFFSET_NORMAL
import kotlinx.android.synthetic.main.item_media_category.view.*

class MediaCategoryAdapter(itemCallback: DiffUtil.ItemCallback<MediaCategory> = MediaCategoryDiffCallback()) :
    ListAdapter<MediaCategory, MediaCategoryAdapter.MediaCategoryViewHolder>(itemCallback) {

    var innerItemSelectListener: ((Media) -> Unit)? = null

    // 스크롤 성능을 향상시키기 위해 ViewPool 생성
    // https://proandroiddev.com/optimizing-nested-recyclerview-a9b7830a4ba7
    private val viewPool = RecyclerView.RecycledViewPool()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MediaCategoryViewHolder {
        val viewHolder = MediaCategoryViewHolder(parent)
        viewHolder.listMedia.setRecycledViewPool(viewPool)
        return viewHolder
    }

    override fun onBindViewHolder(holder: MediaCategoryViewHolder, position: Int) {
        with(getItem(position)) {
            holder.tvCategoryName.text = name
            holder.mediaAdapter.submitList(media)
        }
    }

    inner class MediaCategoryViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context)
            .inflate(R.layout.item_media_category, parent, false)
    ) {
        var mediaAdapter = MediaAdapter()
        val tvCategoryName: TextView = itemView.tv_category_name
        val listMedia: RecyclerView = itemView.list_media.apply {
            setHasFixedSize(true)
            addItemDecoration(ItemOffsetDecoration(DIR_RIGHT, OFFSET_NORMAL))
            layoutManager = LinearLayoutManager(context, HORIZONTAL, false)
            mediaAdapter.itemSelectListener = innerItemSelectListener
            adapter = mediaAdapter
        }
    }
}
