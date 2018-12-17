package com.threes.scenespotinseoul.ui.main.adapter

import android.support.v7.util.DiffUtil

class MediaCategoryDiffCallback : DiffUtil.ItemCallback<MediaCategory>() {

    override fun areItemsTheSame(oldItem: MediaCategory, newItem: MediaCategory): Boolean =
        (oldItem.name == newItem.name)

    override fun areContentsTheSame(oldItem: MediaCategory, newItem: MediaCategory): Boolean =
        (oldItem.name == newItem.name)
}