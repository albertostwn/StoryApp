package com.dicoding.picodiploma.loginwithanimation.view.detail

import android.annotation.SuppressLint
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import com.dicoding.picodiploma.loginwithanimation.databinding.ActivityDetailBinding
import com.dicoding.picodiploma.loginwithanimation.loadImage
import com.dicoding.picodiploma.loginwithanimation.view.main.StoryModel

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getStory()
        setupView()
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    @SuppressLint("SetTextI18n")
    private fun getStory() {
        binding.apply {
            val story = intent?.extras?.getParcelable<StoryModel>(DATA_STORY)

            imgPoster.loadImage(story?.photoUrl.toString())
            tvName.text = story?.name.toString()
            tvDesc.text = story?.description.toString()
        }
    }

    companion object {
        const val DATA_STORY = "data_story"
    }
}