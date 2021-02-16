package com.hakansarac.projectmngr.java

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.hakansarac.projectmngr.R
import com.hakansarac.projectmngr.activities.MainActivity
import com.hakansarac.projectmngr.activities.SignInActivity
import com.hakansarac.projectmngr.firebase.FirestoreClass
import com.hakansarac.projectmngr.utils.Constants

class MyFirebaseMessagingService : FirebaseMessagingService() {

    companion object{
        private const val TAG = "MyFirebaseMsgService"
    }

    override fun onMessageReceived(p0: RemoteMessage) {
        super.onMessageReceived(p0)
        Log.d(TAG,"FROM: ${p0.from}")

        p0.data.isNotEmpty().let{
            Log.d(TAG,"Message data Payload: ${p0.data}")

            val title = p0.data[Constants.FCM_KEY_TITLE]!!
            val message = p0.data[Constants.FCM_KEY_MESSAGE]!!
            sendNotification(title,message)
        }

        p0.notification?.let{
            Log.d(TAG,"Message Notification Body: ${it.body}")
        }
    }

    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
        Log.e(TAG,"Refreshed token: $p0")
        sendRegistrationToServer(p0)
    }

    private fun sendRegistrationToServer(token: String?){
        //TODO: implement
    }

    private fun sendNotification(title: String, message: String){
        val intent = if(FirestoreClass().getCurrentUserId().isNotEmpty()) {
            Intent(this, MainActivity::class.java)
        }else{
            Intent(this, SignInActivity::class.java)
        }
        intent.addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_CLEAR_TASK or
                        Intent.FLAG_ACTIVITY_CLEAR_TOP
        )
        val pendingIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_ONE_SHOT) //Flag indicating that this PendingIntent can be used once.
        val channelId = this.resources.getString(R.string.default_notification_channel_id)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this,channelId).setSmallIcon(R.drawable.ic_stat_ic_notification)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel = NotificationChannel(channelId,"Channel ProjectMngr Title",NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }
        notificationManager.notify(0,notificationBuilder.build())
    }
}