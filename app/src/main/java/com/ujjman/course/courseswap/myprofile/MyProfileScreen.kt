package com.ujjman.course.courseswap.myprofile

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.R
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.jet.firestore.JetFirestore
import com.jet.firestore.getListOfObjects
import com.ujjman.course.courseswap.SwapDetails
import com.ujjman.course.courseswap.allRequests.*
import com.ujjman.course.courseswap.helper.*

private lateinit var selectedOption: MutableState<Boolean>
private lateinit var changeThemeToDark: MutableState<Boolean>
private lateinit var changeThemeToLight: MutableState<Boolean>
private lateinit var viewModel: MyProfileViewModel
private lateinit var navController: NavController
private lateinit var context: Context
private var myRequest: SwapDetails =SwapDetails("","","")
private lateinit var check: MutableState<Int>
private lateinit var requestsListMatchingRequests: MutableState<List<SwapDetails>>
private lateinit var requestsListMyRequests: MutableState<List<SwapDetails>>

@Composable
fun MyProfile(myProfileViewModel: MyProfileViewModel, con: Context, nav: NavController)
{
    viewModel = myProfileViewModel
    navController=nav
    context=con
    requestsListMatchingRequests=remember { mutableStateOf(listOf<SwapDetails>()) }
    requestsListMyRequests=remember { mutableStateOf(listOf<SwapDetails>()) }
    check= remember {
        mutableStateOf(0)
    }
    selectedOption= remember { mutableStateOf(true) }
    changeThemeToDark= remember { mutableStateOf(ThemeCheck.dark) }
    changeThemeToLight= remember { mutableStateOf(ThemeCheck.light) }
    viewModel.theme(changeThemeToDark.value, changeThemeToLight.value, selectedOption)
    changeTheme(dark = ThemeCheck.dark)

}

@Composable
fun changeTheme(dark: Boolean)
{
    var selectedOptionForRequests = remember { mutableStateOf(true) }
    var changeScreen = remember {
        mutableStateOf(1)
    }
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
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(text = "Light" , color = MaterialTheme.colors.onPrimary)
                Spacer(modifier = Modifier.width(5.dp))
                RadioButton(selected = selectedOption.value,
                    colors = RadioButtonDefaults.colors(selectedColor = Blue300 , unselectedColor = Blue300),
                    onClick = {
                    viewModel.changeThemeValue(selectedOption, changeThemeToDark, changeThemeToLight)
                })
                Spacer(modifier = Modifier.width(20.dp))
                Text(text = "Dark", color = MaterialTheme.colors.onPrimary)
                Spacer(modifier = Modifier.width(5.dp))
                RadioButton(selected = !selectedOption.value,
                    colors = RadioButtonDefaults.colors(selectedColor = Blue300 , unselectedColor = Blue300),
                    onClick = {
                    viewModel.changeThemeValue2(selectedOption, changeThemeToDark, changeThemeToLight)
                }
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
            Button(onClick = {
                viewModel.signOut()
                navController.navigate(NavigationItem.Login.route)
            },
                modifier = Modifier
                    .width(120.dp)
                    .height(50.dp)
            ) {
                Text(text = "Logout")
            }
            Spacer(modifier = Modifier.height(40.dp))

            //viewModel.matchingRequests()
            Row (modifier = Modifier
                .height(40.dp)
                .fillMaxWidth(), horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
                ){
                Text(text = "Matching Requests", color = MaterialTheme.colors.onPrimary)
                RadioButton(selected =selectedOptionForRequests.value,
                    onClick = {
                              viewModel.changeRequest(selectedOptionForRequests,1)
                        changeScreen.value=1
                    },
                    colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colors.onBackground , unselectedColor = MaterialTheme.colors.onBackground))
                Text(text = "My Request", color = MaterialTheme.colors.onPrimary)
                RadioButton(selected =!selectedOptionForRequests.value,
                    onClick = {
                        viewModel.changeRequest(selectedOptionForRequests,2)
                        changeScreen.value=2
                    },
                    colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colors.onBackground , unselectedColor = MaterialTheme.colors.onBackground))
            }

            when(changeScreen.value)
            {
                1-> matchingRequests()
                2-> {
                    myRequest()
                    requestsListMatchingRequests.value= listOf()
                }
            }


        }
    }
}


@Composable
fun matchingRequests()
{

    val listState = rememberLazyListState()
    var isEmptyRequests = remember {
        mutableStateOf(0)
    }
    if(myRequest.courseWant.equals(""))
    {
        viewModel.myRequest(myRequest,check)
    }
    else
    {
        check.value=1
    }
    when(check.value) {
        0-> {
            requestsListMatchingRequests.value = listOf()
        }
        1-> {
            JetFirestore(
                path = { collection("swapRequests") },
                limitOnSingleTimeCollectionFetch = 2,
                queryOnCollection = {
                    whereEqualTo(
                        "courseHave",
                        myRequest.courseWant
                    ).whereEqualTo("courseWant", myRequest.courseHave)
                },
                onSingleTimeCollectionFetch = { values, _ ->
                    isEmptyRequests.value = 1
                    requestsListMatchingRequests.value = requestsListMatchingRequests.value + values.getListOfObjects()
                }
            ) { pagination ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 15.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    when (requestsListMatchingRequests.value.size) {
                        0 -> {
                            when(isEmptyRequests.value) {
                                0-> LoadingScreen()
                               1-> {}
                            }
                        }
                        else -> {
                            LazyColumn(
                                modifier = Modifier
                                    .padding(10.dp)
                                    .weight(1f),
                                state = listState
                            ) {
                                items(requestsListMatchingRequests.value.size) { item ->
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
                                            Text(
                                                text = textHave + requestsListMatchingRequests.value[item].courseHave.toString(),
                                                fontSize = 16.sp
                                            )
                                            Spacer(modifier = Modifier.height(20.dp))
                                            Text(
                                                text = textWant + requestsListMatchingRequests.value[item].courseWant.toString(),
                                                fontSize = 16.sp
                                            )
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
                                Text(text = "Load More")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun myRequest()
{
    val listState: LazyListState = rememberLazyListState()
    var isEmptyRequests = remember {
        mutableStateOf(0)
    }
    JetFirestore(
        path = { collection("swapRequests") },
        limitOnSingleTimeCollectionFetch = 1,
        queryOnCollection = {  whereEqualTo("uid", Firebase.auth.currentUser?.uid.toString())},
        onRealtimeCollectionFetch = { values, _ ->
            isEmptyRequests.value=1
            requestsListMyRequests.value =  values.getListOfObjects()
        }
    ) { pagination ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 15.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            when (requestsListMyRequests.value.size) {
                0 -> {
                    when(isEmptyRequests.value) {
                        0-> LoadingScreen()
                        1-> {}
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .padding(10.dp)
                            .weight(1f),
                        state = listState
                    ) {
                        items(requestsListMyRequests.value.size) { item ->
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
                                    Text(
                                        text = textHave + requestsListMyRequests.value[item].courseHave.toString(),
                                        fontSize = 16.sp
                                    )
                                    Spacer(modifier = Modifier.height(20.dp))
                                    Text(
                                        text = textWant + requestsListMyRequests.value[item].courseWant.toString(),
                                        fontSize = 16.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}