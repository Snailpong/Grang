package com.pong.grang.view.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.pong.grang.R
import com.pong.grang.model.SubtitleModel
import com.pong.grang.view.adapter.SubtitleAdapter
import kotlinx.android.synthetic.main.activity_player_subtitle.*
import kotlinx.android.synthetic.main.dialog_add_subtitle.view.*

class PlayerSubtitleActivity : AppCompatActivity() {

    private lateinit var mSubtitleAdapter: SubtitleAdapter
    private val mSubtitleItems: ArrayList<SubtitleModel> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player_subtitle)

        initRecyclerView()
        initAddButton()
    }

    private fun initRecyclerView() {
        mSubtitleAdapter = SubtitleAdapter(mSubtitleItems)

        mSubtitleItems.run {
            add(SubtitleModel(1, 0, 60, "자막1"))
            add(SubtitleModel(2, 60, 90, "자막2"))
            add(SubtitleModel(3, 90, 120, "자막3"))
            add(SubtitleModel(4, 120, 150, "자막4"))
        }

        recyclerview_subtitle_list.run {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@PlayerSubtitleActivity)
            adapter = mSubtitleAdapter
        }
    }

    private fun initAddButton() {
        btn_add_subtitle.setOnClickListener {
            openAddSubtitleDialog()
        }
    }

    private fun openAddSubtitleDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_subtitle, null)
        val dialog = AlertDialog.Builder(this)
            .setTitle("add")
            .setView(dialogView)
            .setPositiveButton("Ok", { dialogInterface, i ->
                val idx = dialogView.idx_dialog_add_subtitle.text.toString().toInt()
                val startTime = dialogView.start_time_dialog_add_subtitle.text.toString().toLong()
                val endTime = dialogView.end_time_dialog_add_subtitle.text.toString().toLong()
                val text = dialogView.text_dialog_add_subtitle.text.toString()

                val subtitleModel = SubtitleModel(idx, startTime, endTime, text)
                mSubtitleAdapter.addItem(subtitleModel)
                mSubtitleAdapter.notifyDataSetChanged()
            })
            .setNegativeButton("No", null)
            .create()
        dialog.show()
    }
}