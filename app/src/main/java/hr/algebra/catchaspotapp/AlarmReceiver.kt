package hr.algebra.catchaspotapp

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.provider.Settings

private const val DURATION = 1000L
private const val NOTIFICATION_ID = 1
private const val CHANNEL_ID = "channel_id"
private val CHANNEL_TITLE : CharSequence = "channel_title"

@Suppress("DEPRECATION")
class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        playRingtone(context, DURATION)
        sendNotification(context)
    }


    private fun playRingtone(context: Context, duration: Long) {
        val mediaPlayer = MediaPlayer.create(context, Settings.System.DEFAULT_RINGTONE_URI)
        mediaPlayer.start()
        Handler(Looper.getMainLooper()).postDelayed({mediaPlayer.stop()}, duration)
    }

    private fun sendNotification(context: Context) {

        val intent = Intent(context, ParkedActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            NOTIFICATION_ID,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val builder: Notification.Builder
        builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_TITLE,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
            Notification.Builder(context, channel.id)

        }else{

            Notification.Builder(context)

        }

        builder
            .setContentTitle("Parking spot alarm")
            .setContentText("You've been parked for one hour!")
            .setSmallIcon(R.drawable.placeholder).setAutoCancel(true)
            .setContentIntent(pendingIntent)

        notificationManager.notify(NOTIFICATION_ID, builder.build())
    }

}