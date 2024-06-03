package com.prafull.chatbuddy.authScreen

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.android.gms.auth.api.signin.GoogleSignIn.getClient
import com.google.android.gms.auth.api.signin.GoogleSignIn.getSignedInAccountFromIntent
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.prafull.chatbuddy.BuildConfig
import com.prafull.chatbuddy.MajorScreens
import com.prafull.chatbuddy.R
import org.koin.androidx.compose.getViewModel

@Composable
fun AuthScreen(
    navController: NavController,
    mAuth: FirebaseAuth,
) {
    val authViewModel: AuthViewModel = getViewModel()
    val context = LocalContext.current
    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(BuildConfig.WEB_CLIENT_ID)
        .requestEmail()
        .build()
    val googleSignInClient = getClient(context, gso)
    // Google SignIn Launcher
    val signInLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult()) { result ->
            val googleSignInAccountTask = getSignedInAccountFromIntent(result.data)
            val exception = googleSignInAccountTask.exception
            if (googleSignInAccountTask.isSuccessful) {
                try {
                    val account = googleSignInAccountTask.getResult(ApiException::class.java)!!
                    mAuth.signInWithCredential(
                            GoogleAuthProvider.getCredential(
                                    account.idToken,
                                    null
                            )
                    )
                        .addOnCompleteListener { authResultTask ->
                            if (authResultTask.isSuccessful) {
                                authViewModel.loginUser(
                                        account.displayName ?: "User",
                                        account.email ?: ""
                                )

                                Toast.makeText(
                                        context,
                                        "Google SignIn Successful",
                                        Toast.LENGTH_SHORT
                                ).show()
                                navController.navigate(MajorScreens.App.name)
                            } else {
                                Toast.makeText(context, "Google SignIn Failed", Toast.LENGTH_SHORT)
                                    .show()
                                authViewModel.loading = false
                            }
                        }
                } catch (e: Exception) {
                    authViewModel.loading = false
                    Toast.makeText(context, "Google SignIn Failed task", Toast.LENGTH_SHORT).show()
                }
            } else {
                authViewModel.loading = false
                Toast.makeText(context, "Google SignIn Failed $exception", Toast.LENGTH_SHORT)
                    .show()
            }
        }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
                Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "logo",
                    colorFilter = ColorFilter.tint(
                            color = Color.Cyan
                    )
            )
            OutlinedButton(onClick = {
                signInLauncher.launch(googleSignInClient.signInIntent)
            }, enabled = !authViewModel.loading) {
                Text("Google SignIn")
            }
        }
        if (authViewModel.loading) {
            CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = Color.Cyan,
                    strokeWidth = 4.dp
            )
        }
    }

}