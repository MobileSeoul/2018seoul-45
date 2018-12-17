package com.threes.scenespotinseoul.ui.main.adapter

import android.support.v7.util.DiffUtil
import com.threes.scenespotinseoul.data.model.Media

class MediaDiffCallback : DiffUtil.ItemCallback<Media>() {

    override fun areItemsTheSame(oldItem: Media, newItem: Media): Boolean =
        (oldItem.uuid == newItem.uuid)

    override fun areContentsTheSame(oldItem: Media, newItem: Media): Boolean =
        (oldItem.uuid == newItem.uuid)
}