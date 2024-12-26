@file:OptIn(ExperimentalMaterial3Api::class)

package com.lucamiras.tandemai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.launch
import com.lucamiras.tandemai.src.LLMClient
import com.lucamiras.tandemai.src.ChatBubble
import com.lucamiras.tandemai.StartScreen
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.lucamiras.tandemai.src.Mistake
import com.lucamiras.tandemai.src.MistakesContent


class SharedViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T{
        if (modelClass.isAssignableFrom(SharedViewModel::class.java)) {
            return SharedViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val llmClient = LLMClient()
        Log.w("MYTAG", "Initialized LLM")
        val sharedViewModel = ViewModelProvider(
            this, SharedViewModelFactory())[SharedViewModel::class.java]
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            NavHost(navController = navController, startDestination = "StartScreen") {
                composable("startScreen"){
                    StartScreen(navController)
                }
                composable("MyApp/{chosenLanguage}/{chosenLevel}",
                    arguments= listOf(
                        navArgument("chosenLanguage") { type = NavType.StringType},
                        navArgument("chosenLevel") { type = NavType.StringType})
                ){
                    val language = navController.currentBackStackEntry?.arguments?.getString("chosenLanguage")
                    val level = navController.currentBackStackEntry?.arguments?.getString("chosenLevel")

                    MyApp(navController, llmClient, language, level)
                }
                composable("MyMistakes") {
                    MyMistakes(navController)
                }

            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyApp(navController: NavController,
          llmClient: LLMClient,
          language: String?,
          level: String?) {
    llmClient.initializePartner(language, level)
    val sharedViewModel: SharedViewModel = viewModel(factory = SharedViewModelFactory())
    val userName: String = "user"
    val assistantName: String = "model"
    val appName: String = "Tandem AI"
    val openingMessage: String = "Start the conversation in $llmClient.language."
    val openingResponse: String = llmClient.callLLM(openingMessage)
    var message by remember { mutableStateOf("")}
    val history = remember { mutableStateListOf(content(role=assistantName) {text(openingResponse)})}
    val mistakesNum = llmClient.mistakesCount.collectAsState()
    val mistakesColor = llmClient.mistakesColor.collectAsState()
    val mistakesList = llmClient.mistakesList
    val coroutineScope = rememberCoroutineScope()

    Scaffold (
        topBar = {
                TopAppBar (
                    title  = { Text(appName) }
                )
        },
        floatingActionButton = {
            FloatingActionButton(
                containerColor = mistakesColor.value,
                onClick = {
                    for (m in mistakesList) {
                        sharedViewModel.addToMistakes(m)
                    }
                    navController.navigate("MyMistakes")
                }
            ) {
                Text(mistakesNum.value.toString())
            }
        },
        floatingActionButtonPosition = FabPosition.End,
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = message,
                    onValueChange = { message = it },
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 56.dp),
                    shape = RoundedCornerShape(25.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = {
                        if (message.isNotBlank()) {
                            history.add(content(role=userName){text(message)})
                            coroutineScope.launch {
                                history.add(content(role=assistantName) {text(llmClient.callLLM(message))})
                                message = ""
                            }
                        }
                    }
                ) {
                    Text("Send")
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                reverseLayout = true
            ) {
                items(history.asReversed()) { msg ->
                    ChatBubble(msg)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyMistakes(navController: NavController) {
    val sharedViewModel: SharedViewModel = viewModel(factory = SharedViewModelFactory())
    Log.w("MYTAG", sharedViewModel.mistakes.toString())
    val mistakesListTest = listOf(
        Mistake(id=0, description = "Hello"),
        Mistake(id=1, description = "My name is"),
        Mistake(id=2, description = "Doge"),
        Mistake(id=3, description = "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua.")
    )
    val mistakesList = sharedViewModel.mistakes.value
    val mistakesNum = 3
    val topAppBarTitle = if (mistakesNum == 0) { "Mistakes" } else { "Mistakes ($mistakesNum)" }

    Scaffold (
        topBar = {
            TopAppBar (
                title = { Text(topAppBarTitle) }
            )
        }
    ) { innerPadding ->
        Box (
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            MistakesContent(mistakesList = mistakesList)
        }
    }
}
