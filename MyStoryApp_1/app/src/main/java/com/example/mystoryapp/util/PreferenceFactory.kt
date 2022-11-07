package com.example.mystoryapp.util

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.mystoryapp.datastore.AuthPreference
import com.example.mystoryapp.viewmodel.Auth

class PreferenceFactory(private val authPreference: AuthPreference): ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(Auth::class.java)){
            return Auth(authPreference) as T
        }
        throw IllegalArgumentException("note 8 ${modelClass.name}")
    }


}