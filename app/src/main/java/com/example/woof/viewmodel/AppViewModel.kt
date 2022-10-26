package com.example.woof.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.woof.repo.Response
import com.example.woof.repo.AppRepository
import com.example.woof.repo.Usertype
import com.google.firebase.auth.FirebaseUser

class AppViewModel(application: Application) :
    AndroidViewModel(application) {
    private val appRepository: AppRepository = AppRepository(application)
    val userdata: LiveData<FirebaseUser>
    get() = appRepository.userData
    val response: LiveData<Response<String>>
        get() = appRepository.response
    val responseDB: LiveData<Response<String>>
        get() = appRepository.responseDB
    val usertype: LiveData<Usertype>
    get() = appRepository.user

    fun login(email: String?, password: String?) {
        appRepository.login(email, password)
    }

    fun normalUserRegister(name: String?, petName: String?, email: String?, password: String?) {
        appRepository.normalUserRegister(name, petName, email, password)
    }

    fun logOut() {
        appRepository.logOut()
    }

    fun getUserType(user: FirebaseUser) {
        appRepository.userType(user)
    }

}