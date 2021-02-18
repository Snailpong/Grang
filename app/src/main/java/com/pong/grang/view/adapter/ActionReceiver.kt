package com.pong.grang.view.adapter

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import com.pong.grang.R

class ActionReceiver : PlayerNotificationManager.CustomActionReceiver{
    var actions = mutableListOf<String>("prev", "back", "play", "pause", "for", "next")

    override fun getCustomActions(player: Player): MutableList<String> {
        return actions
    }

    override fun createCustomActions(
        context: Context,
        instanceId: Int
    ): MutableMap<String, NotificationCompat.Action> {
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
        val prevAction = Intent()
        prevAction.putExtra("action", actions[0])
        val backwardAction = Intent()
        backwardAction.putExtra("action", actions[1])
        val playAction = Intent()
        playAction.putExtra("action", actions[2])
        val pauseAction = Intent()
        pauseAction.putExtra("action", actions[3])
        val forwardAction = Intent()
        forwardAction.putExtra("action", actions[4])
        val nextAction = Intent()
        nextAction.putExtra("action", actions[5])
        return mutableMapOf(
            Pair(actions[0], NotificationCompat.Action(R.drawable.exo_icon_previous, actions[0],
                PendingIntent.getBroadcast(context, 0, Intent(prevAction).setPackage(context?.packageName), PendingIntent.FLAG_CANCEL_CURRENT))),
            Pair(actions[1], NotificationCompat.Action(R.drawable.exo_icon_rewind, actions[1],
                PendingIntent.getBroadcast(context, 0, Intent(backwardAction).setPackage(context?.packageName), PendingIntent.FLAG_CANCEL_CURRENT))),
            Pair(actions[2], NotificationCompat.Action(R.drawable.exo_icon_play, actions[2],
                PendingIntent.getBroadcast(context, 0, Intent(playAction).setPackage(context?.packageName), PendingIntent.FLAG_CANCEL_CURRENT))),
            Pair(actions[3], NotificationCompat.Action(R.drawable.exo_icon_pause, actions[3],
                PendingIntent.getBroadcast(context, 0, Intent(pauseAction).setPackage(context?.packageName), PendingIntent.FLAG_CANCEL_CURRENT))),
            Pair(actions[4], NotificationCompat.Action(R.drawable.exo_icon_fastforward, actions[4],
                PendingIntent.getBroadcast(context, 0, Intent(forwardAction).setPackage(context?.packageName), PendingIntent.FLAG_CANCEL_CURRENT))),
            Pair(actions[5], NotificationCompat.Action(R.drawable.exo_icon_next, actions[5],
                PendingIntent.getBroadcast(context, 0, Intent(nextAction).setPackage(context?.packageName), PendingIntent.FLAG_CANCEL_CURRENT)))
        )
    }

    override fun onCustomAction(player: Player, action: String, intent: Intent) {
        Log.d("wwww", action)
        Log.d("www", intent.getStringExtra("action")!!)
    }

}
