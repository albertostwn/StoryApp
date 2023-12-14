package com.dicoding.picodiploma.loginwithanimation.view.login

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowInsets
import android.view.WindowManager
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.dicoding.picodiploma.loginwithanimation.ViewModelFactory
import com.dicoding.picodiploma.loginwithanimation.data.pref.UserPreference
import com.dicoding.picodiploma.loginwithanimation.databinding.ActivityCheckUserLoginBinding
import com.dicoding.picodiploma.loginwithanimation.view.main.MainActivity
import com.dicoding.picodiploma.loginwithanimation.view.main.MainViewModel

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class CheckUserLoginActivity : AppCompatActivity() {
    private lateinit var mainViewModel: MainViewModel
    private lateinit var binding: ActivityCheckUserLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckUserLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupViewModel()
        setupView()
        onAction()
    }

    private fun setupViewModel() {
        mainViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreference.getInstance(dataStore))
        )[MainViewModel::class.java]
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

    private fun onAction() {
        binding.apply {
            mainViewModel.getUserIsLogin().observe(this@CheckUserLoginActivity) {
                if (it) {
                    Intent(this@CheckUserLoginActivity, MainActivity::class.java).also { intent ->
                        startActivity(intent)
                        finish()
                    }
                } else {
                    progressBar.alpha = 1f
                    Thread {
                        val handler = Handler(Looper.getMainLooper())
                        var status = 0
                        while (status < 100) {
                            status += 2
                            try {
                                Thread.sleep(50)
                            } catch (e: InterruptedException) {
                                e.printStackTrace()
                            }
                            handler.post {
                                progressBar.progress = status
                                if (status == 100) {
                                    Intent(
                                        this@CheckUserLoginActivity,
                                        LoginActivity::class.java
                                    ).also { intent ->
                                        startActivity(intent)
                                        finishAffinity()
                                    }
                                }
                            }
                        }
                    }.start()
                }
            }
        }
    }
}