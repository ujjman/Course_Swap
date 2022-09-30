package com.ujjman.course.courseswap.addcourse

import android.app.Application
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.ujjman.course.courseswap.MainActivity
import com.ujjman.course.courseswap.SwapDetails
import com.ujjman.course.courseswap.TopCourses

class AddCourseViewModel(application: Application, activity: MainActivity) : ViewModel() {
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDatabase: DatabaseReference
    private lateinit var db : FirebaseFirestore
    private var requestsHave: Int=0
    private var requestsWant: Int=0

    suspend fun addCourseRequest(courseHave: String, courseWant:String, check: MutableState<Int>)
    {
        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        db.collection("topCourses")
            .whereEqualTo("name", courseWant.uppercase())
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    var req : Int = document.data["requests"].toString().toInt()
                    req++
                    val newReq: TopCourses = TopCourses(req.toString().uppercase(), document.id)
                    db.collection("topCourses").document(document.id).set(newReq)
                    requestsWant=req
                }
                if(documents.size()==0)
                {
                    val newReq: TopCourses = TopCourses("1", courseWant.uppercase())
                    db.collection("topCourses").document(courseWant.uppercase()).set(newReq)
                    requestsWant=1
                }

                db.collection("topCourses")
                    .whereEqualTo("name", courseHave.uppercase())
                    .get()
                    .addOnSuccessListener { documents ->
                        for (document in documents) {
                            var req : Int = document.data["requests"].toString().toInt()
                            requestsHave=req
                        }
                        if(documents.size()==0)
                        {
                            requestsHave=0
                        }
                        val user = mAuth.currentUser
                        if (user != null) {
                            Log.d("user",user.toString()+"   "+user.email)
                        }
                        val swapDetails: SwapDetails = SwapDetails(courseHave= courseHave.uppercase(),courseWant= courseWant.uppercase(), uid = user?.uid, requestsHave=requestsHave, requestsWant=requestsWant)
                        db.collection("swapRequests").add(swapDetails).addOnSuccessListener {
                            db.collection("swapRequests").whereEqualTo("courseHave", courseWant.uppercase()).get().addOnSuccessListener {
                                documents->
                                for(doc in documents)
                                {
                                    var reqcheck=doc.data["requestsHave"].toString().toInt()
                                    var cohave = doc.data["courseHave"].toString().uppercase()
                                    var cowant = doc.data["courseWant"].toString().uppercase()
                                    var id = doc.data["uid"].toString()
                                    var reqwant = doc.data["requestsWant"].toString().toInt()
                                    Log.d("hello","  "+reqcheck+"   "+requestsWant)
                                    val swap = SwapDetails(cohave,cowant,id,requestsWant,reqwant)
                                    db.collection("swapRequests").document(doc.id).set(swap)
                                }
                                db.collection("swapRequests").whereEqualTo("courseWant", courseWant.uppercase()).get().addOnSuccessListener { documents ->
                                    for (doc in documents) {
                                        var reqcheck = doc.data["requestsHave"].toString().toInt()
                                        var cohave = doc.data["courseHave"].toString().uppercase()
                                        var cowant = doc.data["courseWant"].toString().uppercase()
                                        var id = doc.data["uid"].toString()
                                        var reqwant = doc.data["requestsWant"].toString().toInt()
                                        val swap =
                                            SwapDetails(cohave, cowant, id, reqcheck, requestsWant)
                                        db.collection("swapRequests").document(doc.id).set(swap)
                                    }
                                }.addOnSuccessListener {
                                    check.value=1
                                }



                            }
                        }

                    }
                    .addOnFailureListener { exception ->
                        Log.d("Error ", exception.toString())
                    }


            }
            .addOnFailureListener { exception ->
                Log.d("Error ", exception.toString())
            }




    }

}