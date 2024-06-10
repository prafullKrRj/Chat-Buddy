package com.prafull.chatbuddy.mainApp.ui.promplibraryscreen

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.prafull.chatbuddy.mainApp.models.PromptLibraryItem
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalFoundationApi::class, ExperimentalComposeUiApi::class)
@Composable
fun PromptScreen(modifier: Modifier, paddingValues: PaddingValues, navController: NavController) {
    val promptViewModel: PromptLibraryViewModel = koinViewModel()
    val state by promptViewModel.uiState.collectAsState()
    val searchQuery = remember { mutableStateOf("") }
    val tabs = listOf("Personal Prompts", "Business Prompts")
    val scope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current
    val pagerState = rememberPagerState(
            pageCount = { tabs.size },
            initialPage = 0
    )
    Log.d("PromptScreen", "PromptScreen: ${state.personalPrompts}")
    if (state.isLoading) {
        CircularProgressIndicator()
    } else if (state.error != null) {
        Button(onClick = {
            promptViewModel.getPrompts()
        }) {
            Text(text = "Retry")
        }
    } else {
        Column(
                Modifier
                    .padding(paddingValues)
                    .pointerInteropFilter {
                        focusManager.clearFocus()
                        false
                    }
        ) {
            OutlinedTextField(
                    value = searchQuery.value,
                    onValueChange = { searchQuery.value = it },
                    label = { Text("Search") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                            focusedContainerColor = MaterialTheme.colorScheme.outlineVariant,
                            unfocusedContainerColor = MaterialTheme.colorScheme.outlineVariant
                    ),
                    shape = RoundedCornerShape(30)
            )
            TabRow(selectedTabIndex = pagerState.currentPage) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                            text = { Text(title) },
                            selected = pagerState.currentPage == index,
                            onClick = {
                                scope.launch { pagerState.animateScrollToPage(index) }
                            }
                    )
                }
            }
            HorizontalPager(state = pagerState) { page ->
                when (page) {
                    0 -> {
                        DisplayPrompts(state.personalPrompts, searchQuery.value, modifier)
                    }

                    1 -> {
                        DisplayPrompts(state.businessPrompts, searchQuery.value, modifier)
                    }
                }
            }
        }
    }
}

@Composable
fun DisplayPrompts(prompts: List<PromptLibraryItem>, searchQuery: String, modifier: Modifier) {
    val filteredPrompts = prompts.filter { it.name.contains(searchQuery, ignoreCase = true) }
    LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 150.dp),
            modifier = modifier
    ) {
        items(filteredPrompts) { prompt ->
            Card(
                    modifier = Modifier
                        .padding(8.dp)
                        .height(220.dp)
            ) {
                val verticalScrollState = rememberScrollState()
                Column(
                        modifier = Modifier
                            .padding(8.dp)
                            .verticalScroll(verticalScrollState),
                        verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = prompt.name, fontWeight = FontWeight.SemiBold, fontSize = 18.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = prompt.description)
                }
            }
        }
    }
}