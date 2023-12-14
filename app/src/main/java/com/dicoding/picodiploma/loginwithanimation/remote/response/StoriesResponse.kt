package com.dicoding.picodiploma.loginwithanimation.remote.response

import com.dicoding.picodiploma.loginwithanimation.view.main.StoryModel
import com.google.gson.annotations.SerializedName

data class StoriesResponse(

	@field:SerializedName("listStory")
	var listStory: MutableList<StoryModel>,

	@field:SerializedName("error")
	val error: Boolean,

	@field:SerializedName("message")
	val message: String
)