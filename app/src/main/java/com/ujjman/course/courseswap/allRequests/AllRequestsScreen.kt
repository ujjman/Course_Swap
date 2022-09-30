package com.ujjman.course.courseswap.allRequests

import android.app.Activity
import android.app.Application
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Top
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.PlaceholderVerticalAlign.Companion.Top
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.Query
import com.google.firebase.ktx.Firebase
import com.jet.firestore.JetFirestore
import com.jet.firestore.Pagination
import com.jet.firestore.getListOfObjects
import com.ujjman.course.courseswap.R
import com.ujjman.course.courseswap.SwapDetails
import com.ujjman.course.courseswap.TopCourses
import com.ujjman.course.courseswap.helper.*


const val textHave: String = "Course Have : "
const val textWant: String = "Course Want : "
private var checkFilterRequest: Int = 0
private var textLoad: String = "Load More "
private lateinit var viewModel: AllRequestsViewModel
private lateinit var context: Application
private lateinit var activity: Activity
private lateinit var list : MutableState<List<SwapDetails>>
private lateinit var listTopCourses : MutableState<List<TopCourses>>
private lateinit var backPress : MutableState<Boolean>
private lateinit var query: MutableState<(CollectionReference.() -> Query)?>
private lateinit var navController: NavController
private lateinit var checkFetched: MutableState<Int>
@Composable
fun AllRequests(allRequestsViewModel: AllRequestsViewModel, con: Application, nav: NavController, ac: Activity) {
    viewModel=allRequestsViewModel
    navController=nav
    activity=ac
    backPress = remember { mutableStateOf(false) }
    checkFetched = remember { mutableStateOf(0) }
    context=con
    list= remember { mutableStateOf(listOf<SwapDetails>()) }
    listTopCourses= remember { mutableStateOf(listOf<TopCourses>()) }
    query = remember { mutableStateOf(null) }
    when(backPress.value)
    {
        true-> showExitBox()
    }
    val backPress = {backPress()}
    BackPressHandler(onBackPressed = backPress)
    changeTheme(dark = ThemeCheck.dark)
}

fun backPress()
{
    backPress.value=true;
}

@Composable
fun showExitBox()
{
    AlertDialog(
        title = { Text(text = "Exit?") },
        text = {Text(text = "Do you really want to exit?")},
    onDismissRequest = { },
    confirmButton = {
        TextButton(onClick = { activity.finish() })
        { Text(text = "Exit") }
    },
    dismissButton = {
        TextButton(onClick = {backPress.value=false})
        { Text(text = "Cancel") }
    }
)
}

@Composable
fun LoadingScreen()
{
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(100.dp)
    ) {
        CircularProgressIndicator(color = Color.White)
    }
}


