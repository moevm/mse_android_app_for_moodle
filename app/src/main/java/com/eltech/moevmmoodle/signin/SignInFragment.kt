package com.eltech.moevmmoodle.signin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import com.eltech.moevmmoodle.R
import com.eltech.moevmmoodle.Screen
import com.eltech.moevmmoodle.navigate
import com.eltech.moevmmoodle.theme.MoodleTheme

/**
 * Fragment containing the sign in UI.
 */
class SignInFragment : Fragment() {

    private val viewModel: SignInViewModel by viewModels { SignInViewModelFactory() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel.navigateTo.observe(owner = viewLifecycleOwner) { navigateToEvent ->
            navigateToEvent.getContentIfNotHandled()?.let { navigateTo ->
                navigate(navigateTo, Screen.SignIn)
            }
        }

        return ComposeView(requireContext()).apply {
            // In order for savedState to work, the same ID needs to be used for all instances.
            id = R.id.sign_in_fragment

            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            setContent {
                MoodleTheme {
                    SignIn(
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
