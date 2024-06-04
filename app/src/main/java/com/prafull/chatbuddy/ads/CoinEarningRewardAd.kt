package com.prafull.chatbuddy.ads

import android.app.Activity
import android.util.Log
import android.widget.Toast
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback

fun rewardedAds(activity: Activity, adWatched: () -> Unit) {
    RewardedAd.load(
            activity,
            "ca-app-pub-3940256099942544/5224354917",
            AdRequest.Builder().build(),
            object : RewardedAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Log.e("TAG", "Ad failed to load")
                }

                override fun onAdLoaded(rewardedAd: RewardedAd) {
                    super.onAdLoaded(rewardedAd)
                    rewardedAd.fullScreenContentCallback = object : FullScreenContentCallback() {
                        override fun onAdDismissedFullScreenContent() {
                            Log.d("TAG", "Ad was dismissed.")
                            rewardedAds(activity, adWatched)
                            onAdDismissedFullScreenContent()
                        }

                        override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                            super.onAdFailedToShowFullScreenContent(p0)
                        }

                        override fun onAdShowedFullScreenContent() {
                            Log.d("TAG", "Ad showed fullscreen content.")
                        }
                    }
                    rewardedAd.show(activity) {
                        Toast.makeText(activity, "You earned 10 coins", Toast.LENGTH_SHORT).show()
                    }
                }

            })
}