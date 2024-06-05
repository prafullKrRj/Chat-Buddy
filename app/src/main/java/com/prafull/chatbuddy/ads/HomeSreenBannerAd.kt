package com.prafull.chatbuddy.ads

import androidx.compose.runtime.Composable
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.prafull.chatbuddy.HOME_SCREEN_BANNER_AD_TEST

@Composable
fun BannerAd() {
    AndroidView(
            factory = { context ->
                AdView(context).apply {
                    setAdSize(AdSize.FULL_BANNER)
                    adUnitId = HOME_SCREEN_BANNER_AD_TEST
                    loadAd(AdRequest.Builder().build())
                }
            }
    )
}