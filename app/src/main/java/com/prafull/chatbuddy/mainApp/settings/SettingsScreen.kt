package com.prafull.chatbuddy.mainApp.settings

import android.content.Context
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.prafull.chatbuddy.R
import com.prafull.chatbuddy.mainApp.common.components.SelectModelDialogBox
import com.prafull.chatbuddy.mainApp.common.components.UserImage
import com.prafull.chatbuddy.signOutAndNavigateToAuth
import com.prafull.chatbuddy.ui.theme.themechanging.ThemeOption
import com.prafull.chatbuddy.ui.theme.themechanging.ThemeViewModel
import org.koin.androidx.compose.koinViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val settingsViewModel: SettingsViewModel = koinViewModel()
    val modelsState by settingsViewModel.modelState.collectAsState()
    val themeViewModel: ThemeViewModel = koinViewModel()
    val context = LocalContext.current

    if (settingsViewModel.showModelSelectionDialog) {
        settingsViewModel.getModels()
        SelectModelDialogBox(modelsState = modelsState, onModelSelect = {
            settingsViewModel.showModelSelectionDialog = false
            settingsViewModel.setModel(it)
        }) {
            settingsViewModel.showModelSelectionDialog = false
        }
    }
    // Handle showing dialogs based on the ViewModel state
    HandleDialogs(settingsViewModel, context, navController)

    // Scaffold provides the basic structure of the screen with a top bar and content area
    Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                SettingsTopAppBar(scrollBehavior)
            }
    ) { paddingValues ->
        // Main content of the settings screen
        SettingsContent(paddingValues, settingsViewModel, themeViewModel)
    }
}

@Composable
private fun HandleDialogs(
    settingsViewModel: SettingsViewModel,
    context: Context,
    navController: NavController
) {
    // Show clear data dialog if needed
    if (settingsViewModel.showClearDataDialog) {
        SignOutAndClearDataDialog(
                text = R.string.Clear_Data,
                onDismiss = { settingsViewModel.showClearDataDialog = false },
                onConfirm = { settingsViewModel.clearData() }
        )
    }

    // Show logout dialog if needed
    if (settingsViewModel.showLogoutDialog) {
        SignOutAndClearDataDialog(
                text = R.string.sign_out,
                onDismiss = { settingsViewModel.showLogoutDialog = false },
                onConfirm = {
                    settingsViewModel.signOut(context)
                    navController.signOutAndNavigateToAuth()
                }
        )
    }
    if (settingsViewModel.showDeleteAccountDialog) {
        SignOutAndClearDataDialog(
                text = R.string.delete_account,
                onDismiss = { settingsViewModel.showDeleteAccountDialog = false },
                onConfirm = {
                    settingsViewModel.deleteAccount(context)
                    navController.signOutAndNavigateToAuth()
                }
        )
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsTopAppBar(scrollBehavior: TopAppBarScrollBehavior) {
    TopAppBar(
            title = { Text(text = stringResource(R.string.settings)) },
            scrollBehavior = scrollBehavior
    )
}

@Composable
private fun SettingsContent(
    paddingValues: PaddingValues,
    settingsViewModel: SettingsViewModel,
    themeViewModel: ThemeViewModel
) {
    val themeState by themeViewModel.themeOption.collectAsState()
    LazyColumn(contentPadding = paddingValues, modifier = Modifier.fillMaxSize()) {
        item { UserProfile(settingsViewModel) }
        item { SectionTitle(R.string.account) }
        item { UserEmail(settingsViewModel) }
        item {
            SimpleSettingsItem(
                    onClick = { /*TODO*/ },
                    text = R.string.subscriptions,
                    icon = R.drawable.outline_monetization_on_24
            )
        }
        item {
            SimpleSettingsItem(
                    onClick = { settingsViewModel.showClearDataDialog = true },
                    text = R.string.Clear_Data,
                    icon = R.drawable.baseline_data_usage_24
            )
        }
        item {
            SimpleSettingsItem(
                    onClick = {
                        settingsViewModel.showDeleteAccountDialog = true
                    },
                    text = R.string.delete_account,
                    icon = R.drawable.outline_delete_24
            )
        }
        item { SectionTitle(R.string.app) }
        item {
            DualLineSettingsItem(
                    onClick = {
                        settingsViewModel.showColorSchemeDialog = true
                    },
                    line1 = R.string.color_scheme,
                    line2 =
                    when (themeState) {
                        ThemeOption.SYSTEM_DEFAULT -> R.string.system_default
                        ThemeOption.LIGHT -> R.string.light
                        ThemeOption.DARK -> R.string.dark
                    },
                    icon = R.drawable.outline_color_lens_24
            )
        }
        item {
            Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            settingsViewModel.showModelSelectionDialog = true
                        }
                        .padding(16.dp)
            ) {
                Icon(
                        painter = painterResource(id = R.drawable.baseline_explore_24),
                        contentDescription = null
                )
                Spacer(modifier = Modifier.padding(4.dp))
                Column {
                    Text(text = stringResource(id = R.string.default_model))
                    Text(text = settingsViewModel.defaultModel)
                }
            }
        }
        item { SectionTitle(R.string.About) }
        item {
            SimpleSettingsItem(
                    onClick = { /*TODO*/ },
                    text = R.string.privacy_policy,
                    icon = R.drawable.outline_privacy_tip_24
            )
        }
        item { SignOutButton { settingsViewModel.showLogoutDialog = true } }
    }
    if (settingsViewModel.showColorSchemeDialog) {
        ThemeSelectionDialog(
                currentOption = themeViewModel.themeOption.collectAsState().value,
                onDismiss = {
                    settingsViewModel.showColorSchemeDialog = false
                },
                onOptionSelected = { option ->
                    themeViewModel.setThemeOption(option)
                    settingsViewModel.showColorSchemeDialog = false
                }
        )
    }
}

