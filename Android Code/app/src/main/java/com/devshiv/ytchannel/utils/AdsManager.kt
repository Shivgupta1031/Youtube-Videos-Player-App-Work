package com.devshiv.ytchannel.utils

import android.app.Activity
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxAdListener
import com.applovin.mediation.MaxAdViewAdListener
import com.applovin.mediation.MaxError
import com.applovin.mediation.MaxReward
import com.applovin.mediation.MaxRewardedAdListener
import com.applovin.mediation.ads.MaxAdView
import com.applovin.mediation.ads.MaxInterstitialAd
import com.applovin.mediation.ads.MaxRewardedAd
import com.devshiv.ytchannel.App
import com.devshiv.ytchannel.R
import com.devshiv.ytchannel.utils.Constants.TAG

object AdsManager {

    val timeoutHandler:Handler? = Handler(Looper.getMainLooper())
    val ADS_TIME_OUT = 10000L

    fun loadBannerAd(activity: Activity, layout: LinearLayout, callback: AdsCallback? = null) {
        if (App.settings.show_ads) {
            val adView = MaxAdView(App.settings.banner_id, activity)

            // Set a timeout for banner ad loading
            val timeoutRunnable = Runnable {
                // If banner ad hasn't loaded within the timeout, trigger the failure callback
                layout.visibility = View.GONE
                callback?.onAdFailedToLoad()
                adView.destroy()
            }

            adView.setListener(object : MaxAdViewAdListener {
                override fun onAdExpanded(ad: MaxAd) {}
                override fun onAdCollapsed(ad: MaxAd) {}
                override fun onAdLoaded(ad: MaxAd) {
                    Log.d(TAG, "onAdLoaded: Banner ")
                    // Cancel the timeout as banner ad has loaded
                    timeoutHandler?.removeCallbacks(timeoutRunnable)

                    layout.visibility = View.VISIBLE
                    callback?.onAdLoaded()
                }

                override fun onAdDisplayed(ad: MaxAd) {}
                override fun onAdHidden(ad: MaxAd) {}
                override fun onAdClicked(ad: MaxAd) {}
                override fun onAdLoadFailed(adUnitId: String, error: MaxError) {
                    Log.d(TAG, "onAdLoadFailed: " + error.getMessage())
                    // Cancel the timeout as banner ad loading has failed
                    timeoutHandler?.removeCallbacks(timeoutRunnable)

                    layout.visibility = View.GONE
                    callback?.onAdFailedToLoad()
                }

                override fun onAdDisplayFailed(ad: MaxAd, error: MaxError) {
                    Log.d(TAG, "onAdDisplayFailed: " + error.getMessage())
                    // Cancel the timeout as banner ad loading has failed
                    timeoutHandler?.removeCallbacks(timeoutRunnable)

                    layout.visibility = View.GONE
                    callback?.onAdFailedToLoad()
                }
            })

            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val heightPx = activity.resources.getDimensionPixelSize(R.dimen.banner_height)
            adView.layoutParams = FrameLayout.LayoutParams(width, heightPx)
            layout.addView(adView)
            adView.loadAd()

            // Set a timeout of 12 seconds for banner ad loading
            timeoutHandler?.postDelayed(timeoutRunnable, ADS_TIME_OUT)
        } else {
            layout.visibility = View.GONE
            callback?.onAdFailedToLoad()
        }
    }

