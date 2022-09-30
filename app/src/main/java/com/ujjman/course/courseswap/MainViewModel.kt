package com.ujjman.course.courseswap

import android.app.ActivityManager
import android.content.Context
import android.content.Context.ACTIVITY_SERVICE
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.ViewModel

class MainViewModel(context: Context): ViewModel() {
    private val context: Context = context
    fun isMyServiceRunning(serviceClass: Class<*>): Boolean {
        val manager = context.getSystemService(ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                Log.i("Service status", "Running")
                return true
            }
        }
        Log.i("Service status", "Not running")
        return false
    }
}