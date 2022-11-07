package com.example.mystoryapp.viewmodel

import androidx.lifecycle.*
import com.example.mystoryapp.api.Config
import com.example.mystoryapp.datastore.AuthPreference
import com.example.mystoryapp.response.Login
import com.example.mystoryapp.response.LoginResult
import com.example.mystoryapp.response.Register
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Auth(private val authPreference: AuthPreference): ViewModel() {

    private val _Login = MutableLiveData<LoginResult>()
    val login : LiveData<LoginResult> = _Login
    private val _register = MutableLiveData<Register>()
    val register : LiveData<Register> = _register

    fun Login(email : String, password: String){
        Config.getApiService().login(email, password)
            .enqueue(object : Callback<Login>{
                override fun onResponse(call: Call<Login>, response: Response<Login>) {
                    if (response.isSuccessful){
                        response.body().let { loginResult ->
                            loginResult?.loginResult?.let {
                                _Login.value = LoginResult(it.name, it.userId, it.token)
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<Login>, t: Throwable) {
                }

            })
    }

    fun Register(name: String, email:String, password: String){
        Config.getApiService().register(name, email, password)
            .enqueue(object : Callback<Register>{
                override fun onResponse(call: Call<Register>, response: Response<Register>) {
                    if (response.isSuccessful){
                        _register.postValue(response.body())
                    }
                }

                override fun onFailure(call: Call<Register>, t: Throwable) {
                }

            })
    }

    fun setToken(token: String){
        viewModelScope.launch {
            authPreference.setToken(token)
        }
    }
    fun deleteToken(){
        viewModelScope.launch {
            authPreference.deleteToken()
        }
    }
    fun getToken() :LiveData<String> = authPreference.getToken().asLiveData()



}