    fun showInterstitialAdWithLoading(activity: Activity?, callback: AdsCallback?) {
        if (App.settings.show_ads && activity != null && !activity.isFinishing && App.settings.interstitial_id.isNotEmpty()) {
            callback?.loadingStatus(true)

            val interstitialAd = MaxInterstitialAd(App.settings.interstitial_id, activity)

            // Set a timeout for ad loading
            val timeoutRunnable = Runnable {
                // If ad hasn't loaded within the timeout, trigger the failure callback
                callback?.loadingStatus(false)
                callback?.onAdFailedToLoad()
                interstitialAd?.destroy()
            }

            interstitialAd.setListener(object : MaxAdListener {
                override fun onAdLoaded(ad: MaxAd) {
                    Log.d(TAG, "onAdLoaded: ")
                    // Cancel the timeout as ad has loaded
                    timeoutHandler?.removeCallbacks(timeoutRunnable)

                    callback?.loadingStatus(false)
                    interstitialAd.showAd()
                }

                override fun onAdDisplayed(ad: MaxAd) {
                    Log.d(TAG, "onAdDisplayed: ")
                    callback?.loadingStatus(false)
                }

                override fun onAdHidden(ad: MaxAd) {
                    callback?.onAdDismissed()
                }

                override fun onAdClicked(ad: MaxAd) {
                    callback?.onAdClicked()
                }

                override fun onAdLoadFailed(adUnitId: String, error: MaxError) {
                    Log.d(TAG, "onAdLoadFailed: " + error.getMessage())
                    // Cancel the timeout as ad loading has failed
                    timeoutHandler?.removeCallbacks(timeoutRunnable)

                    callback?.loadingStatus(false)
                    callback?.onAdFailedToLoad()
                }

                override fun onAdDisplayFailed(ad: MaxAd, error: MaxError) {
                    Log.d(TAG, "onAdDisplayFailed: ")
                    // Cancel the timeout as ad loading has failed
                    timeoutHandler?.removeCallbacks(timeoutRunnable)

                    callback?.loadingStatus(false)
                    callback?.onAdFailedToLoad()
                }
            })

            // Load the ad
            interstitialAd.loadAd()

            // Set a timeout of 12 seconds for ad loading
            timeoutHandler?.postDelayed(timeoutRunnable, ADS_TIME_OUT)
        } else {
            callback?.onAdFailedToLoad()
            callback?.loadingStatus(false)
        }
    }

    fun showRewardedAdWithLoading(activity: Activity?, callback: AdsCallback?) {
        if (App.settings.show_ads && activity != null && !activity.isFinishing) {
            callback?.loadingStatus(true)
            val rewardedAd: MaxRewardedAd = MaxRewardedAd.getInstance(App.settings.rewarded_id, activity)

            // Set a timeout for rewarded ad loading
            val timeoutRunnable = Runnable {
                // If rewarded ad hasn't loaded within the timeout, trigger the failure callback
                callback?.onAdFailedToLoad()
                callback?.loadingStatus(false)
                rewardedAd.destroy()
            }

            rewardedAd.setListener(object : MaxRewardedAdListener {
                override fun onRewardedVideoStarted(ad: MaxAd) {}
                override fun onRewardedVideoCompleted(ad: MaxAd) {
                    callback?.onVideoCompleted()
                    callback?.loadingStatus(false)
                }

                override fun onUserRewarded(ad: MaxAd, reward: MaxReward) {}
                override fun onAdLoaded(ad: MaxAd) {
                    // Cancel the timeout as rewarded ad has loaded
                    timeoutHandler?.removeCallbacks(timeoutRunnable)

                    callback?.onAdLoaded()
                    if (rewardedAd.isReady()) {
                        rewardedAd.showAd()
                    }
                }

                override fun onAdDisplayed(ad: MaxAd) {}
                override fun onAdHidden(ad: MaxAd) {
                    callback?.onAdDismissed()
                    callback?.loadingStatus(false)
                }

                override fun onAdClicked(ad: MaxAd) {
                    callback?.onAdClicked()
                }

                override fun onAdLoadFailed(adUnitId: String, error: MaxError) {
                    // Cancel the timeout as rewarded ad loading has failed
                    timeoutHandler?.removeCallbacks(timeoutRunnable)

                    callback?.onAdFailedToLoad()
                    callback?.loadingStatus(false)
                }

                override fun onAdDisplayFailed(ad: MaxAd, error: MaxError) {
                    // Cancel the timeout as rewarded ad loading has failed
                    timeoutHandler?.removeCallbacks(timeoutRunnable)

                    callback?.onAdFailedToLoad()
                    callback?.loadingStatus(false)
                }
            })

            rewardedAd.loadAd()

            // Set a timeout of 12 seconds for rewarded ad loading
            timeoutHandler?.postDelayed(timeoutRunnable, 12000)
        } else {
            callback?.onAdFailedToLoad()
            callback?.loadingStatus(false)
        }
    }

    interface AdsCallback {
        fun onVideoCompleted() {}
        fun onAdLoaded() {}
        fun onAdFailedToLoad() {}
        fun onAdDismissed() {}
        fun onAdClicked() {}
        fun loadingStatus(show: Boolean) {}
    }

}