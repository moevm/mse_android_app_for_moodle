package com.eltech.moevmmoodle.signin

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.eltech.moevmmoodle.Screen
import com.eltech.moevmmoodle.Screen.SignIn
import com.eltech.moevmmoodle.util.Event

class WelcomeViewModel(private val userRepository: UserRepository) : ViewModel() {

    // It's an extension of LiveData, with the difference that it is not an abstract class
    // and the setValue(T) and post Value(T) methods are output in the api, that is, public.
    // In fact, the class is a helper for those cases when we do not want to put the logic of
    // updating the value in LiveData, but only want to use it as a Holder.
    private val _navigateTo = MutableLiveData<Event<Screen>>()
    val navigateTo: LiveData<Event<Screen>> = _navigateTo

    fun handleContinue(email: String) {
        if (userRepository.isKnownUserEmail(email)) {
            _navigateTo.value = Event(SignIn)
        }
    }
}

@Suppress("UNCHECKED_CAST")
class WelcomeViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WelcomeViewModel::class.java)) {
            return WelcomeViewModel(UserRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
