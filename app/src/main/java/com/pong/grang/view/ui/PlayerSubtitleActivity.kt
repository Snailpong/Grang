package com.pong.grang.view.ui

import android.R
import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.SurfaceHolder
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.pong.grang.databinding.ActivityPlayerSubtitleBinding
import com.pong.grang.databinding.DialogAddSubtitleBinding
import com.pong.grang.model.SubtitleModel
import com.pong.grang.view.adapter.SubtitleAdapter


class PlayerSubtitleActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlayerSubtitleBinding
    private lateinit var mSubtitleAdapter: SubtitleAdapter
    private val mSubtitleItems: ArrayList<SubtitleModel> = ArrayList()
    private lateinit var videoURL: String
    private lateinit var player: SimpleExoPlayer
    private lateinit var mVideoScreen: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerSubtitleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        videoURL = getIntent().getStringExtra("videoUri")!!

        initPlayer()
        initRecyclerView()
        initAddButton()
    }

    private fun initPlayer() {
        player = SimpleExoPlayer.Builder(this).build()
        binding.videoView.setPlayer(player)
        val mediaItem: MediaItem = MediaItem.fromUri(videoURL)
        player.setMediaItem(mediaItem)
        player.setPlayWhenReady(true);
        player.prepare();
    }

    private fun initRecyclerView() {
        mSubtitleAdapter = SubtitleAdapter(this, mSubtitleItems)

        mSubtitleItems.run {
            add(SubtitleModel(1, 0, 60, "자막1"))
            add(SubtitleModel(2, 60, 90, "자막2"))
            add(SubtitleModel(3, 90, 120, "자막3"))
            add(SubtitleModel(4, 120, 150, "자막4"))
        }

        binding.recyclerviewSubtitleList.run {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@PlayerSubtitleActivity)
            adapter = mSubtitleAdapter
        }
    }

    private fun initAddButton() {
        binding.btnAddSubtitle.setOnClickListener {
            openAddSubtitleDialog()
        }
    }

    private fun openAddSubtitleDialog() {
        val dialogView = DialogAddSubtitleBinding.inflate(LayoutInflater.from(this))
        val dialog = AlertDialog.Builder(this)
            .setTitle("add")
            .setPositiveButton("Ok", { dialogInterface, _ ->
                val idx = dialogView.idxDialogAddSubtitle.text.toString().toInt()
                val startTime = dialogView.startTimeDialogAddSubtitle.text.toString().toLong()
                val endTime = dialogView.endTimeDialogAddSubtitle.text.toString().toLong()
                val text = dialogView.textDialogAddSubtitle.text.toString()

                val subtitleModel = SubtitleModel(idx, startTime, endTime, text)
                mSubtitleAdapter.addItem(subtitleModel)
                mSubtitleAdapter.notifyDataSetChanged()
            })
            .setNegativeButton("No", null)
            .create()
        dialog.setContentView(binding.root)
        dialog.show()
    }
}