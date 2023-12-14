package com.dicoding.picodiploma.loginwithanimation.view.main

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Pair
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.picodiploma.loginwithanimation.R
import com.dicoding.picodiploma.loginwithanimation.data.pref.UserPreference
import com.dicoding.picodiploma.loginwithanimation.databinding.ActivityMainBinding
import com.dicoding.picodiploma.loginwithanimation.remote.ApiConfig
import com.dicoding.picodiploma.loginwithanimation.remote.response.StoriesResponse
import com.dicoding.picodiploma.loginwithanimation.showToast
import com.dicoding.picodiploma.loginwithanimation.ViewModelFactory
import com.dicoding.picodiploma.loginwithanimation.view.adStory.AddStoryActivity
import com.dicoding.picodiploma.loginwithanimation.view.detail.DetailActivity
import com.dicoding.picodiploma.loginwithanimation.view.login.LoginActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.Executors

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class MainActivity : AppCompatActivity() {
    private lateinit var mainViewModel: MainViewModel
    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: StoriesAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.rvStory.layoutManager = LinearLayoutManager(this)
        binding.rvStory.setHasFixedSize(true)
        adapter = StoriesAdapter().apply {
            onClick { story, itemListStoryBinding ->
                val optionsCompat = ActivityOptions.makeSceneTransitionAnimation(
                    this@MainActivity,
                    Pair(itemListStoryBinding.imgPoster, "image"),
                    Pair(itemListStoryBinding.tvName, "name")
                )
                Intent(this@MainActivity, DetailActivity::class.java).also { intent ->
                    intent.putExtra(DetailActivity.DATA_STORY, story)
                    startActivity(intent, optionsCompat.toBundle())
                }
            }
        }
        setupViewModel()
        setupAction()
        getNewStory()
        loadStories()
    }

    private fun setupViewModel() {
        mainViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreference.getInstance(dataStore))
        )[MainViewModel::class.java]
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_language -> {
                startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
                return true
            }
            R.id.action_logout -> {
                val dispatcher = Executors.newSingleThreadExecutor().asCoroutineDispatcher()
                val scope = CoroutineScope(dispatcher)
                scope.launch {
                    mainViewModel.removeUserIsLogin()
                    mainViewModel.removeUserToken()
                    withContext(Dispatchers.Main) {
                        Intent(this@MainActivity, LoginActivity::class.java).also { intent ->
                            startActivity(intent)
                            finish()
                        }
                    }
                }
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setupAction() {
        binding.apply {
            swipeMain.setOnRefreshListener {
                swipeMain.isRefreshing = true
                loadStories()
            }
            fabAddNewStory.setOnClickListener {
                val intent = Intent(this@MainActivity, AddStoryActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun getNewStory() {
        binding.apply {
            if (intent != null) {
                val isNewStory = intent.extras?.getBoolean(SUCCESS_UPLOAD_STORY)
                if (isNewStory != null && isNewStory) {
                    swipeMain.isRefreshing = true
                    loadStories()
                    this@MainActivity.showToast(getString(R.string.story_uploaded))
                }
            }
        }
    }

    private fun loadStories() {
        binding.apply {
            val dispatcher = Executors.newSingleThreadExecutor().asCoroutineDispatcher()
            val scope = CoroutineScope(dispatcher)
            scope.launch {
                val token = "Bearer ${mainViewModel.getUserToken()}"
                withContext(Dispatchers.Main) {
                    val service = ApiConfig().getApiService().getListStories(token)
                    service.enqueue(object : Callback<StoriesResponse> {
                        override fun onResponse(
                            call: Call<StoriesResponse>,
                            response: Response<StoriesResponse>
                        ) {
                            if (response.isSuccessful) {
                                val responseBody = response.body()
                                if (responseBody != null && !responseBody.error) {
                                    adapter.stories = responseBody.listStory
                                }
                            } else {
                                Toast.makeText(
                                    this@MainActivity,
                                    response.message(),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                        override fun onFailure(call: Call<StoriesResponse>, t: Throwable) {
                            Toast.makeText(
                                this@MainActivity,
                                getString(R.string.network_unavailable),
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                    })
                    rvStory.adapter = adapter
                    swipeMain.isRefreshing = false
                }

            }
        }
    }

    companion object {
        const val SUCCESS_UPLOAD_STORY = "success upload story"
    }
}