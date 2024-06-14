package com.prafull.chatbuddy.settings

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.prafull.chatbuddy.R
import com.prafull.chatbuddy.mainApp.ui.UserImage
import com.prafull.chatbuddy.signOutAndNavigateToAuth
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController, onBackClicked: () -> Unit) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val settingsViewModel: SettingsViewModel = koinViewModel()
    val context = LocalContext.current
    if (settingsViewModel.showClearDataDialog) {
        SignOutAndClearDataDialog(text = R.string.Clear_Data,
                onDismiss = { settingsViewModel.showClearDataDialog = false },
                onConfirm = { settingsViewModel.clearData() }
        )
    }
    if (settingsViewModel.showLogoutDialog) {
        SignOutAndClearDataDialog(text = R.string.sign_out,
                onDismiss = { settingsViewModel.showLogoutDialog = false },
                onConfirm = {
                    settingsViewModel.signOut(context)
                    navController.signOutAndNavigateToAuth()
                }
        )
    }
    Scaffold(modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection), topBar = {
        TopAppBar(title = {
            Text(
                    text = stringResource(R.string.settings)
            )
        }, navigationIcon = {
            IconButton(onClick = onBackClicked) {
                Icon(
                        imageVector = Icons.AutoMirrored.Default.ArrowBack,
                        contentDescription = "Back"
                )
            }
        }, scrollBehavior = scrollBehavior
        )
    }) { paddingValues ->
        LazyColumn(contentPadding = paddingValues, modifier = Modifier.fillMaxSize()) {
            item {
                Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(16.dp), verticalAlignment = CenterVertically
                ) {
                    UserImage(modifier = Modifier, firebaseAuth = settingsViewModel.mAuth)
                    Spacer(modifier = Modifier.padding(8.dp))
                    Text(
                            text = settingsViewModel.mAuth.currentUser?.displayName
                                ?: stringResource(R.string.user)
                    )
                }
            }
            item {
                Text(
                        text = stringResource(R.string.account),
                        modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
            item {
                Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                ) {
                    Icon(
                            painter = painterResource(id = R.drawable.baseline_mail_outline_24),
                            contentDescription = "null"
                    )
                    Spacer(modifier = Modifier.padding(4.dp))
                    Column {
                        Text(
                                text = stringResource(id = R.string.mail)
                        )
                        Text(
                                text = settingsViewModel.mAuth.currentUser?.email ?: stringResource(
                                        R.string.email_not_set
                                )
                        )
                    }
                }
            }
            item {
                SimpleSettingsItem(
                        onClick = { /*TODO*/ },
                        text = R.string.subscriptions,
                        icon = R.drawable.outline_monetization_on_24
                )
            }
            item {
                SimpleSettingsItem(
                        onClick = {
                            settingsViewModel.showClearDataDialog = true
                        }, text = R.string.Clear_Data, icon = R.drawable.baseline_data_usage_24
                )
            }
            item {
                SimpleSettingsItem(
                        onClick = {
                            settingsViewModel.showDeleteAccountDialog = true
                        }, text = R.string.delete_account, icon = R.drawable.outline_delete_24
                )
            }
            item {
                Text(
                        text = stringResource(R.string.app),
                        modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
            item {
                DualLineSettingsItem(
                        onClick = { /*TODO*/ },
                        line1 = R.string.color_scheme,
                        line2 = R.string.system_default,
                        icon = R.drawable.outline_color_lens_24
                )
            }
            item {
                Text(
                        text = stringResource(R.string.About),
                        modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
            item {
                SimpleSettingsItem(
                        onClick = { /*TODO*/ },
                        text = R.string.privacy_policy,
                        icon = R.drawable.outline_privacy_tip_24
                )
            }
            item {
                Row(
                        Modifier
                            .fillMaxWidth()
                            .clickable {
                                settingsViewModel.showLogoutDialog = true
                            }
                            .padding(16.dp), verticalAlignment = CenterVertically) {
                    Icon(
                            imageVector = Icons.AutoMirrored.Default.ExitToApp,
                            contentDescription = "null",
                            tint = Color(0xFFD32F2F)
                    )
                    Spacer(modifier = Modifier.padding(4.dp))
                    Text(
                            text = stringResource(R.string.sign_out), color = Color(0xFFD32F2F)
                    )
                }
            }
        }
    }
}

@Composable
private fun SimpleSettingsItem(
    onClick: () -> Unit,
    @StringRes text: Int,
    @DrawableRes icon: Int,
) {
    Row(
            Modifier
                .fillMaxWidth()
                .clickable { onClick() }
                .padding(16.dp)) {
        Icon(painter = painterResource(id = icon), contentDescription = "null")
        Spacer(modifier = Modifier.padding(4.dp))
        Text(
                text = stringResource(id = text)
        )
    }
}

@Composable
private fun DualLineSettingsItem(
    onClick: () -> Unit, @StringRes line1: Int, @StringRes line2: Int, @DrawableRes icon: Int
) {
    Row(modifier = Modifier
        .fillMaxWidth()
        .clickable { onClick() }
        .padding(16.dp)) {
        Icon(painter = painterResource(id = icon), contentDescription = "null")
        Spacer(modifier = Modifier.padding(4.dp))
        Column {
            Text(
                    text = stringResource(id = line1)
            )
            Text(
                    text = stringResource(id = line2)
            )
        }
    }
}

@Composable
fun SignOutAndClearDataDialog(@StringRes text: Int, onDismiss: () -> Unit, onConfirm: () -> Unit) {
    AlertDialog(onDismissRequest = onDismiss, confirmButton = {
        TextButton(onClick = onConfirm) {
            Text(text = stringResource(R.string.confirm))
        }
    }, text = {
        Text(text = stringResource(text))
    }, dismissButton = {
        TextButton(onClick = onDismiss) {
            Text(text = stringResource(R.string.cancel))
        }
    })
}