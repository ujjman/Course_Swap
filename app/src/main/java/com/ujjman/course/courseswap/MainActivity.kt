package com.ujjman.course.courseswap

import android.app.ActivityManager
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import com.ujjman.course.courseswap.addcourse.AddCourseViewModel
import com.ujjman.course.courseswap.addcourse.AddCourseViewModelFactory
import com.ujjman.course.courseswap.allRequests.AllRequestsViewModel
import com.ujjman.course.courseswap.allRequests.AllRequestsViewModelFactory
import com.ujjman.course.courseswap.helper.Navigation
import com.ujjman.course.courseswap.login.LoginViewModel
import com.ujjman.course.courseswap.login.LoginViewModelFactory
import com.ujjman.course.courseswap.myprofile.MyProfileViewModel
import com.ujjman.course.courseswap.myprofile.MyProfileViewModelFactory
import com.ujjman.course.courseswap.notification.Restarter
import com.ujjman.course.courseswap.notification.YourService
import com.ujjman.course.courseswap.ui.theme.CourseSwapTheme

class MainActivity : ComponentActivity() {
    var mServiceIntent: Intent? = null
    private var mYourService: YourService? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val loginViewModel: LoginViewModel by viewModels {
            LoginViewModelFactory(
                application,
                this
            )
        }
        val addCourseViewModel: AddCourseViewModel by viewModels {
            AddCourseViewModelFactory(
                application,
                this
            )
        }
        val allRequestsViewModel: AllRequestsViewModel by viewModels {
            AllRequestsViewModelFactory(
                application,
                this
            )
        }
        val myProfileViewModel: MyProfileViewModel by viewModels {
            MyProfileViewModelFactory(
                application,
                this
            )
        }
        val mainViewModel: MainViewModel by viewModels {
            MainActivityViewModelFactory(
                application
            )
        }

        loginViewModel.googleSignIn()
        setContent {

            CourseSwapTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Navigation(
                        loginViewModel = loginViewModel,
                        addCourseViewModel = addCourseViewModel,
                        allRequestsViewModel = allRequestsViewModel,
                        myProfileViewModel = myProfileViewModel,
                        context = this.application,
                        activity = this
                    )

                    }
                }
            }
        mYourService = YourService()
        mServiceIntent = Intent(this, mYourService!!::class.java)
        if (!mainViewModel.isMyServiceRunning(mYourService!!::class.java)) {
            startService(mServiceIntent)
        }
    }


    override fun onDestroy() {
        //stopService(mServiceIntent);
        val broadcastIntent = Intent()
        broadcastIntent.action = "restartservice"
        broadcastIntent.setClass(this, Restarter::class.java)
        this.sendBroadcast(broadcastIntent)
        super.onDestroy()
    }
}




