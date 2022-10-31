package com.example.woof.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.woof.repo.DBRepository
import com.example.woof.repo.Response
import com.google.firebase.auth.FirebaseUser

class DBViewModel(application: Application) :
    AndroidViewModel(application) {
    private val dbRepository: DBRepository = DBRepository(application)
    val profileData: LiveData<MutableList<String?>>
        get() = dbRepository.userProfileData
    val dbLiveData: LiveData<Response<String>>
        get() = dbRepository.responseDB

    fun uploadImageToStorage(imageUri: Uri, user: FirebaseUser){
        dbRepository.uploadImageToStorage(imageUri, user)
    }

    fun getProfileData(user: FirebaseUser) {
        dbRepository.getProfileData(user)
    }

    fun changeProfileData(user: FirebaseUser, field: String, newData: String){
        dbRepository.changeProfileData(user, field, newData)
    }
}