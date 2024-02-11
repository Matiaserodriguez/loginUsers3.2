package com.example.userlogin

import android.content.Context
import androidx.datastore.dataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

class DataStorePreferences(context: Context) {
    private val dataStore = context.dataStore
    private val gson = Gson()
    suspend fun insertDB (db : InMemoryDatabase) {
        dataStore.edit { pref ->
            pref[stringPreferencesKey("users")] = gson.toJson(db)
        }
    }

    fun getDB() : Flow<String> {
        return dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    println("EXCEPTION")
                    emit(emptyPreferences())
                } else {
                    println("EXCEPTION")
                    throw exception
                }
            }
            .map {pref ->
                val usersFromDataStore = pref[stringPreferencesKey("users")] ?: "{}"
                println("---------users from data store---------")
                println(usersFromDataStore)
                usersFromDataStore
            }
    }

    fun convertJSONtoDB(db: String) : InMemoryDatabase {
        val inMemoryDatabase = gson.fromJson(db, InMemoryDatabase::class.java)
        return inMemoryDatabase
    }
}

data class InMemoryDatabase(val users: MutableList<User> = mutableListOf())

class User(owner: InMemoryDatabase, usr: String, pass: String) {
    val username = usr
    val password = pass
    private var courses : MutableMap<Int, String> = mutableMapOf()

    init {
        owner.users.add(this)
    }

    fun addCourse(courseCode: Int, courseName: String){
        courses[courseCode] = courseName
    }

    fun removeCourse (courseCode: Int) {
        courses.remove(courseCode)
    }
}

var db = InMemoryDatabase()