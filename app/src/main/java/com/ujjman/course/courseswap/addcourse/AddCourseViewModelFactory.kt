package com.ujjman.course.courseswap.addcourse

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ujjman.course.courseswap.MainActivity

class AddCourseViewModelFactory(
    private val application: Application,
    private val activity: MainActivity
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddCourseViewModel::class.java)) {
            return AddCourseViewModel(application,activity) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}