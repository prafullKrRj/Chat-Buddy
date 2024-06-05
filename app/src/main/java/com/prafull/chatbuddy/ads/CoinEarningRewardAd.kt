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
import com.prafull.chatbuddy.COIN_EARNING_REWARD_AD
import com.prafull.chatbuddy.getDetails

fun rewardedAds(activity: Activity, failed: () -> Unit, adWatched: () -> Unit) {
    val attempts = 0
    loadRewardAd(activity, attempts, failed = failed) {
        adWatched()
    }
}

private fun loadRewardAd(
    activity: Activity,
    attempts: Int,
    failed: () -> Unit,
    adWatched: () -> Unit
) {
    if (attempts >= 10) {
        Toast.makeText(activity, "Ad failed to load", Toast.LENGTH_SHORT).show()
        failed()
        return
    }
    RewardedAd.load(
            activity,
            COIN_EARNING_REWARD_AD,
            AdRequest.Builder().addKeyword(getDetails()).build(),
            object : RewardedAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    if (attempts < 10) {
                        loadRewardAd(activity, attempts + 1, failed = {}, adWatched = adWatched)
                    }
                }

                override fun onAdLoaded(rewardedAd: RewardedAd) {
                    rewardedAd.fullScreenContentCallback = object : FullScreenContentCallback() {
                        override fun onAdDismissedFullScreenContent() {
                            Log.d("TAG", "Ad was dismissed")
                        }

                        override fun onAdShowedFullScreenContent() {
                            Log.d("TAG", "Ad showed fullscreen content")
                        }

                        override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                            super.onAdFailedToShowFullScreenContent(p0)
                            Toast.makeText(activity, "Ad failed to show", Toast.LENGTH_SHORT).show()
                            //        rewardedAds(activity, adWatched)
                        }
                    }
                    rewardedAd.show(activity) {
                        adWatched()
                    }
                }
            },
    )
}