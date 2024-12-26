package com.lucamiras.tandemai

import android.icu.text.CaseMap.Title
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHost
import androidx.navigation.compose.rememberNavController
import com.lucamiras.tandemai.src.MistakesBubble
import com.lucamiras.tandemai.src.MistakesContent


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyMistakes(navController: NavController) {
    val mistakesList = navController.currentBackStackEntry?.arguments?.getStringArrayList("MyMistakes/{mistakesList}")?.toList()

    Scaffold (
        topBar = {
            TopAppBar (
                title = { Text("Mistakes") }
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

