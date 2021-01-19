package com.pong.grang.view.ui

import android.media.MediaPlayer
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.SurfaceHolder
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.pong.grang.R
import com.pong.grang.model.SubtitleModel
import com.pong.grang.view.adapter.SubtitleAdapter
import kotlinx.android.synthetic.main.activity_player_subtitle.*
import kotlinx.android.synthetic.main.dialog_add_subtitle.view.*

class PlayerSubtitleActivity : AppCompatActivity(), SurfaceHolder.Callback {

    private lateinit var mSubtitleAdapter: SubtitleAdapter
    private val mSubtitleItems: ArrayList<SubtitleModel> = ArrayList()
    private val videoURL: String = "http://techslides.com/demos/sample-videos/small.mp4"
    private lateinit var mVideoScreen: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player_subtitle)

        initSurface()
        initRecyclerView()
        initAddButton()
    }

    private fun initSurface() {
//        var mediaPlayer: MediaPlayer? = MediaPlayer.create(this, videoUri)
        screen_player.holder.addCallback(this)
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

    override fun surfaceCreated(holder: SurfaceHolder) {
        val videoUri: Uri = Uri.parse(videoURL)
        mVideoScreen = MediaPlayer()
        mVideoScreen.setDataSource(videoURL)
        mVideoScreen.setDisplay(holder)
        mVideoScreen.prepare()
        mVideoScreen.start()
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        //TODO("Not yet implemented")
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        mVideoScreen.release()
    }
}