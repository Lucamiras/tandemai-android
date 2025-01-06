package com.lucamiras.tandemai.ui.featureSetup

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.lucamiras.tandemai.data.model.Language
import com.lucamiras.tandemai.data.model.Scenario
import com.lucamiras.tandemai.data.model.SkillLevel
import com.lucamiras.tandemai.ui.featureChat.ChatViewModel
import com.lucamiras.tandemai.ui.featureMistakes.MistakesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetupScreen(navController: NavController,
                setupViewModel: SetupViewModel,
                chatViewModel: ChatViewModel,
                mistakesViewModel: MistakesViewModel) {

    // Here we retrieve the languages and skill levels from the enums in data.model
    val languages = remember { Language.entries.toList() }
    val skillLevel = remember { SkillLevel.entries.toList() }
    val scenario = remember { Scenario.entries.toList() }

    // Setup necessary variables for dropdowns and dropdown selection
    val languagesItemPosition = remember { mutableIntStateOf(0) }
    var skillLevelSliderPosition by remember { mutableFloatStateOf(0f) }
    val scenarioItemPosition = remember { mutableIntStateOf(0) }

    val isLanguagesDropdownExpanded = remember { mutableStateOf(false) }
    val isScenarioDropdownExpanded = remember { mutableStateOf(false) }

    // UI
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(setupViewModel.appName) }
            )
        },
    ) { innerPadding ->
        Column (
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ){
            Text(text="Choose your language and skill level!")
            Box {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable {
                        isLanguagesDropdownExpanded.value = true
                    }) {
                    Text(
                        text = languages[languagesItemPosition.intValue].name,
                        modifier = Modifier
                            .padding(12.dp)
                            .background(color = Color.LightGray)
                            .padding(12.dp)
                    )
                }
                DropdownMenu(expanded = isLanguagesDropdownExpanded.value, onDismissRequest = {isLanguagesDropdownExpanded.value = false}) {
                    languages.forEachIndexed { index, language ->
                        DropdownMenuItem(
                            text = {Text(text = language.name) },
                            onClick = {
                                isLanguagesDropdownExpanded.value = false
                                languagesItemPosition.intValue = index
                            })
                    }
                }
            }
            Box (
                modifier = Modifier
                    .height(100.dp)
                    .padding(start = 50.dp, end = 50.dp),
                contentAlignment = Alignment.Center
            ) {
                Column {
                    Slider (
                        value = skillLevelSliderPosition,
                        onValueChange = { skillLevelSliderPosition = it },
                        steps = 2,
                        valueRange = 0f..3f
                    )
                    Text(
                        text = skillLevel[skillLevelSliderPosition.toInt()].toString(),
                        textAlign = TextAlign.Center)
                }
            }
            Text("Optionally choose a scenario to practice")
            Box {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable {
                        isScenarioDropdownExpanded.value = true
                    }) {
                    Text(
                        text = scenario[scenarioItemPosition.intValue].short,
                        modifier = Modifier
                            .padding(12.dp)
                            .background(color = Color.LightGray)
                            .padding(12.dp)
                    )
                }
                DropdownMenu(expanded = isScenarioDropdownExpanded.value, onDismissRequest = {isScenarioDropdownExpanded.value = false}) {
                    scenario.forEachIndexed { index, scenario ->
                        DropdownMenuItem(
                            text = {Text(text = scenario.short) },
                            onClick = {
                                isScenarioDropdownExpanded.value = false
                                scenarioItemPosition.intValue = index
                            })
                    }
                }
            }
            Box {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(start=50.dp, top=25.dp, end=50.dp, bottom=25.dp)
                ) {
                    Text(
                        text = scenario[scenarioItemPosition.intValue].description,
                        textAlign = TextAlign.Center,
                        fontStyle = FontStyle.Italic
                    )
                }
            }

            // BUTTON
            Button(
                onClick={
                    // Save user selection to setupViewModel
                    setupViewModel.saveUserSelection(
                        language = languages[languagesItemPosition.intValue],
                        skillLevel = skillLevel[skillLevelSliderPosition.toInt()],
                        scenario = scenario[scenarioItemPosition.intValue]
                    )
                    // Clear any previous chat history and mistakes
                    chatViewModel.clearChatHistory()
                    mistakesViewModel.clearMistakes()
                    // Initiate a new conversation
                    chatViewModel.startConversation(setupViewModel)
                    // Navigate to ChatScreen
                    navController.navigate("ChatScreen")
                }) {
                Text(
                    text="Let's chat!")
            }
        }

    }

}
