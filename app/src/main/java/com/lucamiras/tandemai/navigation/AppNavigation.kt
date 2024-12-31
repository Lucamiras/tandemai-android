package com.lucamiras.tandemai.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.lucamiras.tandemai.ui.featureMistakes.MistakesScreen
import com.lucamiras.tandemai.ui.featureSetup.SetupScreen
import com.lucamiras.tandemai.ui.featureChat.ChatScreen
import com.lucamiras.tandemai.ui.featureChat.ChatViewModel
import com.lucamiras.tandemai.ui.featureMistakes.MistakesViewModel
import com.lucamiras.tandemai.ui.featureSetup.SetupViewModel


@Composable
fun AppNavigation(
    setupViewModel: SetupViewModel,
    chatViewModel: ChatViewModel,
    mistakesViewModel: MistakesViewModel
) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "StartScreen") {
        composable("startScreen"){
            SetupScreen(navController, setupViewModel)
        }
        composable("MyApp"){
            ChatScreen(navController, setupViewModel, chatViewModel)
        }
        composable("MyMistakes") {
            MistakesScreen(navController, mistakesViewModel)
        }
    }
}