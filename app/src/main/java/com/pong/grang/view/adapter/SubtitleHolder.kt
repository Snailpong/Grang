package com.pong.grang.view.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.pong.grang.model.SubtitleModel
import kotlinx.android.synthetic.main.item_subtitle.view.*

class SubtitleHolder(view: View): RecyclerView.ViewHolder(view) {
    val startTime = view.item_start_time_subtitle
    val subtitle = view.item_subtitle_subtitle

    fun bind(subtitleModel: SubtitleModel) {
        startTime.text = subtitleModel.startTime.toString()
        subtitle.text = subtitleModel.text
    }
}