package com.ujjman.course.courseswap.allRequests

import android.app.Application
import android.util.Log
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.MutableState
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.ktx.Firebase
import com.ujjman.course.courseswap.MainActivity

class AllRequestsViewModel(application: Application, activity: MainActivity) : ViewModel(){

    fun isLastItemVisible(lazyListState: LazyListState): Boolean {
        val lastItem = lazyListState.layoutInfo.visibleItemsInfo.lastOrNull()
        return lastItem == null || lastItem.size + lastItem.offset <= lazyListState.layoutInfo.viewportEndOffset
    }

    fun query(text: String, check: Int) : (CollectionReference.()->Query)?
    {
        var collectionReference: (CollectionReference.() -> Query)? =null
        if(check==0) {
            collectionReference = { whereEqualTo("courseWant",text) }
        }
        else if(check==1) {
            collectionReference = { whereEqualTo("courseHave",text) }
        }
        return collectionReference
    }

    fun checkForExistingRequest(check: MutableState<Int>)
    {
        val db = FirebaseFirestore.getInstance()
        db.collection("swapRequests").whereEqualTo("uid",Firebase.auth.currentUser?.uid.toString()).get().addOnSuccessListener {
            doc->
            if(doc.documents.size==0)
                check.value=1
            else
                check.value=2
        }.addOnFailureListener {
            check.value=2
        }
        Log.d("ujjman", "sd"+check)
    }

}