@Composable
fun backSide()
{
    var requestsList by remember { mutableStateOf(listOf<TopCourses>()) }
    var selectedOption = remember { mutableStateOf(true) }
    val textState = remember { mutableStateOf(TextFieldValue()) }

    Column(modifier = Modifier
        .fillMaxWidth()
        .background(color = MaterialTheme.colors.primary),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
            Row(modifier = Modifier
                .height(70.dp)
                .fillMaxWidth()
                .padding(top = 10.dp),
            ) {
                Text(
                    text = "Top Courses",
                    modifier = Modifier
                        .padding(20.dp)
                        .weight(4f),
                    textAlign = TextAlign.Center
                )
                Button(modifier = Modifier
                    .padding(end = 5.dp)
                    .weight(1f)
                    .background(Color.Transparent),
                    onClick = {
                        navController.navigate(NavigationItem.MyProfile.route)
                    }) {
                    Image(painter = rememberAsyncImagePainter(Firebase.auth.currentUser?.photoUrl), contentDescription = "" )
                }
            }


        JetFirestore(
            path = { collection("topCourses") },
            limitOnSingleTimeCollectionFetch = 3,
            queryOnCollection = { orderBy("requests", Query.Direction.DESCENDING) },
            onSingleTimeCollectionFetch = { values, _ ->
                requestsList = values.getListOfObjects()
                listTopCourses.value = values.getListOfObjects()
            }
        ) { pagination ->
            when(listTopCourses.value.size) {
                0-> {}
                else ->
                LazyColumn(
                    modifier = Modifier
                        .padding(5.dp)
                ) {
                    items(listTopCourses.value.size) { item ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(5.dp),
                            shape = RoundedCornerShape(18.dp),
                            backgroundColor = MaterialTheme.colors.secondaryVariant
                        ) {
                            Column(
                                modifier = Modifier
                                    .height(50.dp)
                                    .padding(start = 15.dp),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.Start
                            ) {
                                Text(
                                    text = "Course Want : " + listTopCourses.value[item].name.toString(),
                                    fontSize = 16.sp
                                )
                            }
                        }

                    }
                }
            }

        }
        Spacer(modifier = Modifier.padding(bottom = 20.dp))
        Text(modifier = Modifier.fillMaxWidth(), text = "Apply Filter", textAlign = TextAlign.Center, fontSize = 18.sp)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = textWant, fontSize = 14.sp)
                RadioButton(selected = selectedOption.value,
                    colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colors.onBackground , unselectedColor = MaterialTheme.colors.onBackground),
                    onClick = {
                        if(!selectedOption.value) {
                            selectedOption.value = selectedOption.value == false
                        }
                        checkFilterRequest=0
                    }
                )
                Text(text = textHave, fontSize = 14.sp)
                RadioButton(selected = !selectedOption.value,
                    colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colors.onBackground , unselectedColor = MaterialTheme.colors.onBackground),
                    onClick = {
                        if (selectedOption.value) {
                            selectedOption.value = selectedOption.value == false
                        }
                        checkFilterRequest=1
                    }
                )
            }
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
                focusedLabelColor = Color.White
            ),
            onValueChange = { textState.value = it },
            label = { Text(text = "Enter course name")}
        )
        Row(horizontalArrangement = Arrangement.Center) {
            Button(
                onClick = {
                    list.value = listOf()
                    query.value = viewModel.query(textState.value.text.uppercase(), checkFilterRequest)
                    checkFetched.value=0
                },
                colors = ButtonDefaults.buttonColors(Blue700)
            ) {
                Text(text = "Apply")

            }
            Spacer(modifier = Modifier.width(30.dp))
            Button(
                onClick = {
                    list.value = listOf()
                    query.value = null
                    checkFetched.value=0
                },
                colors = ButtonDefaults.buttonColors(Blue700)
            ) {
                Text(text = "Clear Filter")

            }
        }
        
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun changeTheme(dark: Boolean) {
    var check = remember {
        mutableStateOf(0)
    }
    when(check.value) {
        1 -> {
            navController.navigate(NavigationItem.AddCourse.route)
            check.value = 0
        }
        2 -> {
            Toast.makeText(
                context,
                "Only 1 request is allowed per user and you have already made a request",
                Toast.LENGTH_LONG
            ).show()
             check.value=0
        }
    }
    AppTheme(darkTheme = dark) {
        Scaffold(
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        viewModel.checkForExistingRequest(check)
                    },
                    backgroundColor = Color.Magenta,
                    content = {
                        Icon(Icons.Filled.Add,"")
                    }
                )
            }
        ) {

            BackdropScaffold(
                modifier = Modifier.fillMaxWidth(),
                scaffoldState = rememberBackdropScaffoldState(BackdropValue.Revealed),
                frontLayerShape = BottomSheetShape,
                frontLayerScrimColor = Color.Unspecified,
                appBar = {},
                peekHeight = 100.dp,
                backLayerContent = {
                    backSide()
                },
                frontLayerContent = {
                    frontSide()
                }
            )
        }
    }

}



@Composable
fun frontSide()
{
    var requestsList by remember { mutableStateOf(listOf<SwapDetails>()) }
    val listState: LazyListState = rememberLazyListState()

    JetFirestore(
        path = { collection("swapRequests") },
        limitOnSingleTimeCollectionFetch = 5,
        queryOnCollection = query.value,
        onSingleTimeCollectionFetch = { values, _ ->
            requestsList = requestsList + values.getListOfObjects()
            list.value= list.value + values.getListOfObjects()
            checkFetched.value=1
        }
    ) { pagination ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 15.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            when (list.value.size) {
                0 -> {
                    if (checkFetched.value == 0)
                        LoadingScreen()
                    else {
                        Toast.makeText(context,"No requests to show",Toast.LENGTH_SHORT).show()
                    }
                }
                else -> {
                    checkFetched.value = 0
                    LazyColumn(
                        modifier = Modifier
                            .padding(10.dp)
                            .weight(1f),
                        state = listState
                    ) {
                        items(list.value.size) { item ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(5.dp),
                                shape = RoundedCornerShape(8.dp),
                                backgroundColor = MaterialTheme.colors.secondaryVariant
                            ) {
                                Column(
                                    modifier = Modifier
                                        .height(100.dp)
                                        .padding(16.dp),
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.Start
                                ) {
                                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            modifier = Modifier.weight(1f) ,
                                            text = textHave + list.value[item].courseHave.toString(),
                                            fontSize = 16.sp
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text(text = "Requests : ${list.value[item].requestsHave}", fontSize = 12.sp)
                                    }
                                    Spacer(modifier = Modifier.height(20.dp))
                                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            modifier = Modifier.weight(1f),
                                            text = textWant + list.value[item].courseWant.toString(),
                                            fontSize = 16.sp
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text( text = "Requests : ${list.value[item].requestsWant}", fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                    }
                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp)
                            .height(40.dp),
                        onClick = {
                            pagination.loadNextPage()
                        },
                        colors = ButtonDefaults.buttonColors(Blue700)

                    ) {
                        Text(text = textLoad)
                    }
                }
            }
        }
    }
}
