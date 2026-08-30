package com.jobradar.app.presentation

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jobradar.app.presentation.auth.LoginScreen
import com.jobradar.app.presentation.navigation.JobRadarNavHost
import com.jobradar.app.presentation.root.RootViewModel

/**
 * Top-level gate: observes the auth session. Shows the login screen when logged
 * out and the main app when signed in, cross-fading between them. This is the
 * single entry point that enforces "must be signed in to use the app".
 */
@Composable
fun JobRadarRoot(
    viewModel: RootViewModel = hiltViewModel(),
) {
    val session by viewModel.session.collectAsStateWithLifecycle()
    val loggedIn = session != null

    Crossfade(
        targetState = loggedIn,
        animationSpec = tween(durationMillis = 300),
        label = "root-auth-gate",
    ) { isIn ->
        if (isIn) {
            JobRadarNavHost()
        } else {
            LoginScreen(onLoginSuccess = {})
        }
    }
}
