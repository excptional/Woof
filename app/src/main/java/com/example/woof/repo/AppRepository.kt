package com.example.woof.repo

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject

class AppRepository(private val application: Application) {
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firebaseDB: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val userLiveData = MutableLiveData<FirebaseUser>()
    val userData: LiveData<FirebaseUser>
        get() = userLiveData
    private val responseLiveData = MutableLiveData<Response<String>>()
    val response: LiveData<Response<String>>
        get() = responseLiveData
    private val responseDBLivedata = MutableLiveData<Response<String>>()
    val responseDB: LiveData<Response<String>>
        get() = responseDBLivedata
    private val usertype = MutableLiveData<Usertype>()
    val user: LiveData<Usertype>
    get() = usertype

    fun login(email: String?, password: String?) {
        firebaseAuth.signInWithEmailAndPassword(email!!, password!!)
            .addOnCompleteListener(
            ) { task ->
                if (task.isSuccessful) {
                    responseLiveData.postValue(Response.Success())
                    userLiveData.postValue(firebaseAuth.currentUser)
                } else {
                    responseLiveData.postValue(Response.Failure(task.exception.toString()))
                }
            }
    }

    fun normalUserRegister(
        name: String?,
        petName: String?,
        email: String?,
        password: String?,
    ) {
        val data = hashMapOf(
            "name" to name,
            "petName" to petName,
            "email" to email,
            "usertype" to "Normal User"
        )
        firebaseAuth.createUserWithEmailAndPassword(email!!, password!!)
            .addOnCompleteListener(
            ) { task ->
                if (task.isSuccessful) {
                    userLiveData.postValue(firebaseAuth.currentUser)
                    responseLiveData.postValue(Response.Success())
                    firebaseDB.collection("User").document(firebaseAuth.currentUser!!.uid).set(data)
                        .addOnSuccessListener {
                            responseLiveData.postValue(Response.Success())
                        }.addOnFailureListener {
                            responseLiveData.postValue(Response.Failure(task.exception.toString()))
                        }
                    firebaseDB.collection("Normal User").document(firebaseAuth.currentUser!!.uid)
                        .set(data)
                        .addOnSuccessListener {
                            responseLiveData.postValue(Response.Success())
                        }.addOnFailureListener {
                            responseLiveData.postValue(Response.Failure(task.exception.toString()))
                        }
                } else {
                    responseLiveData.postValue(Response.Failure(task.exception.toString()))
                }
            }
    }

    fun logOut() {
        firebaseAuth.signOut()
        userLiveData.postValue(null)
    }


    fun userType(user: FirebaseUser) {
        firebaseDB.collection("User").document(user.uid).get()
            .addOnSuccessListener {
                if(it.exists()){
                    val userType = it.getString("usertype")
                    usertype.postValue(Usertype.Success(userType))
                }else usertype.postValue(Usertype.Failure("Data does not exist"))
            }
            .addOnFailureListener {
                usertype.postValue(Usertype.Failure(it.toString()))
            }
    }

    init {
        if (firebaseAuth.currentUser != null) {
            userLiveData.postValue(firebaseAuth.currentUser)
        } else {
            userLiveData.postValue(null)
        }
    }
}