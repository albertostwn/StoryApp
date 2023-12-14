package com.dicoding.picodiploma.loginwithanimation.view.adStory

import androidx.lifecycle.ViewModel
import com.dicoding.picodiploma.loginwithanimation.data.pref.UserPreference
import kotlinx.coroutines.flow.first

class AddStoryViewModel(private val pref: UserPreference) : ViewModel() {
    suspend fun getUserToken() = pref.getUserToken().first()
}