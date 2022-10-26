package com.example.woof.repo

sealed class Usertype(val str: String? = null){
    class Success(user: String?): Usertype(str = user)
    class Failure (e: String?): Usertype(str = e)
}
