package com.example.mystoryapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.mystoryapp.databinding.ActivityLoginBinding
import com.example.mystoryapp.datastore.AuthPreference
import com.example.mystoryapp.util.Constanta
import com.example.mystoryapp.util.PreferenceFactory
import com.example.mystoryapp.viewmodel.Auth

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel: Auth
    private lateinit var authPreference: AuthPreference
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        authPreference = AuthPreference(this)

        viewModel = ViewModelProvider(this, PreferenceFactory(authPreference))[Auth::class.java]



        Login()
        binding.btnRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

    }

    private fun Login(){
        binding.btnLogin.setOnClickListener{
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            when {
                !email.matches(Constanta.emailPattern) -> {
                    Toast.makeText(this, "Format email salah", Toast.LENGTH_SHORT).show()
                }
                email.isBlank() or password.isBlank() -> {
                    Toast.makeText(this, "Isi email dan password", Toast.LENGTH_SHORT).show()
                } else -> {
                    viewModel.Login(email, password)
                    viewModel.login.observe(this){
                        viewModel.setToken(it.token)
                        startActivity(Intent(this, MainActivity::class.java))
                        Toast.makeText(this, "Selamat datang di Story App ${it.name}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}