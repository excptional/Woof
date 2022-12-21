package com.example.woof.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.woof.repo.DBRepository
import com.example.woof.repo.Response
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentSnapshot

class DBViewModel(application: Application) :
    AndroidViewModel(application) {
    private val dbRepository: DBRepository = DBRepository(application)
    val profileData: LiveData<MutableList<String?>>
        get() = dbRepository.userProfileData
    val dbLiveData: LiveData<Response<String>>
        get() = dbRepository.responseDB
    val tradeLicUrl: LiveData<String?>
        get() = dbRepository.tradeLicUrl
    val postData: LiveData<MutableList<DocumentSnapshot>>
        get() = dbRepository.postData
    val hospitalData: LiveData<MutableList<DocumentSnapshot>>
        get() = dbRepository.hospitalData
    val kennelData: LiveData<MutableList<DocumentSnapshot>>
        get() = dbRepository.kennelData
    val petShopData: LiveData<MutableList<DocumentSnapshot>>
        get() = dbRepository.petShopData
    val trainingCenterData: LiveData<MutableList<DocumentSnapshot>>
        get() = dbRepository.trainingCenterData
    val groomingCenterData: LiveData<MutableList<DocumentSnapshot>>
        get() = dbRepository.groomingCenterData
    val foodAndAccData: LiveData<MutableList<DocumentSnapshot>>
        get() = dbRepository.foodAndAccData
    val medicineData: LiveData<MutableList<DocumentSnapshot>>
        get() = dbRepository.medicineData
    val bookingsData: LiveData<MutableList<DocumentSnapshot>>
        get() = dbRepository.bookingsData
    val doctorData: LiveData<MutableList<DocumentSnapshot>>
        get() = dbRepository.doctorData
    val orderData: LiveData<MutableList<DocumentSnapshot>>
        get() = dbRepository.orderData
    val feedbackData: LiveData<String?>
        get() = dbRepository.feedbackData


    fun uploadImageToStorage(imageUri: Uri, user: FirebaseUser) {
        dbRepository.uploadImageToStorage(imageUri, user)
    }

    fun getProfileData(user: FirebaseUser) {
        dbRepository.getProfileData(user)
    }

    fun changeProfileData(user: FirebaseUser, field: String, newData: String) {
        dbRepository.changeProfileData(user, field, newData)
    }

    fun uploadPostWithImage(
        user: FirebaseUser,
        userName: String,
        userImageUrl: String,
        description: String,
        imageUri: Uri?
    ) {
        dbRepository.uploadPostWithImage(user, userName, userImageUrl, description, imageUri!!)
    }

    fun uploadPostWithOutImage(
        user: FirebaseUser,
        userName: String,
        userImageUrl: String,
        description: String,
    ) {
        dbRepository.uploadPostWithOutImage(user, userName, userImageUrl, description)
    }

    fun fetchPost() {
        dbRepository.fetchPost()
    }

    fun giveLikes(user: FirebaseUser, id: String?) {
        dbRepository.giveLikes(user, id)
    }

    fun fetchHospitals() {
        dbRepository.fetchHospitals()
    }

    fun fetchPetShop() {
        dbRepository.fetchPetShop()
    }

    fun fetchKennels() {
        dbRepository.fetchKennels()
    }

    fun fetchTrainingCenter() {
        dbRepository.fetchTrainingCenter()
    }

    fun fetchGroomingCenter() {
        dbRepository.fetchGroomingCenter()
    }

    fun fetchFoodAndAcc() {
        dbRepository.fetchFoodAndAcc()
    }

    fun fetchMedicine() {
        dbRepository.fetchMedicine()
    }

    fun uploadTradeLicDoc(img: Uri) {
        dbRepository.uploadTradeLicDoc(img)
    }

    fun getBookingRequest() {
        dbRepository.getBookingRequest()
    }

    fun getDoctorData() {
        dbRepository.getDoctorData()
    }

    fun addGroomingCenter(name: String, number: String, address: String, city: String, website: String?) {
        dbRepository.addGroomingCenter(name, number, address, city, website)
    }

    fun addMedicines(uid: String, productName: String, productImage: Uri, productPrice: String, description: String) {
        dbRepository.addMedicines(uid, productName, productImage, productPrice, description)
    }

    fun addTrainingCenter(name: String, number: String, address: String, city: String, website: String?) {
        dbRepository.addTrainingCenter(name, number, address, city, website)
    }

    fun addKennels(name: String, number: String, address: String, city: String, website: String?) {
        dbRepository.addKennels(name, number, address, city, website)
    }

    fun addPetShops(name: String, number: String, address: String, city: String, website: String?) {
        dbRepository.addPetShops(name, number, address, city, website)
    }

    fun addAccessories(uid: String, productName: String, productImage: Uri, productPrice: String) {
        dbRepository.addAccessories(uid, productName, productImage, productPrice)
    }

    fun fetchOrders() {
        dbRepository.fetchOrder()
    }

    fun sendFeedback(email: String, subject: String, massage: String) {
        dbRepository.sendFeedback(email, subject, massage)
    }

    fun addOrder(
        sellerID: String, userName: String, userNumber: String, userAddress: String,
        userUID: String, productName: String, productImageUrl: String, payableAmount: String, quantity: Int
    ) {
        dbRepository.addOrder(
            sellerID,
            userName,
            userNumber,
            userAddress,
            userUID,
            productName,
            productImageUrl,
            payableAmount,
            quantity
        )
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
        dbRepository.bookDoctor(
            userName,
            userImageUrl,
            userUid,
            docName,
            docImageUrl,
            docUid,
            date,
            time,
            docSpeciality,
            species,
            issue
        )
    }

    fun updateStatus(id: String, status: String) {
        dbRepository.updateStatus(id, status)
    }

}