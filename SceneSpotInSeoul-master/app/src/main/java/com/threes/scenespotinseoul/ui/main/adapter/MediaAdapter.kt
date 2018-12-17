package com.threes.scenespotinseoul.ui.main.adapter

import android.support.v7.recyclerview.extensions.ListAdapter
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.threes.scenespotinseoul.R
import com.threes.scenespotinseoul.data.model.Media
import kotlinx.android.synthetic.main.item_content.view.*

class MediaAdapter(itemCallback: DiffUtil.ItemCallback<Media> = MediaDiffCallback()) :
    ListAdapter<Media, MediaAdapter.MediaViewHolder>(itemCallback) {

    var itemSelectListener: ((Media) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MediaViewHolder = MediaViewHolder(parent)

    override fun onBindViewHolder(holder: MediaViewHolder, position: Int) {
        with(getItem(position)) {
            holder.itemView.setOnClickListener {
                itemSelectListener?.invoke(this)
            }
            Glide.with(holder.itemView)
                .load(image)
                .apply(RequestOptions.centerCropTransform())
                .into(holder.ivMedia)

            holder.tvName.text = name
        }
    }

    inner class MediaViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context)
            .inflate(R.layout.item_content, parent, false)
    ) {
        val ivMedia: ImageView = itemView.iv_image
        val tvName: TextView = itemView.tv_name
    }
}