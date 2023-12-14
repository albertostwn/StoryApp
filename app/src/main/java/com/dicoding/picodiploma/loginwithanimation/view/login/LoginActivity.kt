package com.dicoding.picodiploma.loginwithanimation.view.login

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.dicoding.picodiploma.loginwithanimation.R
import com.dicoding.picodiploma.loginwithanimation.ViewModelFactory
import com.dicoding.picodiploma.loginwithanimation.data.pref.UserPreference
import com.dicoding.picodiploma.loginwithanimation.databinding.ActivityLoginBinding
import com.dicoding.picodiploma.loginwithanimation.disable
import com.dicoding.picodiploma.loginwithanimation.enable
import com.dicoding.picodiploma.loginwithanimation.hideSoftKeyboard
import com.dicoding.picodiploma.loginwithanimation.remote.ApiConfig
import com.dicoding.picodiploma.loginwithanimation.remote.response.LoginResponse
import com.dicoding.picodiploma.loginwithanimation.showAlertLoading
import com.dicoding.picodiploma.loginwithanimation.view.main.MainActivity
import com.dicoding.picodiploma.loginwithanimation.view.signup.SignupActivity
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.Observable
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class LoginActivity : AppCompatActivity() {
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var binding: ActivityLoginBinding
    private lateinit var loading: AlertDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loading = showAlertLoading(this)
        setupViewModel()
        setupView()
        onAction()
        processedLogin()
    }

    private fun setupViewModel() {
        loginViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreference.getInstance(dataStore))
        )[LoginViewModel::class.java]
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

    @SuppressLint("CheckResult")
    private fun processedLogin() {
        binding.apply {
            val emailStream = RxTextView.textChanges(emailEditText)
                .skipInitialValue()
                .map {
                    emailEditText.error != null
                }

            val passwordStream = RxTextView.textChanges(passwordEditText)
                .skipInitialValue()
                .map {
                    passwordEditText.error != null
                }

            val invalidFieldStream = Observable.combineLatest(
                emailStream,
                passwordStream
            ) { emailInvalid, passwordInvalid ->
                !emailInvalid && !passwordInvalid
            }

            invalidFieldStream.subscribe { isValid ->
                if (isValid) loginButton.enable() else loginButton.disable()
            }

            loginButton.setOnClickListener {
                if (validate()) {
                    loading.show()
                    login()
                }
            }

        }
    }

    private fun login() {
        hideSoftKeyboard(this@LoginActivity, binding.root)
        val email = binding.emailEditText.text?.trim().toString()
        val password = binding.passwordTextView.text?.trim().toString()
        val service = ApiConfig().getApiService().postLogin(email, password)
        service.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null && !responseBody.error) {
                        loginViewModel.saveUserToken(responseBody.loginResult.token)
                        loginViewModel.saveUserSession(true)
                        Intent(this@LoginActivity, MainActivity::class.java).also { intent ->
                            startActivity(intent)
                            finishAffinity()
                        }
                    }
                    loading.dismiss()
                } else {
                    Toast.makeText(
                        this@LoginActivity,
                        getString(R.string.login_failed),
                        Toast.LENGTH_SHORT
                    ).show()
                    loading.dismiss()
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Toast.makeText(
                    this@LoginActivity,
                    getString(R.string.network_unavailable),
                    Toast.LENGTH_SHORT
                )
                    .show()
                loading.dismiss()
            }
        })
    }

    private fun validate(): Boolean {
        val valid: Boolean?
        val email = binding.emailEditText.text?.trim().toString()
        val password = binding.passwordEditText.text?.trim().toString()
        when {
            email.isEmpty() -> {
                binding.emailEditTextLayout.error = getString(R.string.enter_your_email)
                valid = java.lang.Boolean.FALSE
            }
            password.isEmpty() -> {
                binding.passwordEditTextLayout.error = getString(R.string.enter_your_password)
                valid = java.lang.Boolean.FALSE
            }
            else -> {
                valid = java.lang.Boolean.TRUE
                binding.emailEditTextLayout.error = null
                binding.passwordEditTextLayout.error = null
            }
        }
        return valid
    }

    private fun onAction() {
        binding.apply {
            loginButton.setOnClickListener {
                Intent(this@LoginActivity, SignupActivity::class.java).also { intent ->
                    startActivity(intent)
                }
            }
        }
    }

    override fun onBackPressed() {
        val finish = Intent(Intent.ACTION_MAIN)
        finish.addCategory(Intent.CATEGORY_HOME)
        finish.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(finish)
    }
}