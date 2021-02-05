package com.pong.grang.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.github.dnbn.submerge.api.subtitle.srt.SRTLine
import com.pong.grang.databinding.ItemSubtitleBinding
import com.pong.grang.model.SubtitleModel

class SubtitleAdapter(private val context : Context, val subtitleLines: ArrayList<SRTLine>): RecyclerView.Adapter<SubtitleAdapter.SubtitleHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubtitleHolder {
        val binding = ItemSubtitleBinding.inflate(LayoutInflater.from(context), parent, false)

        return SubtitleHolder(binding)
    }

    fun addItem(srtLine: SRTLine) {
        subtitleLines.add(srtLine)
    }

    override fun getItemCount(): Int {
        return subtitleLines.size
    }

    override fun onBindViewHolder(holder: SubtitleHolder, position: Int) {
        holder.onBind(subtitleLines[position])
    }

    class SubtitleHolder(val binding : ItemSubtitleBinding): RecyclerView.ViewHolder(binding.root) {
        fun onBind(data : SRTLine) {
            binding.srtLine = data
        }

    }
}