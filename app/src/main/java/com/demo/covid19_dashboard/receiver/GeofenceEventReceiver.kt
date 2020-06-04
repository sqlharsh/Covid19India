package com.demo.covid19_dashboard.receiver

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.os.Build
import android.os.Message
import android.text.TextUtils
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.demo.covid19_dashboard.MainActivity
import com.demo.covid19_dashboard.R
import com.demo.covid19_dashboard.utils.Constants
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofenceStatusCodes
import com.google.android.gms.location.GeofencingEvent
import kotlin.random.Random

class GeofenceEventReceiver : BroadcastReceiver() {

    private val TAG = GeofenceEventReceiver::class.java.simpleName
    override fun onReceive(context: Context, intent: Intent) {
        Log.e(TAG,"geofence event received!")
        val geofencingEvent = GeofencingEvent.fromIntent(intent)
        if (geofencingEvent.hasError()) {
            Log.e(TAG, "error code $geofencingEvent.errorCode" )
            return
        }

        // Get the transition type.
        val geofenceTransition = geofencingEvent.geofenceTransition
        when(geofenceTransition){
            Geofence.GEOFENCE_TRANSITION_ENTER -> {
                Log.e(TAG,"entered in geofence !!")
                sendNotification(context,context.getString(R.string.back_to_home_notification_message))
            }
            Geofence.GEOFENCE_TRANSITION_EXIT -> { 
                Log.e(TAG,"exiting from geofence !!")
                sendNotification(context,context.getString(R.string.going_out_notiification_message))
            }
        }
    }

    private fun sendNotification(context: Context,message: String){
        val intent = Intent(context,MainActivity::class.java)
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        val pendingIntent = PendingIntent.getActivity(
            context, 0 /* Request code */, intent, PendingIntent.FLAG_ONE_SHOT)

        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder =
            NotificationCompat.Builder(context, Constants.PUSH_NOTIFICATION_CHANNEL_ID)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            notificationBuilder.setSmallIcon(R.drawable.ic_notification)
        } else {
            notificationBuilder.setSmallIcon(R.drawable.ic_notification)
        }

        notificationBuilder.setLargeIcon(
            BitmapFactory.decodeResource(
                context.getResources(),
                R.mipmap.ic_launcher
            )
        )

        notificationBuilder.setAutoCancel(true)
        notificationBuilder.setDefaults(NotificationCompat.DEFAULT_ALL)
        notificationBuilder.setSound(defaultSoundUri)
        notificationBuilder.setPriority(NotificationCompat.PRIORITY_MAX)
        notificationBuilder.setContentIntent(pendingIntent)
        notificationBuilder.setContentTitle(context.getString(R.string.app_name))
        notificationBuilder.setContentText(message)

        val bigTextStyle = NotificationCompat.BigTextStyle()

        if (!TextUtils.isEmpty(message))
            bigTextStyle.bigText(message)

        notificationBuilder.setStyle(bigTextStyle)
        val notificationId = 1001

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the NotificationChannel
            val name = context.getString(R.string.local_notification)
            val importance = NotificationManager.IMPORTANCE_HIGH
            val mChannel =
                NotificationChannel(Constants.PUSH_NOTIFICATION_CHANNEL_ID, name, importance)

            mChannel.setShowBadge(true);
            notificationManager.createNotificationChannel(mChannel)
        }

        notificationManager.notify(notificationId, notificationBuilder.build())
    }
}
