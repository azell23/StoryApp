package com.example.mystoryapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mystoryapp.adapter.Main
import com.example.mystoryapp.databinding.ActivityMainBinding
import com.example.mystoryapp.datastore.AuthPreference
import com.example.mystoryapp.response.ListStoryItem
import com.example.mystoryapp.util.PreferenceFactory
import com.example.mystoryapp.viewmodel.Auth
import com.example.mystoryapp.viewmodel.StoryModel
import java.util.*
import kotlin.concurrent.schedule

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var adapterMain : Main
    private lateinit var viewModel: StoryModel
    private lateinit var authPreference: AuthPreference
    private lateinit var authViewModel: Auth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        authPreference = AuthPreference(this)
        authViewModel = ViewModelProvider(this, PreferenceFactory(authPreference))[Auth::class.java]

        viewModel = ViewModelProvider(this)[StoryModel::class.java]
        viewModel._storyList.observe(this){ list ->
            setRecycle(list)
        }

        authViewModel.getToken().observe(this){
            if (it != null){
                viewModel.getStory("Bearer $it")
            }else {
                Log.d("TOKEN", "null")
            }
        }

        binding.refresh.setOnRefreshListener { onRefresh() }
        viewModel.loading.observe(this){
            loading(it)
        }
        upload()
    }
    private fun upload(){
        binding.fabUpload.setOnClickListener {
            startActivity(Intent(this, UploadActivity::class.java))
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }
    private fun setRecycle(listStory: List<ListStoryItem>){
        adapterMain = Main(listStory)
        binding.recyclerViewMain.apply {
            adapter = adapterMain
            layoutManager = LinearLayoutManager(this@MainActivity)
        }

    }


    private fun onRefresh(){
        binding.refresh.isRefreshing = true
        Timer().schedule(1500){

            binding.refresh.isRefreshing = false
        }
        binding.recyclerViewMain.smoothScrollBy(0,0)
        startActivity(Intent(this, MainActivity::class.java))
    }
    private fun loading(_loading: Boolean){
        binding.pb.visibility = if (_loading) View.VISIBLE else View.GONE
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.logout -> {
                val builder = AlertDialog.Builder(this)
                builder.setMessage("Do You want logout ?")
                builder.setPositiveButton("No"){dialog, _ ->
                    dialog.dismiss()
                }
                builder.setPositiveButton("Yes"){_, _ ->
                    this.getSharedPreferences("data",0).edit().clear()
                        .apply()
                    Intent(this, LoginActivity::class.java).also {
                        authViewModel.deleteToken()
                        startActivity(it)
                    }
                    finish()
                }
                val alert = builder.create()
                alert.show()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}