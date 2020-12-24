package info.moevm.moodle.api

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.preferencesKey
import androidx.datastore.preferences.createDataStore
import info.moevm.moodle.R
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class DataStoreMoodleUser(context: Context) {

    private val dataStore: DataStore<Preferences> = context.createDataStore(
        name = context.resources.getString(R.string.moodle_user)
    )

    companion object {
        val FIELD_ID = preferencesKey<Int>("id")
        val FIELD_FULL_NAME = preferencesKey<String>("full_name")
        val FIELD_PROFILE_IMG_URL = preferencesKey<String>("profile_image_url")
        val FIELD_CITY = preferencesKey<String>("city")
        val FIELD_COUNTRY = preferencesKey<String>("country")
    }

    suspend fun addMoodleUser(id: Int, fullName: String, profileImgUrl: String, city: String, country: String) {
        dataStore.edit { preferences ->
            preferences[FIELD_ID] = id
            preferences[FIELD_FULL_NAME] = fullName
            preferences[FIELD_PROFILE_IMG_URL] = profileImgUrl
            preferences[FIELD_CITY] = city
            preferences[FIELD_COUNTRY] = country
        }
    }

    suspend fun getFullNameCurrent(): String {
        return dataStore.data.map { it[FIELD_FULL_NAME] ?: "" }.first()
    }

    val fullNameFlow: Flow<String> = dataStore.data.map {
        val fn = it[FIELD_FULL_NAME] ?: ""
        fn
    }

    val pImgFlow: Flow<String> = dataStore.data.map {
        val pIUrl = it[FIELD_PROFILE_IMG_URL] ?: ""
        pIUrl
    }

    val cityFlow: Flow<String> = dataStore.data.map {
        val city = it[FIELD_CITY] ?: ""
        city
    }

    val countryFlow: Flow<String> = dataStore.data.map {
        val country = it[DataStoreUser.FIELD_TOKEN] ?: ""
        country
    }
}
