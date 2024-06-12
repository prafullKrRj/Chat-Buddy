package com.prafull.chatbuddy.ads

import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.prafull.chatbuddy.mainApp.HOME_SCREEN_BANNER_AD_TEST
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun BannerAd(id: String = "") {

    AndroidView(
            factory = { context ->
                val adView = AdView(context).apply {
                    setAdSize(AdSize.FULL_BANNER)
                    adUnitId = HOME_SCREEN_BANNER_AD_TEST
                }
                val adRequest = AdRequest.Builder().build()

                // Start a coroutine to load a new ad every 2 minutes
                CoroutineScope(Dispatchers.Main).launch {
                    while (true) {
                        loadAd(adView, adRequest, 0, context)
                        delay(120000) // Delay for 2 minutes
                    }
                }

                adView
            },
            modifier = Modifier.fillMaxWidth(),
    )
}

fun loadAd(adView: AdView, adRequest: AdRequest, attempt: Int, context: Context) {
    if (attempt >= 15) { // Limit the number of retry attempts to 15
        Log.d("Ad Failed Banner", "Failed to load ad after 15 attempts")
        return
    }

    adView.adListener = object : AdListener() {

        override fun onAdFailedToLoad(adError: LoadAdError) {
            super.onAdFailedToLoad(adError)
            // Retry loading the ad after a delay
            CoroutineScope(Dispatchers.Main).launch {
                delay(3000) // Delay for 5 seconds
                loadAd(adView, adRequest, attempt + 1, context)
            }
        }
    }

    adView.loadAd(adRequest)
}