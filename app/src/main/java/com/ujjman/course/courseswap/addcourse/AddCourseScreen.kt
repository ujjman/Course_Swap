package com.ujjman.course.courseswap.addcourse

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.ujjman.course.courseswap.R
import com.ujjman.course.courseswap.helper.AppTheme
import com.ujjman.course.courseswap.helper.NavigationItem
import com.ujjman.course.courseswap.helper.ThemeCheck
import kotlinx.coroutines.launch

private lateinit var addCourseViewModel: AddCourseViewModel
private lateinit var navController: NavController
private lateinit var checkUploaded: MutableState<Int>
private lateinit var context: Context

@Composable
fun AddCourse(viewModel: AddCourseViewModel, con: Context, nav: NavController)
{
    context=con
    checkUploaded = remember {
        mutableStateOf(0)
    }
    addCourseViewModel = viewModel
    navController=nav
    changeTheme(dark = ThemeCheck.dark)
    when(checkUploaded.value) {
        1-> {navController.navigate(NavigationItem.AllRequests.route) }
    }
}

@Composable
fun changeTheme(dark: Boolean) {
    AppTheme(darkTheme = dark) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .background(MaterialTheme.colors.background)
                .padding(0.dp, 20.dp, 0.dp, 20.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(text = "Add a Course Swap Request", fontSize = 30.sp , color = MaterialTheme.colors.onPrimary)
            Spacer(modifier = Modifier.height(50.dp))
            Image(
                painterResource(R.mipmap.course_bg),
                contentDescription = "",
                contentScale = ContentScale.Crop,
                modifier = Modifier.height(200.dp)
            )
            Spacer(modifier = Modifier.height(40.dp))
            val textState = remember { mutableStateOf(TextFieldValue()) }
            TextField(
                value = textState.value,
                modifier = Modifier.padding(vertical = 4.dp),
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.textFieldColors(
                    textColor = MaterialTheme.colors.onPrimary,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    cursorColor = Color.LightGray,
                    focusedLabelColor = Color.Red
                ),
                onValueChange = { textState.value = it },
                label = { Text(text = "Course you have")}
            )
            Spacer(modifier = Modifier.height(20.dp))
            val textState2 = remember { mutableStateOf(TextFieldValue()) }
            TextField(
                value = textState2.value,
                modifier = Modifier.padding(vertical = 4.dp),
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.textFieldColors(
                    textColor = MaterialTheme.colors.onPrimary,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    cursorColor = Color.LightGray,
                    focusedLabelColor = Color.Red
                ),
                onValueChange = { textState2.value = it },
                label = { Text(text = "Course you want")}
            )
            Spacer(modifier = Modifier.height(50.dp))
            Button(
                onClick = {
                    Toast.makeText(context,"Saving...", Toast.LENGTH_SHORT).show()
                    addCourseViewModel.viewModelScope.launch() {
                        addCourseViewModel.addCourseRequest(textState.value.text, textState2.value.text, checkUploaded)
                    }

                },
                modifier = Modifier
                    .width(120.dp)
                    .height(50.dp)


            ) {
                Text(text = "Save", fontSize = 28.sp)

            }
        }
    }
}
