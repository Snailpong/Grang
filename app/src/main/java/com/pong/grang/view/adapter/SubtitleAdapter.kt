package com.pong.grang.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pong.grang.R
import com.pong.grang.model.SubtitleModel

class SubtitleAdapter(val subtitleItems: ArrayList<SubtitleModel>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_subtitle, parent, false)
        val viewHolder = SubtitleHolder(view)

        return viewHolder
    }

    fun addItem(subtitleModel: SubtitleModel) {
        subtitleItems.add(subtitleModel)
    }

    override fun getItemCount(): Int {
        return subtitleItems.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val subtitleModel = subtitleItems[position]
        val subtitleHolder = holder as SubtitleHolder
        subtitleHolder.bind(subtitleModel)
    }
}