package com.eltech.moevmmoodle.signin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import com.eltech.moevmmoodle.Screen
import com.eltech.moevmmoodle.navigate
import com.eltech.moevmmoodle.theme.MoodleTheme

/**
 * Fragment containing the welcome UI.
 */
class WelcomeFragment : Fragment() {

//    private val viewModel: WelcomeViewModel by viewModels { WelcomeViewModelFactory() }
    private val viewModel: SignInViewModel by viewModels { SignInViewModelFactory() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel.navigateTo.observe(owner = viewLifecycleOwner) { navigateToEvent ->
            navigateToEvent.getContentIfNotHandled().let { navigateTo ->
                if (navigateTo != null) {
                    navigate(navigateTo, Screen.Welcome)
                }
            }
        }

        return ComposeView(requireContext()).apply {
            setContent {
                MoodleTheme {
                    WelcomeScreen(
                        onNavigationEvent = { event ->
                            when (event) {
                                is SignInEvent.SignIn -> {
                                    viewModel.signIn(event.email, event.password)
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}
