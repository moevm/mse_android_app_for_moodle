package info.moevm.moodle.api

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.createDataStore
import info.moevm.moodle.model.APIVariables
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DataStoreUser(context: Context) {
    private val dataStore: DataStore<Preferences> = context.createDataStore(
        name = APIVariables.USER_PREFERENCES_NAME.value
    )

    companion object {
        val FIELD_LOGIN = preferencesKey<String>("login")
        val FIELD_PASSWORD = preferencesKey<String>("password")
        val FIELD_TOKEN = preferencesKey<String>("token")
        // TODO add email
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

    val tokenFlow: Flow<String> = dataStore.data.map {
        val tok = it[FIELD_TOKEN] ?: ""
        tok
    }

    val loginFlow: Flow<String> = dataStore.data.map {
        val login = it[FIELD_LOGIN] ?: ""
        login
    }
}
