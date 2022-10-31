package com.example.woof.repo

import android.app.Application
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage


class DBRepository(private val application: Application) {

    private val firebaseStorage = FirebaseStorage.getInstance()
    private val firebaseDB = FirebaseFirestore.getInstance()

    private val responseDBLivedata = MutableLiveData<Response<String>>()
    val responseDB: LiveData<Response<String>>
        get() = responseDBLivedata

    private val userProfileLivedata = MutableLiveData<MutableList<String?>>()
    val userProfileData: LiveData<MutableList<String?>>
        get() = userProfileLivedata

    fun uploadImageToStorage(imageUri: Uri, user: FirebaseUser) {
        val ref = firebaseStorage.reference.child("images/${user.uid}/${imageUri.lastPathSegment}")
        ref.putFile(imageUri)
            .addOnSuccessListener {
                ref.downloadUrl
                    .addOnSuccessListener {
                        responseDBLivedata.postValue(Response.Success())
                        uploadImageUrlToDatabase(it, user)
                    }
                    .addOnFailureListener {
                        responseDBLivedata.postValue(Response.Failure(it.toString()))
                    }
            }
            .addOnFailureListener {
                responseDBLivedata.postValue(Response.Failure(it.toString()))
            }
    }

    private fun uploadImageUrlToDatabase(uri: Uri, user: FirebaseUser) {
        val doc = firebaseDB.collection("User").document(user.uid)
        doc.get().addOnSuccessListener {
            if (it.exists()) {
                val usertype = it.getString("Usertype")
                val name = it.getString("Name")
                doc.update("Image Url", uri.toString())
                val innerDoc = firebaseDB.collection(usertype!!).document(user.uid)
                innerDoc.get().addOnSuccessListener {
                    if (it.exists()) {
                        responseDBLivedata.postValue(Response.Success())
                        innerDoc.update("Image Url", uri.toString())
                    } else {
                        responseDBLivedata.postValue(Response.Failure("Something went wrong!"))
                    }
                }.addOnFailureListener {
                    responseDBLivedata.postValue(Response.Failure(it.toString()))
                }
            } else {
                responseDBLivedata.postValue(Response.Failure("Document not exist"))
            }
        }
            .addOnFailureListener {
                responseDBLivedata.postValue(Response.Failure(it.cause.toString()))
            }
    }

    fun getProfileData(user: FirebaseUser) {
        firebaseDB.collection("User").document(user.uid).get()
            .addOnSuccessListener {
                if (it.exists()) {
                    var temp = ""
                    when (it.getString("Usertype")!!) {
                        "Normal User" -> temp = "Pet Name"
                        "Seller" -> temp = "Trade License Number"
                        "Doctor" -> temp = "Registration No"
                    }
                    val list = mutableListOf(
                        it.getString("Usertype")!!,
                        it.getString("Name")!!,
                        it.getString("Image Url")!!,
                        it.getString("Phone No")!!,
                        it.getString(temp)
                    )
                    userProfileLivedata.postValue(list.toMutableList())
                }
            }
    }

    fun changeProfileData(user: FirebaseUser, field: String, newData: String) {
        val doc = firebaseDB.collection("User").document(user.uid)
        doc.get().addOnSuccessListener {
            if (it.exists()) {
                val usertype = it.getString("Usertype")
                doc.update(field, newData)
                val innerDoc = firebaseDB.collection(usertype!!).document(user.uid)
                innerDoc.get().addOnSuccessListener {
                    innerDoc.update(field, newData)
                    responseDBLivedata.postValue(Response.Success())
                }
                    .addOnFailureListener {
                        responseDBLivedata.postValue(Response.Failure(it.toString()))
                    }
            } else {
                responseDBLivedata.postValue(Response.Failure("Document not exist"))
            }
        }
            .addOnFailureListener {
                responseDBLivedata.postValue(Response.Failure(it.toString()))
            }
    }
}