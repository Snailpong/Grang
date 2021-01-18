package com.pong.grang.view.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.pong.grang.R
import com.pong.grang.model.SubtitleModel
import com.pong.grang.view.adapter.SubtitleAdapter
import kotlinx.android.synthetic.main.activity_player_subtitle.*

class PlayerSubtitleActivity : AppCompatActivity() {

    private lateinit var mSubtitleAdapter: SubtitleAdapter
    private val mSubtitleItems: ArrayList<SubtitleModel> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player_subtitle)

        mSubtitleItems.run {
            add(SubtitleModel(1, 0, 60, "자막1"))
            add(SubtitleModel(2, 0, 60, "자막2"))
            add(SubtitleModel(3, 0, 60, "자막3"))
            add(SubtitleModel(4, 0, 60, "자막4"))
        }

        initRecyclerView()
    }

    private fun initRecyclerView() {
        mSubtitleAdapter = SubtitleAdapter(mSubtitleItems)

        recyclerview_subtitle_list.run {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@PlayerSubtitleActivity)
            adapter = mSubtitleAdapter
        }
    }
}