@file:OptIn(ExperimentalMaterial3Api::class)

package com.lucamiras.tandemai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.graphics.Color
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


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
                    val llmClient = LLMClient(language, level)
                    MyApp(navController, llmClient)
                }
            }
        }
    }
}

@Composable
fun MyApp(navController: NavController, llmClient: LLMClient) {
    val userName: String = "user"
    val assistantName: String = "model"
    val appName: String = "Tandem AI"
    val openingMessage: String = "Start the conversation in $llmClient.language."
    val openingResponse: String = llmClient.callLLM(openingMessage)
    var message by remember { mutableStateOf("")}
    val history = remember { mutableStateListOf(content(role=assistantName) {text(openingResponse)})}
    var mistakesExpanded by remember { mutableStateOf(false) }
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
                    mistakesExpanded = !mistakesExpanded
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
        AnimatedVisibility (
            visible = mistakesExpanded,
            enter = fadeIn(),
            exit = fadeOut()
        ){
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.LightGray),
                shape = RoundedCornerShape(20.dp, 20.dp, 20.dp, 20.dp),
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .fillMaxHeight(0.3f)
                    .padding(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                LazyColumn (
                    modifier = Modifier
                        .padding(16.dp)
                        .wrapContentSize()
                ) {
                    items(mistakesList) { m ->
                        Text(m, modifier = Modifier.padding(15.dp))
                    }
                }
            }
        }
    }
}
