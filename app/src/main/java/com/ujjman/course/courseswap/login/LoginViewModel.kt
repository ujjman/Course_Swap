package com.ujjman.course.courseswap.login

import android.app.Application
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.MutableState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.ujjman.course.courseswap.MainActivity
import com.ujjman.course.courseswap.R
import com.ujjman.course.courseswap.helper.NavigationItem
import kotlinx.coroutines.launch

class LoginViewModel(application: Application, activity: MainActivity) : ViewModel() {
    private var auth: FirebaseAuth = Firebase.auth
    private var activity: MainActivity = activity
    private var context : Application = application
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var authResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var signInStatus: MutableState<Int>


fun googleSignIn()
{
    viewModelScope.launch {
        auth.addAuthStateListener { auth ->
            Log.d("addAuthStateListener: ${auth.currentUser}", "")
        }
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient =
            com.google.android.gms.auth.api.signin.GoogleSignIn.getClient(activity, gso)
        authResultLauncher =
            activity.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                val data: Intent? = result.data
                val task =
                    com.google.android.gms.auth.api.signin.GoogleSignIn.getSignedInAccountFromIntent(
                        data
                    )
                try {
                    // Google Sign In was successful, authenticate with Firebase
                    val account = task.getResult(ApiException::class.java)!!
                    Log.d("firebaseAuthWithGoogle:" + account.id, "")
                    firebaseAuthWithGoogle(account.idToken!!)
                } catch (e: ApiException) {
                    // Google Sign In failed, update UI appropriately
                    Log.d(e.toString(), "Google sign in failed")
                }
            }
    }

}

    private fun firebaseAuthWithGoogle(idToken: String) {
        viewModelScope.launch {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            auth.signInWithCredential(credential)
                .addOnCompleteListener(activity) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("signInWithCredential:s", "")
                        signInStatus.value = 1
                        // val user = auth.currentUser
                        // updateUI(user)
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.d(task.exception.toString(), "signInWithCredential:failure")
                        signInStatus.value = 2
                        Toast.makeText(context, "Error Occured", Toast.LENGTH_SHORT).show()
                        // updateUI(null)
                    }
                }
        }
    }
    fun signIn(checkSignInStatus: MutableState<Int>) {
        signInStatus=checkSignInStatus
        val signInIntent = googleSignInClient.signInIntent
        authResultLauncher.launch(signInIntent)
    }
}