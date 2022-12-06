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

    fun giveLikes(user: FirebaseUser, id:  String?){
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

    fun uploadTradeLicDoc(img: Uri){
        dbRepository.uploadTradeLicDoc(img)
    }

    fun getBookingRequest() {
        dbRepository.getBookingRequest()
    }

    fun getDoctorData() {
        dbRepository.getDoctorData()
    }

    fun addGroomingCenter(count: Int){
        dbRepository.addGroomingCenter(count)
    }

    fun addMedicines(count: Int) {
        dbRepository.addMedicines(count)
    }

    fun addTrainingCenter(count: Int){
        dbRepository.addTrainingCenter(count)
    }

    fun addKennels(count: Int){
        dbRepository.addKennelsCenter(count)
    }

    fun addAccessories(count: Int){
        dbRepository.addAccessories(count)
    }

    fun sendFeedback(email: String, subject: String, massage: String){
        dbRepository.sendFeedback(email, subject, massage)
    }

    fun addOrder(userName: String, userNumber: String, userAddress: String,
                 userUID: String, productName: String, payableAmount: String, quantity: Int){
        dbRepository.addOrder(userName, userNumber, userAddress, userUID, productName, payableAmount, quantity)
    }

    fun bookDoctor(name: String, imageUrl: String, uid: String, date: String, timings: String, species: String, issue: String) {
        dbRepository.bookDoctor(name, imageUrl, uid, date, timings, species, issue)
    }

}