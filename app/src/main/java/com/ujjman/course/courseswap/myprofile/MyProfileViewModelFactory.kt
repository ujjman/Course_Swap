package com.ujjman.course.courseswap.myprofile

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ujjman.course.courseswap.MainActivity
import com.ujjman.course.courseswap.login.LoginViewModel

class MyProfileViewModelFactory(
    private val application: Application,
    private val activity: MainActivity
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MyProfileViewModel::class.java)) {
            return MyProfileViewModel(application,activity) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}