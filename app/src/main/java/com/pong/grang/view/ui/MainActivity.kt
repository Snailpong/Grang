package com.pong.grang.view.ui

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.pedro.library.AutoPermissions
import com.pedro.library.AutoPermissionsListener
import com.pong.grang.R
import com.pong.grang.UriPathHelper
import com.pong.grang.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), AutoPermissionsListener {

    private lateinit var binding: ActivityMainBinding
    private val SELECT_MOVIE = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        AutoPermissions.Companion.loadAllPermissions(this,101);
        binding.selectVideoMain.setOnClickListener(SelectVideoListener())

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK && requestCode == SELECT_MOVIE) {
            val videoUri : Uri = data!!.data!!
            val videoPath : String = getPath(videoUri)
            Toast.makeText(this, videoPath, Toast.LENGTH_LONG).show()
            startPlayer(videoPath)
        }
    }

    private fun getPath(videoUri: Uri): String {
        val realPath = UriPathHelper.getPath(this, videoUri)
        return realPath!!
    }

    private fun startPlayer(video_path : String) {
        val playerIntent = Intent(this, PlayerSubtitleActivity::class.java)
        playerIntent.putExtra("videoUri", video_path)
        startActivity(playerIntent)
    }

    inner class SelectVideoListener : View.OnClickListener {
        override fun onClick(v: View?) {
            val videoIntent = Intent(Intent.ACTION_GET_CONTENT)
            videoIntent.setType("video/*")
            videoIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            try {
                startActivityForResult(videoIntent, SELECT_MOVIE)
            } catch(e : android.content.ActivityNotFoundException) {
                e.printStackTrace()
            }
        }
    }

    override fun onDenied(requestCode: Int, permissions: Array<String>) {
        finish()
    }

    override fun onGranted(requestCode: Int, permissions: Array<String>) {
        TODO("Not yet implemented")
    }
}