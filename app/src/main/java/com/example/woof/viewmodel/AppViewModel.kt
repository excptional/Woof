package com.example.woof.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.woof.repo.AppRepository
import com.example.woof.repo.Response
import com.google.firebase.auth.FirebaseUser

class AppViewModel(application: Application) :
    AndroidViewModel(application) {
    private val appRepository: AppRepository = AppRepository(application)
    val userdata: LiveData<FirebaseUser?>
        get() = appRepository.userData
    val response: LiveData<Response<String>>
        get() = appRepository.response
    val usertype: LiveData<String?>
        get() = appRepository.user

    fun login(email: String?, password: String?) {
        appRepository.login(email, password)
    }

    fun normalUserRegister(
        name: String?,
        petName: String?,
        phoneNo: String?,
        email: String?,
        password: String?
    ) {
        appRepository.normalUserRegister(name, petName, phoneNo, email, password)
    }

    fun sellerRegister(
        name: String?,
        tradeLicNo: String?,
        phoneNo: String?,
        email: String?,
        password: String?
    ) {
        appRepository.sellerRegister(name, tradeLicNo, phoneNo, email, password)
    }

    fun doctorRegister(
        name: String?,
        regNo: String?,
        phoneNo: String?,
        email: String?,
        password: String?
    ) {
        appRepository.doctorRegister(name, regNo, phoneNo, email, password)
    }

    fun logOut() {
        appRepository.logOut()
    }

    fun getUserType(user: FirebaseUser) {
        appRepository.userType(user)
    }

}