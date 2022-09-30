package com.ujjman.course.courseswap.myprofile

import android.app.Application
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.ujjman.course.courseswap.MainActivity
import com.ujjman.course.courseswap.SwapDetails
import com.ujjman.course.courseswap.helper.ThemeCheck
import kotlinx.coroutines.launch

class MyProfileViewModel(application: Application, activity: MainActivity) : ViewModel() {
    private var context: Context = application
    private lateinit var allRequestsList: List<SwapDetails>
    private lateinit var myRequestsList: List<SwapDetails>
    private lateinit var matchingRequestsList: List<SwapDetails>

    fun allRequests() {
        viewModelScope.launch {
            val db = FirebaseFirestore.getInstance()
            allRequestsList = mutableListOf()
            myRequestsList = mutableListOf()
            db.collection("swapRequests")
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val swap = document.toObject(SwapDetails::class.java)
                        allRequestsList = allRequestsList + swap
                        if (swap.uid.equals(Firebase.auth.currentUser?.uid)) {
                            myRequestsList = myRequestsList + swap
                        }
                    }

                    for (element in myRequestsList) {
                        for (request in allRequestsList) {
                            if (element.courseWant.equals(request.courseHave)) {
                                matchingRequestsList = matchingRequestsList + request
                            }
                        }
                    }
                    if (allRequestsList.isEmpty() || myRequestsList.isEmpty()) {
                        Log.d("ujjman", "Nothing matching found")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.w("ujjman", "Error getting documents: ", exception)
                }
        }
    }

    fun myRequest(request: SwapDetails, check: MutableState<Int>)
    {
        viewModelScope.launch {
            val db = FirebaseFirestore.getInstance()
            db.collection("swapRequests").whereEqualTo("uid", Firebase.auth.currentUser?.uid)
                .get()
                .addOnSuccessListener { documents ->
                    if(documents.documents.size==0)
                    {
                        check.value=2
                        return@addOnSuccessListener
                    }
                    else
                    {
                       request.courseHave = documents.documents[0].toObject(SwapDetails::class.java)?.courseHave
                       request.courseWant = documents.documents[0].toObject(SwapDetails::class.java)?.courseWant
                       request.uid = documents.documents[0].toObject(SwapDetails::class.java)?.uid
                        check.value=1
                    }
                }
        }
    }


    fun theme(dark: Boolean, light: Boolean, selectedOption: MutableState<Boolean>) {
        if (dark) {
            ThemeCheck.dark = true
            ThemeCheck.light = false
            selectedOption.value = false
        } else if (light) {
            ThemeCheck.dark = false
            ThemeCheck.light = true
            selectedOption.value = true
        }
    }

    fun changeThemeValue(
        selectedOption: MutableState<Boolean>,
        changeThemeToDark: MutableState<Boolean>,
        changeThemeToLight: MutableState<Boolean>
    ) {
        if (!selectedOption.value) {
            selectedOption.value = selectedOption.value == false
        }
        if (selectedOption.value) {
            changeThemeToLight.value = true
            changeThemeToDark.value = false
        }
    }

    fun changeThemeValue2(
        selectedOption: MutableState<Boolean>,
        changeThemeToDark: MutableState<Boolean>,
        changeThemeToLight: MutableState<Boolean>
    ) {
        if (selectedOption.value) {
            selectedOption.value = selectedOption.value == false
        }
        if (!selectedOption.value) {
            changeThemeToLight.value = false
            changeThemeToDark.value = true
        }
    }

    fun changeRequest(selectedOption: MutableState<Boolean>, callingButton: Int)
    {
        if(!selectedOption.value && callingButton==1) {
            selectedOption.value = selectedOption.value == false
        }
        else if (selectedOption.value && callingButton==2) {
            selectedOption.value = selectedOption.value == false
        }
    }

    fun signOut() {
        Firebase.auth.signOut()
    }

}