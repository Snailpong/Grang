package com.pong.grang.view.ui

import android.graphics.Typeface
import android.icu.text.UnicodeSet
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.text.Spannable
import android.text.style.BackgroundColorSpan
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_IDLE
import com.github.dnbn.submerge.api.parser.SRTParser
import com.github.dnbn.submerge.api.subtitle.srt.SRTLine
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.Format
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.MergingMediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.source.SingleSampleMediaSource
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Log
import com.google.android.exoplayer2.util.MimeTypes
import com.google.android.exoplayer2.util.Util
import com.pong.grang.R
import com.pong.grang.databinding.ActivityPlayerSubtitleBinding
import com.pong.grang.databinding.DialogAddSubtitleBinding
import com.pong.grang.helper.CenterSmoothScroller
import com.pong.grang.model.SubtitleModel
import com.pong.grang.view.adapter.SubtitleAdapter
import java.io.File


class PlayerSubtitleActivity : AppCompatActivity() {

    private lateinit var binding : ActivityPlayerSubtitleBinding
    private lateinit var mSubtitleAdapter : SubtitleAdapter
    private lateinit var subtitleList : ArrayList<SRTLine>
    private lateinit var videoUri : String
    private lateinit var subtitleUri : String
    private lateinit var scrollSubtitleRunnable : Runnable
    private lateinit var scrollSubtitleHandler : Handler

    private var player : SimpleExoPlayer? = null
    private var isSync : Boolean = false
    private var currentSrtLine : SRTLine? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerSubtitleBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        setUri()
        initSubtitleData()
        startPlayer()
        initRecyclerView()
        initSubtitleSync()
        initButtonListener()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.videoView.player = null
        player!!.release()
        player = null
        detachSubtitleSync()
    }

    private fun startPlayer() {
        player = SimpleExoPlayer.Builder(this).build()
        binding.videoView.player = player
        binding.controlViewSubtitle.setPlayer(player)
        playWithCaption()
    }

    private fun setUri() {
        videoUri = intent.getStringExtra("videoUri")!!
        subtitleUri = intent.getStringExtra("subtitleUri")!!
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
        return ProgressiveMediaSource.Factory(DefaultDataSourceFactory(this, userAgent))
            .createMediaSource(MediaItem.fromUri(uri))
//        return ProgressiveMediaSource.Factory(DefaultHttpDataSourceFactory(userAgent))
//            .createMediaSource(MediaItem.fromUri(uri))
    }

    private fun initSubtitleData() {
        val subtitleFile = File(subtitleUri)
        val subtitleSrtSub =  SRTParser().parse(subtitleFile)
        val subtitleSet = subtitleSrtSub.lines
        subtitleList = ArrayList(subtitleSet)
        subtitleList.sort()
    }

    private fun initRecyclerView() {
        mSubtitleAdapter = SubtitleAdapter(this, subtitleList) {
            srtLine -> player?.seekTo(srtLine.time.start)
            startSmoothScrollToPosition(srtLine.id)
            Toast.makeText(this, srtLine.id.toString(), Toast.LENGTH_SHORT).show()
        }
        binding.recyclerviewSubtitleList.run {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@PlayerSubtitleActivity)
            adapter = mSubtitleAdapter
        }
        val onTouchListener = object:RecyclerView.OnItemTouchListener {
            override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {}
            override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}

            override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                if(e.action == MotionEvent.ACTION_DOWN && isSync) {
                    detachSubtitleSync()
                }
                return false
            }
        }
        val onScrollListener = object: RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if(newState == SCROLL_STATE_IDLE) {
                    val view = binding.recyclerviewSubtitleList.layoutManager!!.findViewByPosition(currentSrtLine!!.id)
                    val textView = view!!.findViewById<TextView>(R.id.item_subtitle_subtitle)
                    textView.setTypeface(textView.typeface, Typeface.BOLD)
                }
            }
        }
        binding.recyclerviewSubtitleList.addOnItemTouchListener(onTouchListener)
        binding.recyclerviewSubtitleList.addOnScrollListener(onScrollListener)
    }

    private fun initSubtitleSync() {
        scrollSubtitleHandler = Handler()
        scrollSubtitleRunnable = object : Runnable {
            override fun run() {
                val subtitleDelay = 300
                if(player?.isPlaying == true) {
                    val currentPos = player!!.currentPosition

                    if (currentSrtLine == null || !(currentPos >= currentSrtLine!!.time.start - subtitleDelay
                        && currentPos <= currentSrtLine!!.time.end - subtitleDelay)) {
                        changeCurrentSrtLine(currentPos, subtitleDelay)
                    }
                }
                scrollSubtitleHandler.postDelayed(this, 300)
            }
        }
        attachSubtitleSync()
    }

    fun changeCurrentSrtLine(currentPos : Long, subtitleDelay : Int) {
        for (srtLine in subtitleList) {
            if (currentPos >= srtLine.time.start - subtitleDelay
                && currentPos <= srtLine.time.end - subtitleDelay
            ) {
                if(currentSrtLine != null) {
                    val prevView = binding.recyclerviewSubtitleList.layoutManager!!.findViewByPosition(currentSrtLine!!.id)
                    Log.d("w", currentSrtLine!!.id.toString())
                    val prevTextView = prevView!!.findViewById<TextView>(R.id.item_subtitle_subtitle)
                    prevTextView.setTypeface(null, Typeface.NORMAL)
                }
                currentSrtLine = srtLine
                Log.d("w", currentSrtLine!!.id.toString())
                startSmoothScrollToPosition(srtLine.id)


                break
            } else {
                if (currentPos < srtLine.time.end - subtitleDelay) {
                    break
                }
            }
        }
    }

    private fun attachSubtitleSync() {
        scrollSubtitleHandler.post(scrollSubtitleRunnable)
        binding.btnSyncSubtitle.text = "Syncing Subtitle"
        isSync = true
    }

    private fun detachSubtitleSync() {
        scrollSubtitleHandler.removeCallbacks(scrollSubtitleRunnable)
        binding.btnSyncSubtitle.text = "Not Syncing Subtitle"
        isSync = false
    }

    private fun startSmoothScrollToPosition(pos : Int) {
        val smoothScroller = CenterSmoothScroller(binding.recyclerviewSubtitleList.context)
        smoothScroller.targetPosition = pos
        binding.recyclerviewSubtitleList.layoutManager!!.startSmoothScroll(smoothScroller)
    }

    private fun initButtonListener() {
        binding.btnAddSubtitle.setOnClickListener {
            openAddSubtitleDialog()
        }
        binding.btnSyncSubtitle.setOnClickListener {
            if(!isSync) attachSubtitleSync()
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
//                mSubtitleAdapter.addItem(subtitleModel)
                mSubtitleAdapter.notifyDataSetChanged()
            })
            .setNegativeButton("No", null)
            .create()
        dialog.setContentView(binding.root)
        dialog.show()
    }
}