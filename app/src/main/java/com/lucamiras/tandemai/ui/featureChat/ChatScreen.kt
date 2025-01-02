package com.lucamiras.tandemai.ui.featureChat

import androidx.compose.foundation.hoverable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.ai.client.generativeai.type.Content
import com.google.ai.client.generativeai.type.asTextOrNull
import com.lucamiras.tandemai.data.model.Mistake
import com.lucamiras.tandemai.data.repository.ChatSystemInstruction
import com.lucamiras.tandemai.data.repository.MistakeSystemInstruction
import com.lucamiras.tandemai.data.repository.OpeningSystemInstruction
import com.lucamiras.tandemai.ui.featureMistakes.MistakesViewModel
import com.lucamiras.tandemai.ui.featureSetup.SetupViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(navController: NavController,
               setupViewModel: SetupViewModel,
               chatViewModel: ChatViewModel,
               mistakesViewModel: MistakesViewModel
) {
    var message by remember { mutableStateOf("") }
    val chatHistory: List<Content> by chatViewModel.chatHistory.collectAsState()
    val mistakesHistory: List<Mistake> by mistakesViewModel.mistakes.collectAsState()
    val mistakesNum: Int = mistakesHistory.size
    val mistakesButtonColor = if (mistakesNum == 0) { Color.LightGray } else { Color.Magenta }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(setupViewModel.appName) }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                containerColor = mistakesButtonColor,
                onClick = {
                    navController.navigate("MistakesScreen")
                }
            ) {
                Text(mistakesNum.toString())
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
                            chatViewModel.addNewConversation(
                                message = message,
                                systemInstructions = ChatSystemInstruction,
                                setupViewModel = setupViewModel
                            )
                            chatViewModel.evaluateMistakes(
                                setupViewModel = setupViewModel,
                                mistakesViewModel = mistakesViewModel
                            )
                        }
                        message = ""
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
                items(chatHistory.asReversed()) { msg ->
                    ChatBubble(msg)
                }
            }
        }
    }
}

@Composable
fun ChatBubble(chatMessage: Content) {
    val isModelMessage = when (chatMessage.role.toString()) {
        "model" -> true
        else -> false
    }
    val userName = if (isModelMessage) {
        "AI Tandem Partner"
    } else {
        "Me"
    }
    val horizontalAlignment = if(isModelMessage) {
        Alignment.Start
    } else {
        Alignment.End
    }
    val bubbleShape = if (isModelMessage) {
        RoundedCornerShape(4.dp, 20.dp, 20.dp, 20.dp)
    } else {
        RoundedCornerShape(20.dp, 4.dp, 20.dp, 20.dp)
    }

    Column(
        horizontalAlignment = horizontalAlignment,
        modifier = Modifier
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .fillMaxWidth()
    ) {
        Text(
            text = userName,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Row {
            BoxWithConstraints {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.LightGray),
                    shape = bubbleShape,
                    modifier = Modifier.widthIn(0.dp, maxWidth * 0.9f)
                ) {
                    Text(
                        text = chatMessage.parts[0].asTextOrNull().toString(),
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}