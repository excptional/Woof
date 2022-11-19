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
    val postData: LiveData<MutableList<DocumentSnapshot>>
        get() = dbRepository.postData
    val hospitalData: LiveData<MutableList<DocumentSnapshot>>
        get() = dbRepository.hospitalData
    val kennelData: LiveData<MutableList<DocumentSnapshot>>
        get() = dbRepository.kennelData
    val petShopData: LiveData<MutableList<DocumentSnapshot>>
        get() = dbRepository.petShopData
    val tradeLicUrl: LiveData<String?>
        get() = dbRepository.tradeLicUrl


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

    fun uploadTradeLicDoc(img: Uri){
        dbRepository.uploadTradeLicDoc(img)
    }

    fun addGroomingCenter(count: Int){
        dbRepository.addGroomingCenter(count)
    }

    fun addTrainingCenter(count: Int){
        dbRepository.addTrainingCenter(count)
    }

    fun addKennels(count: Int){
        dbRepository.addKennelsCenter(count)
    }

}