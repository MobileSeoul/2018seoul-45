package com.threes.scenespotinseoul.ui.search.widget

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import com.threes.scenespotinseoul.R
import kotlinx.android.synthetic.main.item_search_complete.view.*

class SearchAutoCompleteAdapter(private var items: List<String> = listOf()) :
    RecyclerView.Adapter<SearchAutoCompleteAdapter.SearchCompleteViewHolder>() {
    lateinit var itemSelectListener: (String) -> Unit?

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchCompleteViewHolder =
        SearchCompleteViewHolder(parent)

    override fun onBindViewHolder(holder: SearchCompleteViewHolder, position: Int) {
        with(items[position]) {
            holder.tvResult.text = this
            holder.itemView.setOnClickListener {
                itemSelectListener(this)
            }
        }
    }

    override fun getItemCount(): Int = items.size

    fun submitList(newItems: List<String>) {
        items = newItems
        notifyDataSetChanged()
    }

    inner class SearchCompleteViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context!!)
            .inflate(R.layout.item_search_complete, parent, false)
    ) {
        val tvResult: TextView = itemView.tv_result
    }
}