package com.example.luckytask.firestore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.serialization.json.Json


/**
 * Slim local persistent Storage for:
 * - User info (Name, ...)
 * - Groups (Name, key)
 */

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class AppSettings {
    companion object{
        val USER_KEY = stringPreferencesKey("user_key")
        val GROUP_LIST_KEY = stringPreferencesKey("group_key")
    }

    suspend fun getUserInfo(context: Context):UserDAO?{
        val data = context.dataStore.data.first()
        val json = data[USER_KEY]?: return null
        return Json.decodeFromString<UserDAO>(json)
    }

    suspend fun setUserInfo(context: Context, user: UserDAO) {
        val json = Json.encodeToString(user)
        context.dataStore.edit { settings ->
            settings[USER_KEY] = json
        }
    }

    suspend fun getGroups(context: Context):List<GroupDAO>?{
        val data = context.dataStore.data.first()
        val json = data[GROUP_LIST_KEY]?: return null
        return Json.decodeFromString<List<GroupDAO>>(json)
    }

    suspend fun setGroups(context: Context, groups:List<GroupDAO>){
        val json = Json.encodeToString(groups)
        context.dataStore.edit { settings ->
            settings[GROUP_LIST_KEY] = json
        }
    }
}