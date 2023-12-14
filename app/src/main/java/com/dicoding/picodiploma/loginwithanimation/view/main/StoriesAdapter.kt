package com.dicoding.picodiploma.loginwithanimation.view.main

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.picodiploma.loginwithanimation.DivStoriesCallback
import com.dicoding.picodiploma.loginwithanimation.R
import com.dicoding.picodiploma.loginwithanimation.databinding.ItemListStoryBinding
import com.dicoding.picodiploma.loginwithanimation.getTimeLineUploaded
import com.dicoding.picodiploma.loginwithanimation.loadImage

class StoriesAdapter : RecyclerView.Adapter<StoriesAdapter.ViewHolder>() {
    private var listener: ((StoryModel, ItemListStoryBinding) -> Unit)? = null
    var stories = mutableListOf<StoryModel>()
        set(value) {
            val callback = DivStoriesCallback(field, value)
            val result = DiffUtil.calculateDiff(callback)
            field.clear()
            field.addAll(value)
            result.dispatchUpdatesTo(this)
        }

    inner class ViewHolder(private val binding: ItemListStoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(story: StoryModel) {
            binding.apply {
                tvName.text = story.name
                tvUploadTimeStory.text =
                    "Waktu ${itemView.context.getString(R.string.text_uploaded)} ${
                        getTimeLineUploaded(
                            itemView.context,
                            story.createdAt
                        )
                    }"
                imgPoster.loadImage(story.photoUrl)
                listener?.let {
                    itemView.setOnClickListener {
                        it(story, binding)
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemListStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(stories[position])
    }

    override fun getItemCount(): Int = stories.size

    fun onClick(listener: ((StoryModel, ItemListStoryBinding) -> Unit)?) {
        this.listener = listener
    }
}