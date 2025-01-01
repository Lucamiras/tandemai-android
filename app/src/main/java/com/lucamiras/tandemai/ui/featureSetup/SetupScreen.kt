package com.lucamiras.tandemai.ui.featureSetup

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.lucamiras.tandemai.data.model.Language
import com.lucamiras.tandemai.data.model.SkillLevel
import java.util.Locale

@Composable
fun SetupScreen(navController: NavController, setupViewModel: SetupViewModel) {

    // Here we retrieve the languages and skill levels from the enums in data.model
    val languages = remember { Language.entries.toList() }
    val skillLevel = remember { SkillLevel.entries.toList() }

    // Setup necessary variables for dropdowns and dropdown selection
    val languagesItemPosition = remember {
        mutableIntStateOf(0)
    }
    val skillLevelsItemPosition = remember {
        mutableIntStateOf(0)
    }
    val isLanguagesDropdownExpanded = remember {
        mutableStateOf(false)
    }
    val isSkillLevelsDropdownExpanded = remember {
        mutableStateOf(false)
    }

    // INTERFACE
    Column (
        modifier = Modifier
            .fillMaxSize(),
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
                        .background(color= Color.LightGray)
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
        Box {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable {
                    isSkillLevelsDropdownExpanded.value = true
                }) {
                Text(
                    text = skillLevel[skillLevelsItemPosition.intValue].name,
                    modifier = Modifier
                        .padding(12.dp)
                        .background(color= Color.LightGray)
                        .padding(12.dp)
                )
            }
            DropdownMenu(expanded = isSkillLevelsDropdownExpanded.value, onDismissRequest = {isSkillLevelsDropdownExpanded.value = false}) {
                skillLevel.forEachIndexed { index, level ->
                    DropdownMenuItem(
                        text = {Text(text = level.name)},
                        onClick = {
                            isSkillLevelsDropdownExpanded.value = false
                            skillLevelsItemPosition.intValue = index
                        })
                }
            }
        }

        // BUTTON
        Button(
            onClick={
                setupViewModel.setLanguage(languages[languagesItemPosition.intValue])
                setupViewModel.setSkillLevel(skillLevel[skillLevelsItemPosition.intValue])
                navController.navigate("ChatScreen")}) {
            Text(
                text="Let's go!")
        }
    }
}