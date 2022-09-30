package com.ujjman.course.courseswap.notification

import android.R
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.ujjman.course.courseswap.MainActivity
import com.ujjman.course.courseswap.SwapDetails
import java.util.*


class YourService : Service() {
    var counter = 0
    var previousCount=0
    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) startMyOwnForeground() else startForeground(
            1,
            Notification()
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun startMyOwnForeground() {
//        val NOTIFICATION_CHANNEL_ID = "example.permanence"
//        val channelName = "Background Service"
//        val chan = NotificationChannel(
//            NOTIFICATION_CHANNEL_ID,
//            channelName,
//            NotificationManager.IMPORTANCE_NONE
//        )
//        chan.lightColor = Color.BLUE
//        chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
//        val manager = (getSystemService(NOTIFICATION_SERVICE) as NotificationManager)
//        manager.createNotificationChannel(chan)
//        val notificationBuilder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
//        val notification = notificationBuilder.setOngoing(true)
//            .setContentTitle("App is running in background")
//            .setPriority(NotificationManager.IMPORTANCE_MIN)
//            .setCategory(Notification.CATEGORY_SERVICE)
//            .build()
//        startForeground(2, notification)
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        startTimer()
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        stoptimertask()
        val broadcastIntent = Intent()
        broadcastIntent.action = "restartservice"
        broadcastIntent.setClass(this, Restarter::class.java)
        this.sendBroadcast(broadcastIntent)
    }

    private var timer: Timer? = null
    private var timerTask: TimerTask? = null
    fun startTimer() {
        timer = Timer()
        timerTask = object : TimerTask() {
            override fun run() {
                Log.i("Count", "=========  " + counter++)
                checkMatchingRequest()
            }
        }
        timer!!.schedule(timerTask, 10000, 10000) //
    }

    fun stoptimertask() {
        if (timer != null) {
            timer!!.cancel()
            timer = null
        }
    }

    fun checkMatchingRequest() {
        val db = FirebaseFirestore.getInstance()
        var ch=0
        var swapDetails: SwapDetails?=null
        Firebase.auth.currentUser?.let { Log.d("ddddddd", it.uid) }
        db.collection("swapRequests").whereEqualTo("uid", Firebase.auth.currentUser?.uid.toString())
            .get().addOnSuccessListener { doc ->

                if(doc.size()==1) {
                    for(document in doc)
                    {
                        swapDetails=document.toObject(SwapDetails::class.java)
                    }

                    db.collection("swapRequests").whereEqualTo("courseHave", swapDetails?.courseWant).whereEqualTo("courseWant",
                        swapDetails?.courseHave
                    )
                        .get().addOnSuccessListener { docum ->
                            if(docum.size()>=1 && docum.size()!=previousCount) {
                                previousCount=doc.size()
                                addNotification()
                            }
                        }


                }
        }



    }

    private fun addNotification() {

        val channelID: String="Matched Notification"
        val channelName: String="Message"
        val notificationManager=this.applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        var notificationChannel: NotificationChannel
        var notificationBuilder: NotificationCompat.Builder
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.O)
        {
            notificationChannel= NotificationChannel(channelID,channelName,NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(notificationChannel)
        }
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_IMMUTABLE)
        notificationBuilder=NotificationCompat.Builder(this,channelID)
        notificationBuilder.setContentTitle("Request Matched")
        notificationBuilder.setSmallIcon(R.drawable.ic_notification_overlay)
        notificationBuilder.addAction(R.mipmap.sym_def_app_icon,"Open App",pendingIntent)
        notificationBuilder.setContentText("Congrats! Your swap request just got matched")
        notificationBuilder.setAutoCancel(true)
        notificationManager.notify(100,notificationBuilder.build())


    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }
}