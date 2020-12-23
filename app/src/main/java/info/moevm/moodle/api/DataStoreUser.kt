package info.moevm.moodle.api

import android.content.Context
import android.widget.Toast
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.createDataStore
//import info.moevm.moodle.api.UserPreferencesRepository.UserScheme.FIELD_LOGIN
//import info.moevm.moodle.api.UserPreferencesRepository.UserScheme.FIELD_PASSWORD
//import info.moevm.moodle.api.UserPreferencesRepository.UserScheme.FIELD_TOKEN
import info.moevm.moodle.model.APIVariables
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

class DataStoreUser(context: Context) {
    //TODO dipatcher
    //TODO in sign in use COURUTINES
    private val dataStore: DataStore<Preferences> = context.createDataStore(
        name = APIVariables.USER_PREFERENCES_NAME.value
    )

    //    object UserScheme {
    companion object {
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
        //TODO add in courutin
    }

    val tokenFlow: Flow<String> = dataStore.data.map {
        val tok = it[FIELD_TOKEN] ?: ""
        tok
    }

}



