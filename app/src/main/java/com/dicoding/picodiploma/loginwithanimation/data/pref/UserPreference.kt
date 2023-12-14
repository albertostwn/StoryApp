package com.dicoding.picodiploma.loginwithanimation.data.pref

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserPreference private constructor(private val dataStore: DataStore<Preferences>) {
    private val userKey = booleanPreferencesKey(USER_KEY)
    private val userToken = stringPreferencesKey(USER_TOKEN)
    suspend fun saveUserIsLogin(isLogin: Boolean) {
        dataStore.edit {
            it[userKey] = isLogin
        }
    }

    fun getUserIsLogin(): Flow<Boolean> =
        dataStore.data.map {
            it[userKey] ?: false
        }

    suspend fun removeUserIsLogin() {
        dataStore.edit {
            if (it.contains(userKey)) {
                it.remove(userKey)
            }
        }
    }

    suspend fun saveUserToken(token: String) =
        dataStore.edit {
            it[userToken] = token
        }

    suspend fun removeUserToken() {
        dataStore.edit {
            if (it.contains(userToken)) {
                it.remove(userToken)
            }
        }
    }

    fun getUserToken(): Flow<String?> =
        dataStore.data.map {
            it[userToken]
        }

    companion object {
        @Volatile
        private var INSTANCE: UserPreference? = null

        private const val USER_TOKEN = "user_token"
        private const val USER_KEY = "user_key"

        fun getInstance(dataStore: DataStore<Preferences>): UserPreference {
            return INSTANCE ?: synchronized(this) {
                val instance = UserPreference(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}