package com.dicoding.picodiploma.loginwithanimation

import androidx.recyclerview.widget.DiffUtil
import com.dicoding.picodiploma.loginwithanimation.view.main.StoryModel

class DivStoriesCallback (private val oldStories: List<StoryModel>, private val newStories: List<StoryModel>): DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldStories.size

    override fun getNewListSize(): Int = newStories.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldStories[oldItemPosition].id == newStories[newItemPosition].id

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldStories[oldItemPosition].id == newStories[newItemPosition].id
}