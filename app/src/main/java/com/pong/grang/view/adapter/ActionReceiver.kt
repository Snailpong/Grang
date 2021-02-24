package com.pong.grang.view.adapter

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import com.pong.grang.R
import com.pong.grang.model.TTSModel


class ActionReceiver(private val tts: TTSModel) : PlayerNotificationManager.CustomActionReceiver{

    private lateinit var action1 : NotificationCompat.Action

    override fun getCustomActions(player: Player): MutableList<String> {
        val customActions: MutableList<String> = ArrayList()
        customActions.add("fav")
        return customActions
    }

    override fun createCustomActions(
        context: Context,
        instanceId: Int
    ): MutableMap<String, NotificationCompat.Action> {
        val intent: Intent = Intent("fav").setPackage(context.packageName)
        val pendingIntent = PendingIntent.getBroadcast(
            context, instanceId, intent, PendingIntent.FLAG_CANCEL_CURRENT
        )
        action1 =
            NotificationCompat.Action(
                R.drawable.exo_icon_play,
                "fav",
                pendingIntent
            )
        val actionMap: MutableMap<String, NotificationCompat.Action> =
            HashMap()
        actionMap["fav"] = action1
        return actionMap
    }

    override fun onCustomAction(player: Player, action: String, intent: Intent) {
        if(action == "fav") {
            if (player.isPlaying) {
                player.pause()
                action1.icon = R.drawable.exo_icon_pause
            }
            else {
                if (tts.ttsState == 1) {
                    tts.textToSpeech.stop()
                    tts.ttsState = -1
                }
                else player.play()
            }
        }
    }

}
