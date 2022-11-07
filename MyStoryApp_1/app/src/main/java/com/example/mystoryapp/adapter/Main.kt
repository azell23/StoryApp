package com.example.mystoryapp.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mystoryapp.DetailActivity
import com.example.mystoryapp.databinding.ItemMenuBinding
import com.example.mystoryapp.response.ListStoryItem
import com.example.mystoryapp.util.Constanta
import org.ocpsoft.prettytime.PrettyTime
import java.text.SimpleDateFormat
import java.util.*

class Main(var listStory : List<ListStoryItem>): RecyclerView.Adapter<Main.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemMenuBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(listItem: ListStoryItem) {
            val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
            sdf.timeZone = TimeZone.getTimeZone("ID")
            val time = sdf.parse(listItem.createdAt)?.time
            val pretty = PrettyTime(Locale.getDefault())
            val ago = pretty.format(time?.let { Date(it) })
            with(binding) {
                tvNama.text = listItem.name
                tvDesc.text = listItem.description
                tvTanggal.text= ago
                Glide.with(itemView)
                    .load(listItem.photoUrl)
                    .into(ivPost)
            }
            itemView.setOnClickListener {
                with(it.context){
                    startActivity(Intent(this, DetailActivity::class.java)
                        .putExtra(Constanta.DETAIL, listItem),
                        ActivityOptionsCompat.makeSceneTransitionAnimation(this as Activity,
                            Pair(binding.ivPost, "transition_photo"),
                            Pair(binding.tvNama, "transition_nama"),
                            Pair(binding.tvDesc, "transition_desc"),
                            Pair(binding.tvTanggal, "transition_tanggal")
                        ).toBundle()
                    )
                }
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemMenuBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val story = listStory[position]
        holder.bind(story)
    }

    override fun getItemCount(): Int = listStory.size
}

