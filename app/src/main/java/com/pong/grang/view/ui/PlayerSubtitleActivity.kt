package com.pong.grang.view.ui

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.dnbn.submerge.api.parser.SRTParser
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.*
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.*
import com.google.android.exoplayer2.util.MimeTypes
import com.google.android.exoplayer2.util.Util
import com.pong.grang.databinding.ActivityPlayerSubtitleBinding
import com.pong.grang.databinding.DialogAddSubtitleBinding
import com.pong.grang.model.SubtitleModel
import com.pong.grang.view.adapter.SubtitleAdapter
import java.io.File


class PlayerSubtitleActivity : AppCompatActivity() {

    private lateinit var binding : ActivityPlayerSubtitleBinding
    private lateinit var mSubtitleAdapter : SubtitleAdapter
    private val mSubtitleItems : ArrayList<SubtitleModel> = ArrayList()
    private lateinit var videoUri : String
    private lateinit var subtitleUri : String
    private lateinit var playerView : PlayerView

    var player : SimpleExoPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerSubtitleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        videoUri = intent.getStringExtra("videoUri")!!
        subtitleUri = intent.getStringExtra("subtitleUri")!!

        videoUri = "https://www.learningcontainer.com/wp-content/uploads/2020/05/sample-mp4-file.mp4"
//        subtitleUri = "https://raw.githubusercontent.com/andreyvit/subtitle-tools/master/sample.srt"

        playerView = binding.videoView
        initSubtitleData()
        initRecyclerView()
        initAddButton()
    }

    override fun onStart() {
        super.onStart()
        player = SimpleExoPlayer.Builder(this).build()
        playerView.player = player
        playWithCaption()
    }

    override fun onStop() {
        super.onStop()
        playerView.player = null
        player!!.release()
        player = null
    }

    private fun playWithCaption() {
        val dataSourceFactory = DefaultDataSourceFactory(this,
            Util.getUserAgent(this, "exo-demo"))
        val contentMediaSource = buildMediaSource(videoUri)
        val subtitleSource = SingleSampleMediaSource(Uri.parse(subtitleUri), dataSourceFactory,
            Format.createTextSampleFormat(null, MimeTypes.APPLICATION_SUBRIP, Format.NO_VALUE, "en"),
            C.TIME_UNSET)

//        val subtitle : MediaItem.Subtitle = MediaItem.Subtitle(Uri.parse(subtitleUri), MimeTypes.APPLICATION_SUBRIP, "en")
//        val subtitleSource2 = SingleSampleMediaSource.Factory(dataSourceFactory).createMediaSource(subtitle, C.TIME_UNSET)
//        val mediaItem : MediaItem = MediaItem.Builder().setUri(videoUri).setSubtitles(Lists.newArrayList(subtitle)).build()
//        player!!.setMediaItem(mediaItem)

        val mediaSource = MergingMediaSource(contentMediaSource!!, subtitleSource)
        player!!.setMediaSource(mediaSource)
        player!!.prepare()
        player!!.playWhenReady = true
    }

    private fun buildMediaSource(uri: String): MediaSource? {
        val userAgent: String = Util.getUserAgent(this, "blackJin")
        return ProgressiveMediaSource.Factory(DefaultHttpDataSourceFactory(userAgent))
            .createMediaSource(MediaItem.fromUri(uri))
    }

    private fun initSubtitleData() {
        val subtitleFile = File(subtitleUri)
        val subtitleSrtSub =  SRTParser().parse(subtitleFile)
        val subtitleList = subtitleSrtSub.lines
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