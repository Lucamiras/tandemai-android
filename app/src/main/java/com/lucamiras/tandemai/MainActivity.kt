@file:OptIn(ExperimentalMaterial3Api::class)

package com.lucamiras.tandemai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.activity.viewModels
import com.lucamiras.tandemai.navigation.AppNavigation
import com.lucamiras.tandemai.ui.featureChat.ChatViewModel
import com.lucamiras.tandemai.ui.featureMistakes.MistakesViewModel
import com.lucamiras.tandemai.ui.featureSetup.SetupViewModel


class MainActivity : ComponentActivity() {

    private val setupViewModel: SetupViewModel by viewModels()
    private val chatViewModel: ChatViewModel by viewModels()
    private val mistakesViewModel: MistakesViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        chatViewModel.appContext = this.applicationContext
        enableEdgeToEdge()
        setContent {
            AppNavigation(
                setupViewModel = setupViewModel,
                chatViewModel = chatViewModel,
                mistakesViewModel = mistakesViewModel)
        }
    }
}
