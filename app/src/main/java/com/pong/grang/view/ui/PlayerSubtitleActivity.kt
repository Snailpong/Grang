package com.pong.grang.view.ui

import android.app.NotificationChannel
import android.app.NotificationManager
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.view.LayoutInflater
import android.view.MotionEvent
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
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Log
import com.google.android.exoplayer2.util.MimeTypes
import com.google.android.exoplayer2.util.Util
import com.pong.grang.R
import com.pong.grang.databinding.ActivityPlayerSubtitleBinding
import com.pong.grang.databinding.DialogAddSubtitleBinding
import com.pong.grang.helper.CenterSmoothScroller
import com.pong.grang.model.SubtitleModel
import com.pong.grang.view.adapter.ActionReceiver
import com.pong.grang.view.adapter.DescriptionAdapter
import com.pong.grang.view.adapter.SubtitleAdapter
import java.io.File
import java.util.*
import kotlin.collections.ArrayList


class PlayerSubtitleActivity : AppCompatActivity() {

    private lateinit var binding : ActivityPlayerSubtitleBinding
    private lateinit var mSubtitleAdapter : SubtitleAdapter
    private lateinit var subtitleList : ArrayList<SRTLine>
    private lateinit var videoUri : String
    private lateinit var subtitleUri : String
    private lateinit var scrollSubtitleRunnable : Runnable
    private lateinit var scrollSubtitleHandler : Handler
    private lateinit var playerNotificationMangager : PlayerNotificationManager
    private lateinit var textToSpeech : TextToSpeech

    private var player : SimpleExoPlayer? = null
    private var isSync : Boolean = false
    private var isTTS : Boolean = false
    private var ttsState : Int = -1
    private var currentSrtLine : SRTLine? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerSubtitleBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        setUri()
        initSubtitleData()
        initPlayer()
        initPlayerNotification()
        initTextToSpeech()
        initRecyclerView()
        initSubtitleSync()
        initButtonListener()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.videoView.player = null
        playerNotificationMangager.setPlayer(null)
        player!!.release()
        player = null
        textToSpeech.stop()
        textToSpeech.shutdown()
        detachSubtitleSync()
    }

    private fun initPlayer() {
        player = SimpleExoPlayer.Builder(this).build()
        binding.videoView.player = player
        binding.controlViewSubtitle.player = player

        playWithCaption()
    }

    private fun initPlayerNotification() {
        val name = getString(R.string.channel_name)
        val descriptionText = getString(R.string.channel_description)
        val channelId = getString(R.string.channel_id)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_LOW
            val mChannel = NotificationChannel(channelId, name, importance)
            mChannel.description = descriptionText
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(mChannel)
        }

        playerNotificationMangager = PlayerNotificationManager(this, channelId, 2108, DescriptionAdapter(videoUri), ActionReceiver())
        playerNotificationMangager.setPlayer(player)
    }

    private fun initTextToSpeech() {
        textToSpeech = TextToSpeech(this, TextToSpeech.OnInitListener {
            if (it === TextToSpeech.SUCCESS) {
                //사용할 언어를 설정
                player!!.playWhenReady = true
                val result = textToSpeech.setLanguage(Locale.KOREA)
                //언어 데이터가 없거나 혹은 언어가 지원하지 않으면...
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Toast.makeText(this@PlayerSubtitleActivity, "이 언어는 지원하지 않습니다.", Toast.LENGTH_SHORT).show()
                } else {
                    val utteranceProgressListener = object : UtteranceProgressListener() {
                        override fun onError(p0: String?) {}
                        override fun onStart(p0: String?) {}

                        override fun onDone(p0: String?) {
                            ttsState = 2
                            Log.d("w", "eeeee")
                        }
                    }

                    textToSpeech.setOnUtteranceProgressListener(utteranceProgressListener)
//                    btnEnter.setEnabled(true)
//                    textToSpeech.setPitch(0.7f)
//                    textToSpeech.setSpeechRate(1.2f)
                }
            }
        })
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
//        player!!.playWhenReady = true
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
                if(newState == SCROLL_STATE_IDLE && isSync) {
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
                if(player != null) {
                    val currentPos = player!!.currentPosition
                    when(ttsState) {
                        -1 -> changeCurrentSrtLine (currentPos, subtitleDelay)
                        0 -> {
                            if (currentSrtLine == null || !(currentPos >= currentSrtLine!!.time.start - subtitleDelay
                                        && currentPos <= currentSrtLine!!.time.end - subtitleDelay)) {
                                if(isTTS) playCurrentSubtitleTTS()
                                else changeCurrentSrtLine(currentPos, subtitleDelay)
                            }
                        }
                        2 -> {
                            player?.play()
                            changeCurrentSrtLine (currentPos, subtitleDelay)
                        }
                    }
                }
                scrollSubtitleHandler.postDelayed(this, 300)
            }
        }
        attachSubtitleSync()
    }

    private fun playCurrentSubtitleTTS() {
        player?.pause()
        ttsState = 1
        textToSpeech.speak(currentSrtLine?.printLines(currentSrtLine!!.textLines), TextToSpeech.QUEUE_FLUSH, null, TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID)
    }

    private fun changeCurrentSrtLine(currentPos : Long, subtitleDelay : Int) {
        var flag = true
        for (srtLine in subtitleList) {
            if (currentPos >= srtLine.time.start - subtitleDelay
                && currentPos <= srtLine.time.end - subtitleDelay
            ) {
                flag = false
                ttsState = 0
                if(currentSrtLine != null) {
                    typeFaceToNormal(currentSrtLine!!.id)
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
        if(flag)
            ttsState = -1
    }

    private fun typeFaceToNormal(id : Int?) {
        id ?: return

        val prevView = binding.recyclerviewSubtitleList.layoutManager?.findViewByPosition(id)
        Log.d("w", id.toString())
        val prevTextView = prevView?.findViewById<TextView>(R.id.item_subtitle_subtitle)
        prevTextView?.setTypeface(null, Typeface.NORMAL)
    }

    private fun attachSubtitleSync() {
        scrollSubtitleHandler.post(scrollSubtitleRunnable)
        binding.btnSyncSubtitle.text = "Sync on"
        isSync = true
    }

    private fun detachSubtitleSync() {
        scrollSubtitleHandler.removeCallbacks(scrollSubtitleRunnable)
        binding.btnSyncSubtitle.text = "Sync off"
        typeFaceToNormal(currentSrtLine?.id)
        currentSrtLine = null
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
        binding.btnTtsEnable.setOnClickListener {
            if(!isTTS) {
                isTTS = true
                binding.btnTtsEnable.text = "TTS on"
            } else {
                isTTS = false
                binding.btnTtsEnable.text = "TTS off"
            }
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
