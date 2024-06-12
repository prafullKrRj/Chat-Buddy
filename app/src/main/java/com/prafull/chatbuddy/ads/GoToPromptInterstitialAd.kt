package com.prafull.chatbuddy.ads

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

// Interstitial Ad

fun loadInterstitialAd(
    context: Context,
    activity: Activity,
    onAdLoaded: () -> Unit,
    onAdFailedToLoad: () -> Unit
) {
    var mInterstitialAd: InterstitialAd? = null
    val adRequest = AdRequest.Builder().build()

    InterstitialAd.load(
            context,
            "ca-app-pub-3940256099942544/1033173712",
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Log.d("loadInterstitialAd", adError.message)
                    mInterstitialAd = null
                }

                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    Log.d("loadInterstitialAd", "Ad was loaded.")
                    mInterstitialAd = interstitialAd
                    mInterstitialAd?.let { ad ->
                        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                            override fun onAdDismissedFullScreenContent() {
                                super.onAdDismissedFullScreenContent()
                                mInterstitialAd = null
                            }

                            override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                                super.onAdFailedToShowFullScreenContent(p0)
                                Log.d("loadInterstitialAd", "Ad failed to show.")
                            }

                            override fun onAdImpression() {
                                super.onAdImpression()
                                Log.d("loadInterstitialAd", "Ad impression.")
                            }

                            override fun onAdClicked() {
                                super.onAdClicked()
                                Log.d("loadInterstitialAd", "Ad was clicked.")
                            }
                        }
                        ad.show(activity)
                        onAdLoaded()
                    } ?: kotlin.run {
                        onAdFailedToLoad()
                        Log.d("loadInterstitialAd", "The interstitial ad wasn't loaded yet.")
                    }
                }
            }
    )

}