package com.pong.grang.view.adapter

import android.app.PendingIntent
import android.graphics.Bitmap
import androidx.annotation.Nullable
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.PlayerNotificationManager.BitmapCallback
import com.google.android.exoplayer2.ui.PlayerNotificationManager.MediaDescriptionAdapter


class DescriptionAdapter : MediaDescriptionAdapter {
    override fun getCurrentContentTitle(player: Player): String {
        val window = player.currentWindowIndex
        return "abc" // getTitle(window)
    }

    @Nullable
    override fun getCurrentContentText(player: Player): String? {
        val window = player.currentWindowIndex
        return "def" //BundleJUnitUtils.getDescription(window)
    }

    @Nullable
    override fun getCurrentLargeIcon(
        player: Player,
        callback: BitmapCallback
    ): Bitmap? {
//        val window = player.currentWindowIndex
//        val largeIcon: Bitmap = getLargeIcon(window)
//        if (largeIcon == null && getLargeIconUri(window) != null) {
//            // load bitmap async
//            loadBitmap(getLargeIconUri(window), callback)
//            return getPlaceholderBitmap()
//        }
//        return largeIcon
        return null
    }

    @Nullable
    override fun createCurrentContentIntent(player: Player): PendingIntent? {
        val window = player.currentWindowIndex
//        return createPendingIntent(window)
        return null
    }
}