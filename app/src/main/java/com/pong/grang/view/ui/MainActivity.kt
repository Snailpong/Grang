package com.pong.grang.view.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.github.dnbn.submerge.api.parser.SMIConverter
import com.obsez.android.lib.filechooser.ChooserDialog
import com.pedro.library.AutoPermissions
import com.pedro.library.AutoPermissionsListener
import com.pong.grang.UriPathHelper
import com.pong.grang.databinding.ActivityMainBinding
import com.pong.grang.helper.PreferenceManager
import org.apache.commons.io.FilenameUtils


class MainActivity : AppCompatActivity(), AutoPermissionsListener {

    private lateinit var binding : ActivityMainBinding
    private val SELECT_MOVIE = 2
    private val SELECT_SUBTITLE = 3

    private lateinit var videoPath : String
    private lateinit var subtitlePath : String
    private lateinit var lastVideoPath : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        AutoPermissions.Companion.loadAllPermissions(this,101);
        binding.selectVideoMain.setOnClickListener(SelectVideoListener())

    }

    private fun getPath(videoUri: Uri): String {
        val realPath = UriPathHelper.getPath(this, videoUri)
        return realPath!!
    }

    private fun startPlayer() {
        val playerIntent = Intent(this, PlayerSubtitleActivity::class.java)
        playerIntent.putExtra("videoUri", videoPath)
        playerIntent.putExtra("subtitleUri", subtitlePath)
        startActivity(playerIntent)
    }

    inner class SelectVideoListener : View.OnClickListener {
        override fun onClick(v: View?) {
            lastVideoPath = PreferenceManager.getString(this@MainActivity, "lastVideoPath", Environment.getExternalStorageDirectory().absolutePath)!!
            ChooserDialog(this@MainActivity)
                .withFilterRegex(false, false, ".*\\.(wmv|flv|mkv|3gp|mp4)")
                .withStartFile(lastVideoPath)
//                .withResources(
//                    R.string.title_choose_video,
//                    R.string.title_choose,
//                    R.string.dialog_cancel
//                )
                .withChosenListener(onVideoSelected)
                .build()
                .show()
        }
    }

    private val onVideoSelected =
        ChooserDialog.Result { path, pathFile ->
            Toast.makeText(this@MainActivity, path, Toast.LENGTH_LONG).show()
            videoPath = path
            lastVideoPath = path
            PreferenceManager.setString(this@MainActivity, "lastVideoPath", path)
            ChooserDialog(this@MainActivity)
                .withFilterRegex(false, false, ".*\\.(smi|srt)")
                .withStartFile(path)
//                .withResources(
//                    R.string.title_choose_video,
//                    R.string.title_choose,
//                    R.string.dialog_cancel
//                )
                .withChosenListener(onSubtitleSelected)
                .build()
                .show()
        }
//
    private val onSubtitleSelected =
        ChooserDialog.Result { path, pathFile ->
            subtitlePath = path
            Toast.makeText(this@MainActivity, path, Toast.LENGTH_LONG).show()

            val extension = FilenameUtils.getExtension(subtitlePath)

            if(extension == "smi") {
                Log.d("www", "wwwwww")
                val file = SMIConverter.convertToFile(subtitlePath)
                Log.d("www", file)
            }
            Toast.makeText(this, videoPath, Toast.LENGTH_LONG).show()

            startPlayer()
        }


    override fun onDenied(requestCode: Int, permissions: Array<String>) {
        finish()
    }

    override fun onGranted(requestCode: Int, permissions: Array<String>) {
        TODO("Not yet implemented")
    }
}