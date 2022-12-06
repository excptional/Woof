package com.example.woof.repo

import android.annotation.SuppressLint
import android.app.Application
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
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

    private val kennelLivedata = MutableLiveData<MutableList<DocumentSnapshot>>()
    val kennelData: LiveData<MutableList<DocumentSnapshot>>
        get() = kennelLivedata

    private val petShopLivedata = MutableLiveData<MutableList<DocumentSnapshot>>()
    val petShopData: LiveData<MutableList<DocumentSnapshot>>
        get() = petShopLivedata

    private val trainingCenterLivedata = MutableLiveData<MutableList<DocumentSnapshot>>()
    val trainingCenterData: LiveData<MutableList<DocumentSnapshot>>
        get() = trainingCenterLivedata

    private val groomingCenterLivedata = MutableLiveData<MutableList<DocumentSnapshot>>()
    val groomingCenterData: LiveData<MutableList<DocumentSnapshot>>
        get() = groomingCenterLivedata

    private val foodAndAccLivedata = MutableLiveData<MutableList<DocumentSnapshot>>()
    val foodAndAccData: LiveData<MutableList<DocumentSnapshot>>
        get() = foodAndAccLivedata

    private val medicineLivedata = MutableLiveData<MutableList<DocumentSnapshot>>()
    val medicineData: LiveData<MutableList<DocumentSnapshot>>
        get() = medicineLivedata

    private val bookingsLivedata = MutableLiveData<MutableList<DocumentSnapshot>>()
    val bookingsData: LiveData<MutableList<DocumentSnapshot>>
        get() = bookingsLivedata

    private val doctorLivedata = MutableLiveData<MutableList<DocumentSnapshot>>()
    val doctorData: LiveData<MutableList<DocumentSnapshot>>
        get() = doctorLivedata

    private val tradeLicUrlLivedata = MutableLiveData<String?>()
    val tradeLicUrl: LiveData<String?>
        get() = tradeLicUrlLivedata

    private val feedbackResultLiveData = MutableLiveData<String?>()
    val feedbackData: LiveData<String?>
        get() = feedbackResultLiveData


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
                    val list = mutableListOf<String>()
                    when (it.getString("Usertype")!!) {

                        "Normal User" -> {
                            list.add(it.getString("Usertype")!!)
                            list.add(it.getString("Name")!!)
                            list.add(it.getString("Image Url")!!)
                            list.add(it.getString("Phone No")!!)
                            list.add(it.getString("Pet Name")!!)
                            list.add(it.getString("Pet Species")!!)
                            list.add(it.getString("Pet Breed")!!)
                        }

                        "Seller" -> {

                            list.add(it.getString("Usertype")!!)
                            list.add(it.getString("Name")!!)
                            list.add(it.getString("Image Url")!!)
                            list.add(it.getString("Phone No")!!)
                            list.add(it.getString("Trade License Number")!!)

                        }

                        "Doctor" -> {

                            list.add(it.getString("Usertype")!!)
                            list.add(it.getString("Name")!!)
                            list.add(it.getString("Image Url")!!)
                            list.add(it.getString("Phone No")!!)
                            list.add(it.getString("Speciality")!!)
                            list.add(it.getString("Registration No")!!)

                        }
                    }
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
        firebaseDB.collection("Post").orderBy("Post ID", Query.Direction.DESCENDING).get()
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

    fun uploadTradeLicDoc(img: Uri) {
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

    private fun getErrorMassage(e: Exception): String {
        val colonIndex = e.toString().indexOf(":")
        return e.toString().substring(colonIndex + 2)
    }

    fun addTrainingCenter(count: Int) {
        val postData = hashMapOf(
            "Name" to "",
            "Phone Number" to "",
            "Address" to "",
            "Website" to "",
            "Ratings" to ""
        )
        firebaseDB.collection("Training Center").document("$count").set(postData)

    }

    fun addMedicines(count: Int) {
        val postData = hashMapOf(
            "Name" to "",
            "Image Url" to "",
            "Description" to "",
            "Price" to "",
            "Ratings" to ""
        )
        firebaseDB.collection("Medicines").document("$count").set(postData)

    }

    fun addGroomingCenter(count: Int) {
        val postData = hashMapOf(
            "Name" to "",
            "Phone Number" to "",
            "Address" to "",
            "Website" to "",
            "Ratings" to ""
        )
        firebaseDB.collection("Grooming Center").document("$count").set(postData)

    }

    fun addKennelsCenter(count: Int) {
        val postData = hashMapOf(
            "Name" to "",
            "Phone Number" to "",
            "Address" to "",
            "Website" to "",
            "Ratings" to ""
        )
        firebaseDB.collection("Kennel").document("$count").set(postData)
        firebaseDB.collection("Pet Shop").document("$count").set(postData)

    }

    fun addAccessories(count: Int) {
        val postData = hashMapOf(
            "Product Name" to "",
            "Product Image" to "",
            "Product Price" to "",
            "Product Rating" to "",
        )
        firebaseDB.collection("Food And Accessories").document("$count").set(postData)

    }

    fun fetchKennels() {
        firebaseDB.collection("Kennel").get()
            .addOnSuccessListener { documents ->
                val list = mutableListOf<DocumentSnapshot>()
                for (document in documents) {
                    list.add(document)
                }
                responseDBLivedata.postValue(Response.Success())
                kennelLivedata.postValue(list)
            }
            .addOnFailureListener {
                responseDBLivedata.postValue(Response.Failure(getErrorMassage(it)))
            }
    }

    fun fetchTrainingCenter() {
        firebaseDB.collection("Training Center").get()
            .addOnSuccessListener { documents ->
                val list = mutableListOf<DocumentSnapshot>()
                for (document in documents) {
                    list.add(document)
                }
                responseDBLivedata.postValue(Response.Success())
                trainingCenterLivedata.postValue(list)
            }
            .addOnFailureListener {
                responseDBLivedata.postValue(Response.Failure(getErrorMassage(it)))
            }
    }

    fun fetchGroomingCenter() {
        firebaseDB.collection("Grooming Center").get()
            .addOnSuccessListener { documents ->
                val list = mutableListOf<DocumentSnapshot>()
                for (document in documents) {
                    list.add(document)
                }
                responseDBLivedata.postValue(Response.Success())
                groomingCenterLivedata.postValue(list)
            }
            .addOnFailureListener {
                responseDBLivedata.postValue(Response.Failure(getErrorMassage(it)))
            }
    }

    fun fetchPetShop() {
        firebaseDB.collection("Pet Shop").get()
            .addOnSuccessListener { documents ->
                val list = mutableListOf<DocumentSnapshot>()
                for (document in documents) {
                    list.add(document)
                }
                responseDBLivedata.postValue(Response.Success())
                petShopLivedata.postValue(list)
            }
            .addOnFailureListener {
                responseDBLivedata.postValue(Response.Failure(getErrorMassage(it)))
            }
    }

    fun addOrder(userName: String, userNumber: String, userAddress: String,
                 userUID: String, productName: String, payableAmount: String, quantity: Int ){

        val date = SimpleDateFormat("yyyy_MM_dd_hh:mm:ss", Locale.getDefault()).format(Date())

        val data = mapOf(
            "user Name" to userName,
            "User UID" to userUID,
            "User Number" to userNumber,
            "User Address" to userAddress,
            "Quantity" to quantity,
            "Product Name" to productName,
            "Payable Amount" to payableAmount
        )

        firebaseDB.collection("Orders").document("order_$date").set(data)
            .addOnSuccessListener {
                responseDBLivedata.postValue(Response.Success())
            }
            .addOnFailureListener {
                responseDBLivedata.postValue(Response.Failure(getErrorMassage(it)))
            }

    }

    fun fetchFoodAndAcc() {
        firebaseDB.collection("Food And Accessories").get()
            .addOnSuccessListener { documents ->
                val list = mutableListOf<DocumentSnapshot>()
                for (document in documents) {
                    list.add(document)
                }
                responseDBLivedata.postValue(Response.Success())
                foodAndAccLivedata.postValue(list)
            }
            .addOnFailureListener {
                responseDBLivedata.postValue(Response.Failure(getErrorMassage(it)))
            }
    }

    fun fetchMedicine() {
        firebaseDB.collection("Medicines").get()
            .addOnSuccessListener { documents ->
                val list = mutableListOf<DocumentSnapshot>()
                for (document in documents) {
                    list.add(document)
                }
                responseDBLivedata.postValue(Response.Success())
                medicineLivedata.postValue(list)
            }
            .addOnFailureListener {
                responseDBLivedata.postValue(Response.Failure(getErrorMassage(it)))
            }
    }

    fun sendFeedback(email: String, subject: String, massage: String){
        val date = SimpleDateFormat("yyyy_MM_dd_hh:mm:ss", Locale.getDefault()).format(Date())
        val data = mapOf(
            "Email" to email,
            "Subject" to subject,
            "Massage" to massage,
            "Date" to date
        )
        firebaseDB.collection("Feedback").document(date).set(data).addOnSuccessListener{
            feedbackResultLiveData.postValue("Feedback send")
        }
            .addOnFailureListener {
                feedbackResultLiveData.postValue("Something went wrong!")
            }
    }

    fun getDoctorData() {
        firebaseDB.collection("Doctor").get()
            .addOnSuccessListener { documents ->
                val list = mutableListOf<DocumentSnapshot>()
                for (document in documents) {
                    list.add(document)
                }
                responseDBLivedata.postValue(Response.Success())
                doctorLivedata.postValue(list)
            }
            .addOnFailureListener {
                responseDBLivedata.postValue(Response.Failure(it.toString()))
            }
    }

    fun getBookingRequest(){
        firebaseDB.collection("Bookings").get()
            .addOnSuccessListener { documents ->
                val list = mutableListOf<DocumentSnapshot>()
                for (document in documents) {
                    list.add(document)
                }
                responseDBLivedata.postValue(Response.Success())
                bookingsLivedata.postValue(list)
            }
            .addOnFailureListener {
                responseDBLivedata.postValue(Response.Failure(it.toString()))
            }
    }

    fun bookDoctor(name: String, imageUrl: String, uid: String, date: String, timings: String, species: String, issue: String) {
        val data = mapOf(
            "User Name" to name,
            "User Image Url" to imageUrl,
            "User uid" to uid,
            "Timings" to timings,
            "Date" to date,
            "Species" to species,
            "Issue" to issue,
            "Statue" to "Pending"
        )
        val date2 = SimpleDateFormat("yyyy_MM_dd_hh:mm:ss", Locale.getDefault()).format(Date())
        firebaseDB.collection("Doctor Bookings").document(date2).set(data)
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