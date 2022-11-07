package com.example.mystoryapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.mystoryapp.datastore.AuthPreference
import com.example.mystoryapp.util.PreferenceFactory
import com.example.mystoryapp.viewmodel.Auth

class SplashActivity : AppCompatActivity() {

    private lateinit var authPreference: AuthPreference
    private lateinit var viewModel: Auth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        authPreference = AuthPreference(this)
        viewModel = ViewModelProvider(this, PreferenceFactory(authPreference))[Auth::class.java]
        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({
            token()
        }, 1000)
    }

    private fun token() {

        viewModel.getToken().observe(this){
            if (it != null){
                if (!it.equals("notfound")){
                    startActivity(Intent(this, MainActivity::class.java))
                } else {
                    startActivity(Intent(this, LoginActivity::class.java))
                }
            } else {
                Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
            }
        }
    }
}