package com.example.mystoryapp.util

import android.Manifest

class Constanta {
    companion object{
        const val TOKEN = "token_key"
        const val LOGIN = "auth_user_login"
        const val REQ_CODE = 23
        val REQ_PERMISSION = arrayOf(Manifest.permission.CAMERA)
        const val CAMERA_X_RESULT = 200
        const val SUCSSESS_UPLOAD = "success_upload"
        const val DETAIL = "detail"
        val emailPattern =  Regex("[a-zA-Z0-9._]+@[a-z]+\\.+[a-z]+")
    }
}