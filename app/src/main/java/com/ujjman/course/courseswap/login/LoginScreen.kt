package com.ujjman.course.courseswap.login

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.ujjman.course.courseswap.R
import com.ujjman.course.courseswap.helper.AppTheme
import com.ujjman.course.courseswap.helper.NavigationItem

private lateinit var loginViewModelApp: LoginViewModel
private lateinit var context: Context
private lateinit var navController: NavController
private lateinit var checkSignInStatus : MutableState<Int>
@Composable
fun Login(loginViewModel: LoginViewModel, con: Context, nav: NavController)
{
    checkSignInStatus = remember { mutableStateOf(0) }
    loginViewModelApp=loginViewModel
    navController=nav
    context=con
    if(Firebase.auth.currentUser!=null)
    {
        Toast.makeText(context,"Signed In",Toast.LENGTH_SHORT).show()
        navController.navigate(NavigationItem.AllRequests.route)
        return
    }
    changeTheme(dark = isSystemInDarkTheme())
    when(checkSignInStatus.value)
    {
        1-> {
            Toast.makeText(context,"Signed In",Toast.LENGTH_SHORT).show()
            navController.navigate(NavigationItem.AllRequests.route)
        }
        2-> {
            Toast.makeText(context," Error Occured",Toast.LENGTH_SHORT).show()
        }
    }

}
@Composable
fun changeTheme(dark: Boolean) {
    AppTheme(darkTheme = dark) {
        val img: ImageVector=ImageVector.vectorResource(id = R.drawable.google_small_logo)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .background(MaterialTheme.colors.background),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(text = "Login Using Google", fontSize = 30.sp, color = MaterialTheme.colors.onPrimary)
            Spacer(modifier = Modifier.height(50.dp))
            Image(
                painterResource(R.mipmap.google_logo),
                contentDescription = "",
                contentScale = ContentScale.Inside,
                modifier = Modifier.height(200.dp)
            )
            Spacer(modifier = Modifier.height(80.dp))
            Button(
                onClick = {
                    Toast.makeText(context, "Loading...", Toast.LENGTH_SHORT).show()
                    loginViewModelApp.signIn(checkSignInStatus)
                },
                modifier = Modifier.padding(all = Dp(10F)),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.Transparent),
                border = BorderStroke(width = 1.dp, brush = SolidColor(Color.Magenta)),
                shape = MaterialTheme.shapes.large,
            )
            {
                Icon(
                    imageVector = img,
                    modifier = Modifier
                        .size(18.dp)
                    ,
                    contentDescription = "drawable icons",
                    tint = Color.Unspecified
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(text = "Sign In using Google", color = Color.Red)
            }

        }
    }
}