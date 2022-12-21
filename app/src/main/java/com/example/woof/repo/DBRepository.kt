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

    private val orderLivedata = MutableLiveData<MutableList<DocumentSnapshot>>()
    val orderData: LiveData<MutableList<DocumentSnapshot>>
        get() = orderLivedata

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

    fun addMedicines(uid: String, productName: String, productImage: Uri, productPrice: String, description: String) {
        val date = SimpleDateFormat("yyyy_MM_dd_hh:mm:ss", Locale.getDefault()).format(Date())
        val doc = firebaseStorage.reference.child("medicines/${productImage.lastPathSegment}")
        doc.putFile(productImage).addOnSuccessListener {
            doc.downloadUrl.addOnSuccessListener {
                val data = hashMapOf(
                    "Name" to productName,
                    "Image Url" to it.toString(),
                    "Price" to productPrice,
                    "Ratings" to "3.9",
                    "Seller ID" to uid,
                    "Description" to description
                )
                firebaseDB.collection("Medicines").document(date).set(data)
            }
        }
    }

    fun addGroomingCenter(name: String, number: String, address: String, city: String, website: String?) {
        val date = SimpleDateFormat("yyyy_MM_dd_hh:mm:ss", Locale.getDefault()).format(Date())
        val data = hashMapOf(
            "Name" to name,
            "Phone Number" to number,
            "Address" to address,
            "City" to city,
            "Website" to website,
            "Ratings" to "3.7"
        )
        firebaseDB.collection("Grooming Center").document("$name$date").set(data)

    }

    fun addTrainingCenter(name: String, number: String, address: String, city: String, website: String?) {
        val date = SimpleDateFormat("yyyy_MM_dd_hh:mm:ss", Locale.getDefault()).format(Date())
        val data = hashMapOf(
            "Name" to name,
            "Phone Number" to number,
            "Address" to address,
            "City" to city,
            "Website" to website,
            "Ratings" to "3.7"
        )
        firebaseDB.collection("Training Center").document("$name$date").set(data)
    }

    fun addKennels(name: String, number: String, address: String, city: String, website: String?) {
        val date = SimpleDateFormat("yyyy_MM_dd_hh:mm:ss", Locale.getDefault()).format(Date())
        val data = hashMapOf(
            "Name" to name,
            "Phone Number" to number,
            "Address" to address,
            "City" to city,
            "Website" to website,
            "Ratings" to "3.7"
        )
        firebaseDB.collection("Kennel").document("$name$date").set(data)
    }

    fun addPetShops(name: String, number: String, address: String, city: String, website: String?) {
        val date = SimpleDateFormat("yyyy_MM_dd_hh:mm:ss", Locale.getDefault()).format(Date())
        val data = hashMapOf(
            "Name" to name,
            "Phone Number" to number,
            "Address" to address,
            "City" to city,
            "Website" to website,
            "Ratings" to "3.7"
        )
        firebaseDB.collection("Pet Shop").document("$name$date").set(data)
    }

    fun addAccessories(uid: String, productName: String, productImage: Uri, productPrice: String) {
        val date = SimpleDateFormat("yyyy_MM_dd_hh:mm:ss", Locale.getDefault()).format(Date())
        val doc = firebaseStorage.reference.child("food and accessories/${productImage.lastPathSegment}")
        doc.putFile(productImage).addOnSuccessListener {
            doc.downloadUrl.addOnSuccessListener {
                val data = hashMapOf(
                    "Product Name" to productName,
                    "Product Image" to it.toString(),
                    "Product Price" to productPrice,
                    "Product Rating" to "3.9",
                    "Seller ID" to uid
                )
                firebaseDB.collection("Food And Accessories").document(date).set(data)
            }
        }
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

    fun addOrder(
        sellerID: String, userName: String, userNumber: String, userAddress: String,
        userUID: String, productName: String, productImageUrl: String, payableAmount: String, quantity: Int
    ) {
        val bookingID = UUID.randomUUID()
        var orderID = "${bookingID.leastSignificantBits}${bookingID.mostSignificantBits}"
        orderID = orderID.replace("-", "")
        val date = SimpleDateFormat("yyyy_MM_dd_hh:mm:ss", Locale.getDefault()).format(Date())

        val data = mapOf(
            "Order ID" to orderID,
            "User Name" to userName,
            "User UID" to userUID,
            "User Number" to userNumber,
            "User Address" to userAddress,
            "Quantity" to quantity,
            "Product Name" to productName,
            "Payable Amount" to payableAmount,
            "Product Image Url" to productImageUrl,
            "Delivery Date" to "NA",
            "Seller ID" to sellerID
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

    fun sendFeedback(email: String, subject: String, massage: String) {
        val date = SimpleDateFormat("yyyy_MM_dd_hh:mm:ss", Locale.getDefault()).format(Date())
        val data = mapOf(
            "Email" to email,
            "Subject" to subject,
            "Massage" to massage,
            "Date" to date
        )
        firebaseDB.collection("Feedback").document(date).set(data).addOnSuccessListener {
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
                responseDBLivedata.postValue(Response.Failure(getErrorMassage(it)))
            }
    }

    fun getBookingRequest() {
        firebaseDB.collection("Doctor Bookings").get()
            .addOnSuccessListener { documents ->
                val list = mutableListOf<DocumentSnapshot>()
                for (document in documents) {
                    list.add(document)
                }
                responseDBLivedata.postValue(Response.Success())
                bookingsLivedata.postValue(list)
            }
            .addOnFailureListener {
                responseDBLivedata.postValue(Response.Failure(getErrorMassage(it)))
            }
    }

    fun updateStatus(id: String, status: String) {
        val doc = firebaseDB.collection("Doctor Bookings").document(id)
        doc.get().addOnSuccessListener {
            doc.update("Status", status)
        }
    }

    fun bookDoctor(
        userName: String,
        userImageUrl: String,
        userUid: String,
        docName: String,
        docImageUrl: String,
        docUid: String,
        date: String,
        time: String,
        docSpeciality: String,
        species: String,
        issue: String
    ) {
        val date2 = SimpleDateFormat("yyyy_MM_dd_hh:mm:ss", Locale.getDefault()).format(Date())
        val bookingID = UUID.randomUUID()
        val bId = "${bookingID.leastSignificantBits}${bookingID.mostSignificantBits}"
        val uID = bId.replace("-", "")
        val data = mapOf(
            "Booking ID" to uID,
            "User Name" to userName,
            "User Image Url" to userImageUrl,
            "User Uid" to userUid,
            "Doctor UID" to docUid,
            "Doctor Name" to docName,
            "Doctor Image Url" to docImageUrl,
            "Doctor Speciality" to docSpeciality,
            "Timings" to time,
            "Date" to date,
            "Species" to species,
            "Issue" to issue,
            "Status" to "Pending",
            "ID" to date2
        )
        firebaseDB.collection("Doctor Bookings").document(date2).set(data)
        firebaseDB.collection("My Appointments").document("ApCh5GTcw84qeUN9pCAS").collection(userUid).document(date2)
            .set(data)
    }

    fun fetchOrder(){
        firebaseDB.collection("Orders").get()
            .addOnSuccessListener { documents ->
                val list = mutableListOf<DocumentSnapshot>()
                for (document in documents) {
                    list.add(document)
                }
                responseDBLivedata.postValue(Response.Success())
                orderLivedata.postValue(list)
            }
            .addOnFailureListener {
                responseDBLivedata.postValue(Response.Failure(getErrorMassage(it)))
            }
    }

}