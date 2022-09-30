package com.ujjman.course.courseswap.helper

import android.app.Activity
import android.app.Application
import android.content.Context
import androidx.activity.OnBackPressedCallback
import androidx.activity.OnBackPressedDispatcher
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.runtime.*
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ujjman.course.courseswap.addcourse.AddCourse
import com.ujjman.course.courseswap.addcourse.AddCourseViewModel
import com.ujjman.course.courseswap.allRequests.AllRequests
import com.ujjman.course.courseswap.allRequests.AllRequestsViewModel
import com.ujjman.course.courseswap.login.Login
import com.ujjman.course.courseswap.login.LoginViewModel
import com.ujjman.course.courseswap.myprofile.MyProfile
import com.ujjman.course.courseswap.myprofile.MyProfileViewModel

@Composable
fun Navigation(loginViewModel: LoginViewModel, addCourseViewModel: AddCourseViewModel, allRequestsViewModel: AllRequestsViewModel, myProfileViewModel: MyProfileViewModel, context: Application, activity: Activity)
{
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = NavigationItem.Login.route)
    {
        composable(route = NavigationItem.Login.route)
        {
            Login(loginViewModel = loginViewModel, con = context, nav = navController)
        }
        composable(route = NavigationItem.AllRequests.route)
        {
            AllRequests(allRequestsViewModel = allRequestsViewModel, con = context, nav = navController, ac = activity )
        }
        composable(route = NavigationItem.AddCourse.route)
        {
            AddCourse(viewModel = addCourseViewModel, con = context, nav = navController)
        }
        composable(route = NavigationItem.MyProfile.route)
        {
            MyProfile(myProfileViewModel = myProfileViewModel, con = context, nav = navController)
        }
    }
}

@Composable
fun BackPressHandler(
    backPressedDispatcher: OnBackPressedDispatcher? =
        LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher,
    onBackPressed: () -> Unit
) {
    val currentOnBackPressed by rememberUpdatedState(newValue = onBackPressed)

    val backCallback = remember {
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                currentOnBackPressed()
            }
        }
    }

    DisposableEffect(key1 = backPressedDispatcher) {
        backPressedDispatcher?.addCallback(backCallback)

        onDispose {
            backCallback.remove()
        }
    }
}