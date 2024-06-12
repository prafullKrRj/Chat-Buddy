package com.prafull.chatbuddy.mainApp

import com.google.firebase.auth.FirebaseAuth

const val APP_ID = "ca-app-pub-2793481331485078~1557361659"
const val HOME_SCREEN_BANNER_AD = "ca-app-pub-2793481331485078/4083005533"
const val COIN_EARNING_REWARD_AD = "ca-app-pub-2793481331485078/5359061201"
const val PROMPT_LIBRARY_NATIVE_AD = "ca-app-pub-2793481331485078/6912025455"

const val COIN_EARNING_REWARD_AD_TEST = "ca-app-pub-2793481331485078/5359061201"
const val HOME_SCREEN_BANNER_AD_TEST = "ca-app-pub-3940256099942544/9214589741"
const val PROMPT_LIBRARY_NATIVE_AD_TEST = "ca-app-pub-3940256099942544/2247696110"


fun getDetails(): String {
    val mAuth = FirebaseAuth.getInstance()
    return (mAuth.currentUser?.email.toString())
}
