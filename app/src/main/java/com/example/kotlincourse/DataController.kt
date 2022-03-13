package com.example.kotlincourse

import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.firestore.ktx.firestore
import java.util.*
import java.util.Timer
import kotlin.concurrent.schedule

class DataController: ViewModel() {
    val db = Firebase.firestore
    val items = mutableStateMapOf<String, String>()
    var dbErr = mutableStateOf("");
    var itemSuccess = mutableStateOf("");

    fun getShoppingList( userEmail: String ){
        db.collection("notes")
            .whereEqualTo("userid", userEmail)
            .get()
            .addOnSuccessListener { result ->
                dbErr.value = ""
                items.clear()
                for (document in result) {
                    items[document.id] = document.data["value"].toString()
                }
            }
            .addOnFailureListener { exception ->
                //this could be a permanent error, so it should stay until resolved.
                dbErr.value = "Couldn't download your list, please try again later."
            }
    }

    fun addShoppingListItem( userEmail: String, itemValue: String){
        val data1 = hashMapOf(
            "value" to itemValue,
            "userid" to userEmail
        )
        db.collection("notes")
            .add(data1)
            .addOnSuccessListener { document ->
                setSuccess("Item added to list!")
            }
            .addOnFailureListener { exception ->
                setError("Db error $exception")
            }
        getShoppingList(userEmail)
    }

    fun deleteShoppingListItem( userEmail: String, documentId: String){
        db.collection("notes").document(documentId)
            .delete()
            .addOnSuccessListener { result ->
                setSuccess("Item removed from list!")
            }
            .addOnFailureListener { exception ->
                setError("Db error $exception")
            }
        getShoppingList(userEmail)
    }

    private fun setError(errMsg : String)
    {
        dbErr.value = errMsg
        itemSuccess.value = ""
        Timer().schedule(5000) {
            dbErr.value = ""
        }
    }

    private fun setSuccess(errMsg : String)
    {
        dbErr.value = ""
        itemSuccess.value = errMsg
        Timer().schedule(5000) {
            itemSuccess.value = ""
        }
    }
}
