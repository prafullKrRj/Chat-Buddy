package com.prafull.chatbuddy.ads

import androidx.compose.runtime.Composable
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView

@Composable
fun BannerAd() {
    AndroidView(
            factory = { context ->
                AdView(context).apply {
                    setAdSize(AdSize.FULL_BANNER)
                    adUnitId = "ca-app-pub-3940256099942544/9214589741"
                    loadAd(AdRequest.Builder().build())
                }
            }
    )
}