package info.moevm.moodle.api

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.preferencesKey
import androidx.datastore.preferences.core.preferencesOf
import androidx.datastore.preferences.createDataStore
import info.moevm.moodle.api.UserPreferencesRepository.UserScheme.FIELD_LOGIN
import info.moevm.moodle.api.UserPreferencesRepository.UserScheme.FIELD_PASSWORD
import info.moevm.moodle.api.UserPreferencesRepository.UserScheme.FIELD_TOKEN
import info.moevm.moodle.model.APIVariables
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

class UserPreferencesRepository(context: Context) {
//TODO dipatcher
    //TODO in sign in use COURUTINES
    private val dataStore: DataStore<Preferences> = context.createDataStore(
        name = APIVariables.USER_PREFERENCES_NAME.value
    )

    object UserScheme {
        val FIELD_LOGIN = preferencesKey<String>("login")
        val FIELD_PASSWORD = preferencesKey<String>("password")
        val FIELD_TOKEN = preferencesKey<String>("token")
        //TODO add email
    }

    suspend fun updateToken(token: String) {
        dataStore.edit { preferences ->
            preferences[FIELD_TOKEN] = token
        }
    }

    suspend fun addUser(login: String, password: String, token: String) {
        dataStore.edit { preferences ->
            preferences[FIELD_LOGIN] = login
            preferences[FIELD_PASSWORD] = password
            preferences[FIELD_TOKEN] = token
        }

    }

    fun getToken(): Flow<String> {
        val token: Flow<String> = dataStore.data
            .catch {
                throw it
            }
            .map {
                preferences->
                preferences[FIELD_TOKEN]?:""
            }
        return token
    }
}



