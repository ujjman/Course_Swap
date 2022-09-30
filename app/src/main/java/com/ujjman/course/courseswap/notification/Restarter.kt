package com.ujjman.course.courseswap.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.Toast

class Restarter : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Log.i("Broadcast Listened", "Service tried to stop")
            context.startService(Intent(context, YourService::class.java))

    }
}