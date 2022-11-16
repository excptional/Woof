package com.example.woof.repo

import android.annotation.SuppressLint
import android.app.Application
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.*


@Suppress("UNUSED_CHANGED_VALUE")
class DBRepository(private val application: Application) {

    private val firebaseStorage = FirebaseStorage.getInstance()
    private val firebaseDB = FirebaseFirestore.getInstance()

    private val responseDBLivedata = MutableLiveData<Response<String>>()
    val responseDB: LiveData<Response<String>>
        get() = responseDBLivedata

    private val userProfileLivedata = MutableLiveData<MutableList<String?>>()
    val userProfileData: LiveData<MutableList<String?>>
        get() = userProfileLivedata

    private val postLivedata = MutableLiveData<MutableList<DocumentSnapshot>>()
    val postData: LiveData<MutableList<DocumentSnapshot>>
        get() = postLivedata

    private val hospitalLivedata = MutableLiveData<MutableList<DocumentSnapshot>>()
    val hospitalData: LiveData<MutableList<DocumentSnapshot>>
        get() = hospitalLivedata

    private val tradeLicUrlLivedata = MutableLiveData<String?>()
    val tradeLicUrl: LiveData<String?>
        get() = tradeLicUrlLivedata



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
                        responseDBLivedata.postValue(Response.Failure(getErrorMassage(it)))
                    }
            }
            .addOnFailureListener {
                responseDBLivedata.postValue(Response.Failure(getErrorMassage(it)))
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
                    responseDBLivedata.postValue(Response.Failure(getErrorMassage(it)))
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
                        responseDBLivedata.postValue(Response.Failure(getErrorMassage(it)))
                    }
            } else {
                responseDBLivedata.postValue(Response.Failure("Document not exist"))
            }
        }
            .addOnFailureListener {
                responseDBLivedata.postValue(Response.Failure(getErrorMassage(it)))
            }
    }

    @SuppressLint("SimpleDateFormat")
    fun uploadPostWithImage(
        user: FirebaseUser,
        userName: String,
        userImageUrl: String,
        description: String?,
        imageUri: Uri?
    ) {
        val dateAndTime =
            SimpleDateFormat("MM/dd/yyyy hh:mm:ss aa", Locale.getDefault()).format(Date())
        val date = SimpleDateFormat("yyyy_MM_dd_hh:mm:ss", Locale.getDefault()).format(Date())
        val ref = firebaseStorage.reference.child("post/${user.uid}/${imageUri!!.lastPathSegment}")
        ref.putFile(imageUri)
            .addOnSuccessListener {
                ref.downloadUrl
                    .addOnSuccessListener { uri ->
                        val postData = hashMapOf(
                            "Post ID" to "post_$date",
                            "Profile Name" to userName,
                            "Profile Image Url" to userImageUrl,
                            "Description" to description,
                            "Content Url" to uri.toString(),
                            "No Of Likes" to "0",
                            "List Of Reactors" to "",
                            "Upload Date" to dateAndTime
                        )
                        responseDBLivedata.postValue(Response.Success())
                        firebaseDB.collection("Post").document("post_$date").set(postData)
                            .addOnSuccessListener {
                                responseDBLivedata.postValue(Response.Success())
                                fetchPost()
                            }
                            .addOnFailureListener {
                                responseDBLivedata.postValue(Response.Failure(getErrorMassage(it)))
                            }
                    }
                    .addOnFailureListener {
                        responseDBLivedata.postValue(Response.Failure(getErrorMassage(it)))
                    }
            }
            .addOnFailureListener {
                responseDBLivedata.postValue(Response.Failure(getErrorMassage(it)))
            }

    }

    fun uploadPostWithOutImage(
        user: FirebaseUser,
        userName: String,
        userImageUrl: String,
        description: String?,
    ) {
        val dateAndTime =
            SimpleDateFormat("MM/dd/yyyy hh:mm:ss aa", Locale.getDefault()).format(Date())
        val date = SimpleDateFormat("yyyy_MM_dd_hh:mm:ss", Locale.getDefault()).format(Date())

        val postData = hashMapOf(
            "Post ID" to "post_$date",
            "Profile Name" to userName,
            "Profile Image Url" to userImageUrl,
            "Description" to description,
            "Content Url" to null,
            "No Of Likes" to "0",
            "List Of Reactors" to "",
            "Upload Date" to dateAndTime
        )
        firebaseDB.collection("Post").document("post_$date").set(postData)
            .addOnSuccessListener {
                responseDBLivedata.postValue(Response.Success())
                fetchPost()
            }
            .addOnFailureListener {
                responseDBLivedata.postValue(Response.Failure(getErrorMassage(it)))
            }
    }

    fun fetchPost() {
        firebaseDB.collection("Post").get()
            .addOnSuccessListener { documents ->
                val list = mutableListOf<DocumentSnapshot>()
                for (document in documents) {
                    list.add(document)
                }
                responseDBLivedata.postValue(Response.Success())
                postLivedata.postValue(list)
            }
            .addOnFailureListener {
                responseDBLivedata.postValue(Response.Failure(getErrorMassage(it)))
            }
    }

    @SuppressLint("SuspiciousIndentation")
    fun giveLikes(user: FirebaseUser, id: String?) {
        val doc = firebaseDB.collection("Post").document(id!!)
        doc.get()
            .addOnSuccessListener {
                var likes: Int = Integer.parseInt(it.getString("No Of Likes")!!)
                var likeList = it.getString("List Of Reactors")
                if (likeList!!.contains(user.uid)) {
                    likeList = likeList.replace(user.uid, "")
                    likes--
                } else {
                    likeList += user.uid + " "
                    likes++
                }
                doc.update("No Of Likes", "$likes")
                doc.update("List Of Reactors", likeList)
            }
    }

    fun fetchHospitals() {
        firebaseDB.collection("Hospital").get()
            .addOnSuccessListener { documents ->
                val list = mutableListOf<DocumentSnapshot>()
                for (document in documents) {
                    list.add(document)
                }
                responseDBLivedata.postValue(Response.Success())
                hospitalLivedata.postValue(list)
            }
            .addOnFailureListener {
                responseDBLivedata.postValue(Response.Failure(getErrorMassage(it)))
            }
    }

    fun uploadTradeLicDoc(img: Uri){
        val ref = firebaseStorage.reference.child("tradeLic/${img.lastPathSegment}")
        ref.putFile(img).addOnSuccessListener {
            ref.downloadUrl.addOnSuccessListener { uri ->
                responseDBLivedata.postValue(Response.Success())
                tradeLicUrlLivedata.postValue(uri.toString())
            }
                .addOnFailureListener {
                    responseDBLivedata.postValue(Response.Failure(getErrorMassage(it)))
                }
        }
            .addOnFailureListener {
                responseDBLivedata.postValue(Response.Failure(getErrorMassage(it)))
            }
    }

    private fun getErrorMassage(e: Exception): String{
        val colonIndex = e.toString().indexOf(":")
        return e.toString().substring(colonIndex + 2)
    }

    fun addTrainingCenter(count: Int){
        val postData = hashMapOf(
            "Name" to "",
            "Phone Number" to "",
            "Address" to "",
            "Website" to "",
            "Ratings" to ""
        )
        firebaseDB.collection("Training Center").document("$count").set(postData)

    }

    fun addGroomingCenter(count: Int){
        val postData = hashMapOf(
            "Name" to "",
            "Phone Number" to "",
            "Address" to "",
            "Website" to "",
            "Ratings" to ""
        )
        firebaseDB.collection("Grooming Center").document("$count").set(postData)

    }

    fun addKennelsCenter(count: Int){
        val postData = hashMapOf(
            "Name" to "",
            "Phone Number" to "",
            "Address" to "",
            "Website" to "",
            "Ratings" to ""
        )
        firebaseDB.collection("Kennel And Pet Shop").document("$count").set(postData)

    }

//    fun getLikeList(user: FirebaseUser, id: String?) {
//        val doc = firebaseDB.collection("Post").document(id!!)
//        doc.get()
//            .addOnSuccessListener {
//                val likeList = it.getString("List Of Reactors")
//                if (likeList!!.contains(user.uid)) isLikeLiveData.postValue("true")
//                else isLikeLiveData.postValue("false")
//            }
//    }
}