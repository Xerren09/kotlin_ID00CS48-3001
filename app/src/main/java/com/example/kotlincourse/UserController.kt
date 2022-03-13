package com.example.kotlincourse

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.*
import kotlin.concurrent.schedule

class UserController: ViewModel() {
    var userName = mutableStateOf("")
    var loginErrorText = mutableStateOf("")

    fun login( userEmail: String, password: String ){
        if (userEmail.isEmpty() || password.isEmpty())
        {
            setError("Email or password can not be empty.")
        }
        else
        {
            Firebase.auth.signInWithEmailAndPassword(userEmail, password)
                .addOnSuccessListener {
                    userName.value = userEmail; loginErrorText.value = ""
                }
                .addOnFailureListener {
                    setError("Incorrect email or password.")
                }
        }
    }

    fun logout(){
        Firebase.auth.signOut()
        userName.value = ""
    }

    private fun setError(errMsg : String)
    {
        loginErrorText.value = errMsg
        Timer().schedule(5000) {
            loginErrorText.value = ""
        }
    }
}
