package com.pong.grang.view.ui

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.database.DatabaseProvider
import com.google.android.exoplayer2.database.ExoDatabaseProvider
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.extractor.ExtractorsFactory
import com.google.android.exoplayer2.source.*
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelector
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.*
import com.google.android.exoplayer2.upstream.cache.CacheDataSourceFactory
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import com.google.android.exoplayer2.util.MimeTypes
import com.google.android.exoplayer2.util.Util
import com.pong.grang.databinding.ActivityPlayerSubtitleBinding
import com.pong.grang.databinding.DialogAddSubtitleBinding
import com.pong.grang.model.SubtitleModel
import com.pong.grang.view.adapter.SubtitleAdapter


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

        videoUri = getIntent().getStringExtra("videoUri")!!
        subtitleUri = getIntent().getStringExtra("subtitleUri")!!

        videoUri = "https://www.learningcontainer.com/wp-content/uploads/2020/05/sample-mp4-file.mp4"
        subtitleUri = "https://raw.githubusercontent.com/andreyvit/subtitle-tools/master/sample.srt"

        playerView = binding.videoView

        initRecyclerView()
        initAddButton()
    }

    override fun onStart() {
        super.onStart()
        player = ExoPlayerFactory.newSimpleInstance(this,
            DefaultTrackSelector())
        playerView.setPlayer(player)
        playWithCaption()
    }

    override fun onStop() {
        super.onStop()
        playerView.setPlayer(null)
        player!!.release()
        player = null
    }

    private fun playWithCaption() {
        val dataSourceFactory = DefaultDataSourceFactory(this,
            Util.getUserAgent(this, "exo-demo"))
        val contentMediaSource = buildMediaSource(Uri.parse(videoUri))
        //Add subtitles
        val subtitleSource = SingleSampleMediaSource(Uri.parse(subtitleUri), dataSourceFactory,
            Format.createTextSampleFormat(null, MimeTypes.APPLICATION_SUBRIP, Format.NO_VALUE, "en"),
            C.TIME_UNSET)
        val mediaSource = MergingMediaSource(contentMediaSource!!, subtitleSource)
        // Prepare the player with the source.
        //player.seekTo(contentPosition);
        player!!.prepare(mediaSource)
        player!!.setPlayWhenReady(true)
    }


//    private fun buildMediaSource(parse:Uri):MediaSource {
//        val dataSourceFactory = DefaultDataSourceFactory(this,
//            Util.getUserAgent(this, "exo-demo"))
//        mediaSource = ExtractorMediaSource(parse, dataSourceFactory, DefaultExtractorsFactory(), Handler(), null)
//        return mediaSource
//    }


    private fun buildMediaSource(uri: Uri): MediaSource? {
        val userAgent: String = Util.getUserAgent(this, "blackJin")
        return ExtractorMediaSource.Factory(DefaultHttpDataSourceFactory(userAgent))
            .createMediaSource(uri)
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