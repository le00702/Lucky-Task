package com.example.luckytask.firestore

import android.content.Context
import android.util.Log
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
    companion object {
        val USER_KEY = stringPreferencesKey("user_key")
        val GROUP_LIST_KEY = stringPreferencesKey("group_key")

        val CURRENT_GROUP_KEY = stringPreferencesKey("current_group_key")

        suspend fun getUserInfo(context: Context): UserDAO? {
            val data = context.dataStore.data.first()
            val json = data[USER_KEY] ?: return null
            return Json.decodeFromString<UserDAO>(json)
        }

        suspend fun setUserInfo(context: Context, user: UserDAO) {
            val json = Json.encodeToString(user)
            context.dataStore.edit { settings ->
                settings[USER_KEY] = json
            }
        }

        suspend fun getCurrentGroup(context: Context): Pair<String, String>? {
            val data = context.dataStore.data.first()
            val json = data[CURRENT_GROUP_KEY] ?: return null
            return Json.decodeFromString<Pair<String, String>>(json)
        }

        suspend fun setCurrentGroup(context: Context, group:GroupDAO) {
            val data = Pair(group.id, group.name)
            val json = Json.encodeToString(data)
            context.dataStore.edit { settings ->
                settings[CURRENT_GROUP_KEY] = json
            }
        }

        suspend fun getGroups(context: Context): MutableMap<String,String>? {
            val data = context.dataStore.data.first()
            val json = data[GROUP_LIST_KEY] ?: return null
            return Json.decodeFromString<MutableMap<String,String>>(json)
        }

        suspend fun setGroups(context: Context, groups: MutableMap<String,String>) {
            val json = Json.encodeToString(groups)
            context.dataStore.edit { settings ->
                settings[GROUP_LIST_KEY] = json
            }
        }

        suspend fun addGroup(context: Context, group: GroupDAO) {
            val data = context.dataStore.data.first()
            val json = data[GROUP_LIST_KEY]
            if (json == null) {
                Log.i("AppSettings", "Adding first group")
                setGroups(context, mutableMapOf(group.id to group.name))
                return
            }
            val list = Json.decodeFromString<MutableMap<String,String>>(json)
            if(list.containsKey(group.id)){
                Log.i("AppSettings", "Group already exists")
                return
            }else{
                list.put(group.id, group.name)
            }
            Log.i("AppSettings", "Adding ${group.name} to groups")
            setGroups(context, list)
        }

        suspend fun removeGroup(context: Context, id:String) {
            val data = context.dataStore.data.first()
            val json = data[GROUP_LIST_KEY]
            if (json == null) {
                Log.i("AppSettings", "No Group Found when trying to remove")
                return
            }
            val list = Json.decodeFromString<MutableMap<String, String>>(json)
            list.remove(id)
            setGroups(context, list)
        }

        /**
         * Clear all local dataStore data
         */
        suspend fun removeAllData(context: Context) {
            context.dataStore.edit { settings ->
                settings.clear()
            }
        }
    }
}