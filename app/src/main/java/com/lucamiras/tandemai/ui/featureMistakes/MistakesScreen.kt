package com.lucamiras.tandemai.ui.featureMistakes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import com.lucamiras.tandemai.data.model.Mistake


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MistakesScreen(navController: NavController, mistakesViewModel: MistakesViewModel) {

    val mistakesListTest = listOf(
        Mistake(id=0, description = "Hello"),
        Mistake(id=1, description = "My name is"),
        Mistake(id=2, description = "Doge"),
        Mistake(id=3, description = "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua.")
    )
    val mistakesList = mistakesViewModel.mistakes
    val mistakesNum = mistakesList.value!!.size
    val topAppBarTitle = "Mistakes"

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

@Composable
fun MistakesContent(mistakesList: MutableLiveData<List<Mistake>>) {
    LazyColumn {
        items(mistakesList.value!!.toList()) { mistake ->
            MistakesBubble(mistake = mistake)
        }
    }
}

@Composable
fun MistakesBubble(mistake: Mistake) {
    Card (
        modifier = Modifier
            .padding(12.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(20.dp, 20.dp, 20.dp, 20.dp)
    ) {
        Row (
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text (
                text = mistake.id.toString(),
                modifier = Modifier
                    .padding(12.dp)
                    .wrapContentWidth()
            )
            Text (
                text = mistake.description,
                modifier = Modifier
                    .padding(12.dp)
                    .wrapContentWidth()
            )
        }
    }
}