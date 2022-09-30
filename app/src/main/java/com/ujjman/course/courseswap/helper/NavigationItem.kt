package com.ujjman.course.courseswap.helper

sealed class NavigationItem(val route: String) {
    object Login: NavigationItem("login")
    object AllRequests: NavigationItem("allrequests")
    object AddCourse: NavigationItem("addcourse")
    object MyProfile: NavigationItem("myprofile")
}
