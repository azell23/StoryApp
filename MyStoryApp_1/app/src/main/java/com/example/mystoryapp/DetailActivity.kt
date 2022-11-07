package com.example.mystoryapp

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.mystoryapp.databinding.ActivityDetailBinding
import com.example.mystoryapp.response.ListStoryItem
import com.example.mystoryapp.util.Constanta
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        detailData()
    }
    private fun detailData(){
        val image = binding.ivDetailPost
        val name = binding.tvDetailName
        val desc = binding.tvDetailDesc
        val date = binding.tvDetailDate
        val detail = intent.getParcelableExtra<ListStoryItem>(Constanta.DETAIL) as ListStoryItem
        Glide.with(this)
            .load(detail.photoUrl)
            .into(image)
        name.text = detail.name
        desc.text = detail.description
        date.setLocalDate(detail.createdAt)

    }

    companion object{

        fun TextView.setLocalDate(timestamp: String){
            val simpleDate = SimpleDateFormat("yyyy-MM-dd", Locale.UK)
            val date = simpleDate.parse(timestamp) as Date
            val formatDate = DateFormat.getDateInstance(DateFormat.FULL).format(date)
            this.text = formatDate
        }
    }
}