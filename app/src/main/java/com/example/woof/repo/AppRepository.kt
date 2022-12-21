package com.example.woof.repo

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class AppRepository(private val application: Application) {
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firebaseDB: FirebaseFirestore = FirebaseFirestore.getInstance()

    private val userLiveData = MutableLiveData<FirebaseUser?>()
    val userData: LiveData<FirebaseUser?>
        get() = userLiveData
    private val responseLiveData = MutableLiveData<Response<String>>()
    val response: LiveData<Response<String>>
        get() = responseLiveData
    private val usertype = MutableLiveData<String?>()
    val user: LiveData<String?>
        get() = usertype

    fun login(email: String?, password: String?) {
        firebaseAuth.signInWithEmailAndPassword(email!!, password!!)
            .addOnCompleteListener(
            ) { task ->
                if (task.isSuccessful) {
                    responseLiveData.postValue(Response.Success())
                    userLiveData.postValue(firebaseAuth.currentUser)
                } else {
                    responseLiveData.postValue(Response.Failure(getErrorMassage(task.exception!!)))
                }
            }
    }

    fun normalUserRegister(
        name: String?,
        petName: String?,
        phoneNo: String?,
        email: String?,
        password: String?,
        species: String?,
        breed: String?
    ) {
        val data = hashMapOf(
            "Name" to name,
            "Pet Name" to petName,
            "Phone No" to phoneNo,
            "Image Url" to "https://firebasestorage.googleapis.com/v0/b/woof-uit.appspot.com/o/user2.png?alt=media&token=6eb92a4c-a6e6-443b-9ca3-8e7d5a7cd7ef",
            "Email" to email,
            "Usertype" to "Normal User",
            "Pet Species" to species,
            "Pet Breed" to breed
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
                            responseLiveData.postValue(Response.Failure(getErrorMassage(task.exception!!)))
                        }
                    firebaseDB.collection("Normal User").document(firebaseAuth.currentUser!!.uid)
                        .set(data)
                        .addOnSuccessListener {
                            responseLiveData.postValue(Response.Success())
                        }.addOnFailureListener {
                            responseLiveData.postValue(Response.Failure(getErrorMassage(task.exception!!)))
                        }
                } else {
                    responseLiveData.postValue(Response.Failure(getErrorMassage(task.exception!!)))
                }
            }
    }

    fun sellerRegister(
        name: String?,
        tradeLicNo: String?,
        tradeLicDoc: String?,
        phoneNo: String?,
        email: String?,
        password: String?,
    ) {
        val data = hashMapOf(
            "Name" to name,
            "Trade License Number" to tradeLicNo,
            "Trade License Document Url" to tradeLicDoc,
            "Phone No" to phoneNo,
            "Image Url" to "https://firebasestorage.googleapis.com/v0/b/woof-uit.appspot.com/o/user2.png?alt=media&token=6eb92a4c-a6e6-443b-9ca3-8e7d5a7cd7ef",
            "Email" to email,
            "Usertype" to "Seller"
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
                            responseLiveData.postValue(Response.Failure(it.toString()))
                        }
                    firebaseDB.collection("Seller").document(firebaseAuth.currentUser!!.uid)
                        .set(data)
                        .addOnSuccessListener {
                            responseLiveData.postValue(Response.Success())
                        }.addOnFailureListener {
                            responseLiveData.postValue(Response.Failure(it.toString()))
                        }
                } else {
                    responseLiveData.postValue(Response.Failure(getErrorMassage(task.exception!!)))
                }
            }
    }

    fun doctorRegister(
        name: String,
        regNo: String,
        phoneNo: String,
        email: String,
        password: String,
        speciality: String
    ) {

        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(
            ) { task ->
                if (task.isSuccessful) {
                    val data = hashMapOf(
                        "ID" to firebaseAuth.currentUser!!.uid,
                        "Name" to name,
                        "Registration No" to regNo,
                        "Phone No" to phoneNo,
                        "Image Url" to "https://firebasestorage.googleapis.com/v0/b/woof-uit.appspot.com/o/user2.png?alt=media&token=6eb92a4c-a6e6-443b-9ca3-8e7d5a7cd7ef",
                        "Email" to email,
                        "Usertype" to "Doctor",
                        "Speciality" to speciality
                    )
                    userLiveData.postValue(firebaseAuth.currentUser)
                    responseLiveData.postValue(Response.Success())
                    firebaseDB.collection("User").document(firebaseAuth.currentUser!!.uid).set(data)
                        .addOnSuccessListener {
                            responseLiveData.postValue(Response.Success())
                        }.addOnFailureListener {
                            responseLiveData.postValue(Response.Failure(it.toString()))
                        }
                    firebaseDB.collection("Doctor").document(firebaseAuth.currentUser!!.uid)
                        .set(data)
                        .addOnSuccessListener {
                            responseLiveData.postValue(Response.Success())
                        }.addOnFailureListener {
                            responseLiveData.postValue(Response.Failure(it.toString()))
                        }
                } else {
                    responseLiveData.postValue(Response.Failure(getErrorMassage(task.exception!!)))
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
                if (it.exists()) {
                    val userType = it.getString("Usertype")
                    usertype.postValue(userType)
                } else usertype.postValue("Data does not exist")
            }
            .addOnFailureListener {
                usertype.postValue(it.toString())
            }
    }

    private fun getErrorMassage(e: Exception): String {
        val colonIndex = e.toString().indexOf(":")
        return e.toString().substring(colonIndex + 2)
    }

    init {
        if (firebaseAuth.currentUser != null) {
            userLiveData.postValue(firebaseAuth.currentUser)
        } else {
            userLiveData.postValue(null)
        }
    }
}