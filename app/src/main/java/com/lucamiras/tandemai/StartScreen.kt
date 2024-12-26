package com.lucamiras.tandemai

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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController


@Composable
fun StartScreen(navController: NavController) {

    val languages = listOf("Hungarian", "Danish", "English")
    val levels = listOf("A1", "A2", "B1", "B2")
    val languagesItemPosition = remember {
        mutableIntStateOf(0)
    }
    val levelsItemPosition = remember {
        mutableIntStateOf(0)
    }
    val isLanguagesDropdownExpanded = remember {
        mutableStateOf(false)
    }
    val isLevelsDropdownExpanded = remember {
        mutableStateOf(false)
    }

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
                    text = languages[languagesItemPosition.intValue],
                    modifier = Modifier
                        .padding(12.dp)
                        .background(color= Color.LightGray)
                        .padding(12.dp)
                )
            }
            DropdownMenu(expanded = isLanguagesDropdownExpanded.value, onDismissRequest = {isLanguagesDropdownExpanded.value = false}) {
                languages.forEachIndexed { index, language ->
                    DropdownMenuItem(
                        text = {Text(text = language)},
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
                    isLevelsDropdownExpanded.value = true
                }) {
                Text(
                    text = levels[levelsItemPosition.intValue],
                    modifier = Modifier
                        .padding(12.dp)
                        .background(color= Color.LightGray)
                        .padding(12.dp)
                )
            }
            DropdownMenu(expanded = isLevelsDropdownExpanded.value, onDismissRequest = {isLevelsDropdownExpanded.value = false}) {
                levels.forEachIndexed { index, level ->
                    DropdownMenuItem(
                        text = {Text(text = level)},
                        onClick = {
                            isLevelsDropdownExpanded.value = false
                            levelsItemPosition.intValue = index
                        })
                }
            }
        }

        Button(onClick= {
            val chosenLanguage = languages[languagesItemPosition.intValue]
            val chosenLevel = levels[levelsItemPosition.intValue]
            navController.navigate("MyApp/$chosenLanguage/$chosenLevel")}) {
            Text(text="Let's go!")
        }
    }
}