package com.prafull.chatbuddy.utils.ads

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.gms.ads.nativead.NativeAdView
import com.prafull.chatbuddy.utils.PROMPT_LIBRARY_NATIVE_AD_TEST

@Composable
fun NativeAd() {
    val context = LocalContext.current
    val adLoader = remember {
        AdLoader.Builder(context, PROMPT_LIBRARY_NATIVE_AD_TEST)
            .forNativeAd { nativeAd ->
                // You must call destroy on old ads when you are done with them,
                // otherwise you will leak memory.
                // oldNativeAd?.destroy()
                // oldNativeAd = nativeAd
                // The native ad view will now populate with the set native ad.
                // nativeAdView.setNativeAd(nativeAd)
            }
            .withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Log.d("Ad Failed Native", "Failed to load ad: $adError")
                }
            })
            .withNativeAdOptions(
                    NativeAdOptions.Builder()
                        // Methods in the NativeAdOptions.Builder class can be
                        // used here to specify individual options settings.
                        .build()
            )

            .build()
    }

    AndroidView(
            factory = { context ->
                NativeAdView(context).apply {
                    adLoader.loadAd(AdRequest.Builder().build())
                }
            }
    )
}