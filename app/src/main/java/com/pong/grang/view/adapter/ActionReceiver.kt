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
        val action1: NotificationCompat.Action =
            NotificationCompat.Action(
                R.drawable.exo_icon_shuffle_on,
                "fav",
                pendingIntent
            )
        val actionMap: MutableMap<String, NotificationCompat.Action> =
            HashMap()
        actionMap["fav"] = action1
        return actionMap

        Log.d("wwww", "createeeee")
//        val action =
//            NotificationCompat.Action(
//                context.resources
//                    .getIdentifier("music_clear", "drawable", context.packageName),
//                "closeBar",
//                null
//            )
//        val actionMap: MutableMap<String, NotificationCompat.Action> =
//            HashMap()
//        actionMap["closeBar"] = action
//        return actionMap
//        val prevAction = Intent()
//        prevAction.putExtra("action", actions[0])
//        val backwardAction = Intent()
//        backwardAction.putExtra("action", actions[1])
//        val playAction = Intent()
//        playAction.putExtra("action", actions[2])
//        val pauseAction = Intent()
//        pauseAction.putExtra("action", actions[3])
//        val forwardAction = Intent()
//        forwardAction.putExtra("action", actions[4])
//        val nextAction = Intent()
//        nextAction.putExtra("action", actions[5])
//        return mutableMapOf(
//            Pair(actions[0], NotificationCompat.Action(R.drawable.exo_icon_next, actions[0],
//                PendingIntent.getBroadcast(context, 0, Intent(prevAction).setPackage(context?.packageName), PendingIntent.FLAG_CANCEL_CURRENT))),
//            Pair(actions[1], NotificationCompat.Action(R.drawable.exo_icon_next, actions[1],
//                PendingIntent.getBroadcast(context, 0, Intent(backwardAction).setPackage(context?.packageName), PendingIntent.FLAG_CANCEL_CURRENT))),
//            Pair(actions[2], NotificationCompat.Action(R.drawable.exo_icon_play, actions[2],
//                PendingIntent.getBroadcast(context, 0, Intent(playAction).setPackage(context?.packageName), PendingIntent.FLAG_CANCEL_CURRENT))),
//            Pair(actions[3], NotificationCompat.Action(R.drawable.exo_icon_pause, actions[3],
//                PendingIntent.getBroadcast(context, 0, Intent(pauseAction).setPackage(context?.packageName), PendingIntent.FLAG_CANCEL_CURRENT))),
//            Pair(actions[4], NotificationCompat.Action(R.drawable.exo_icon_fastforward, actions[4],
//                PendingIntent.getBroadcast(context, 0, Intent(forwardAction).setPackage(context?.packageName), PendingIntent.FLAG_CANCEL_CURRENT))),
//            Pair(actions[5], NotificationCompat.Action(R.drawable.exo_icon_next, actions[5],
//                PendingIntent.getBroadcast(context, 0, Intent(nextAction).setPackage(context?.packageName), PendingIntent.FLAG_CANCEL_CURRENT)))
//        )
    }

    override fun onCustomAction(player: Player, action: String, intent: Intent) {
        Log.d("wwww", action)
        if(action.equals("fav")) {
            if (player.isPlaying) player.pause()
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