@Composable
private fun UserProfile(settingsViewModel: SettingsViewModel) {
    // Use remember to avoid recomposing with every state change
    val displayName = remember { settingsViewModel.mAuth.currentUser?.displayName ?: "User" }

    Row(
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = CenterVertically
    ) {
        UserImage(modifier = Modifier, firebaseAuth = settingsViewModel.mAuth)
        Spacer(modifier = Modifier.padding(8.dp))
        Text(text = displayName)
    }
}

@Composable
private fun UserEmail(settingsViewModel: SettingsViewModel) {
    // Use remember to avoid recomposing with every state change
    val email = remember { settingsViewModel.mAuth.currentUser?.email ?: "Email Not Set" }

    Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
    ) {
        Icon(
                painter = painterResource(id = R.drawable.baseline_mail_outline_24),
                contentDescription = null
        )
        Spacer(modifier = Modifier.padding(4.dp))
        Column {
            Text(text = stringResource(id = R.string.mail))
            Text(text = email)
        }
    }
}

@Composable
private fun SectionTitle(@StringRes title: Int) {
    Text(
            text = stringResource(title),
            modifier = Modifier.padding(horizontal = 16.dp)
    )
}

@Composable
private fun SimpleSettingsItem(
    onClick: () -> Unit,
    @StringRes text: Int,
    @DrawableRes icon: Int
) {
    Row(
            Modifier
                .fillMaxWidth()
                .clickable { onClick() }
                .padding(16.dp)
    ) {
        Icon(painter = painterResource(id = icon), contentDescription = null)
        Spacer(modifier = Modifier.padding(4.dp))
        Text(text = stringResource(id = text))
    }
}

@Composable
private fun DualLineSettingsItem(
    onClick: () -> Unit,
    @StringRes line1: Int,
    @StringRes line2: Int,
    @DrawableRes icon: Int
) {
    Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() }
                .padding(16.dp)
    ) {
        Icon(painter = painterResource(id = icon), contentDescription = null)
        Spacer(modifier = Modifier.padding(4.dp))
        Column {
            Text(text = stringResource(id = line1))
            Text(text = stringResource(id = line2))
        }
    }
}

@Composable
fun ThemeSelectionDialog(
    currentOption: ThemeOption,
    onDismiss: () -> Unit,
    onOptionSelected: (ThemeOption) -> Unit
) {
    val options = listOf(ThemeOption.SYSTEM_DEFAULT, ThemeOption.LIGHT, ThemeOption.DARK)
    val selectedOption = remember { mutableStateOf(currentOption) }

    AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text(text = stringResource(R.string.color_scheme)) },
            text = {
                Column {
                    options.forEach { option ->
                        Row(
                                Modifier
                                    .fillMaxWidth()
                                    .selectable(
                                            selected = (option == selectedOption.value),
                                            onClick = { selectedOption.value = option },
                                            role = Role.RadioButton
                                    )
                                    .padding(16.dp),
                                verticalAlignment = CenterVertically
                        ) {
                            RadioButton(
                                    selected = (option == selectedOption.value),
                                    onClick = { selectedOption.value = option }
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(text = option.name.replace("_", " ").lowercase(Locale.getDefault())
                                .replaceFirstChar {
                                    if (it.isLowerCase()) it.titlecase(
                                            Locale.getDefault()
                                    ) else it.toString()
                                })
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { onOptionSelected(selectedOption.value) }) {
                    Text(text = stringResource(R.string.confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text(text = stringResource(R.string.cancel))
                }
            }
    )
}

@Composable
private fun SignOutButton(onClick: () -> Unit) {
    Row(
            Modifier
                .fillMaxWidth()
                .clickable { onClick() }
                .padding(16.dp),
            verticalAlignment = CenterVertically
    ) {
        Icon(
                imageVector = Icons.AutoMirrored.Default.ExitToApp,
                contentDescription = null,
                tint = Color(0xFFD32F2F)
        )
        Spacer(modifier = Modifier.padding(4.dp))
        Text(
                text = stringResource(R.string.sign_out),
                color = Color(0xFFD32F2F)
        )
    }
}

@Composable
fun SignOutAndClearDataDialog(@StringRes text: Int, onDismiss: () -> Unit, onConfirm: () -> Unit) {
    AlertDialog(
            onDismissRequest = onDismiss,
            confirmButton = {
                TextButton(onClick = onConfirm) {
                    Text(text = stringResource(R.string.confirm))
                }
            },
            text = {
                Text(text = stringResource(text))
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text(text = stringResource(R.string.cancel))
                }
            }
    )
}
