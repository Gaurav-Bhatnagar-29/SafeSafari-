package com.example.safarisafe.ui.screens

import android.net.Uri
import android.widget.VideoView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.example.safarisafe.R
import com.example.safarisafe.ui.theme.BackgroundDark
import com.google.firebase.auth.FirebaseAuth

@Composable
fun SplashScreen(navController: NavController) {
    val context = LocalContext.current
    val videoUri = Uri.parse("android.resource://${context.packageName}/${R.raw.splash_video}")

    fun navigateNext() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val destination = if (currentUser != null) "status" else "login"
        navController.navigate(destination) {
            popUpTo("splash") { inclusive = true }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark),
        contentAlignment = Alignment.Center
    ) {
        AndroidView(
            factory = { ctx ->
                VideoView(ctx).apply {
                    setVideoURI(videoUri)
                    setOnCompletionListener {
                        navigateNext()
                    }
                    setOnErrorListener { _, _, _ ->
                        navigateNext()
                        true
                    }
                    start()
                }
            },
            modifier = Modifier.fillMaxSize()
        )
    }

    // Safety timeout in case video fails to start or listener is missed
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(8000) // Adjust based on video length
        navigateNext()
    }
}
