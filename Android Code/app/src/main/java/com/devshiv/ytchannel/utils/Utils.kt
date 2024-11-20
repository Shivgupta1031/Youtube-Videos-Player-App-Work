package com.devshiv.ytchannel.utils

import android.app.Activity
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.util.Log
import com.devshiv.ytchannel.utils.Constants.TAG
import java.lang.reflect.Field
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.regex.Pattern
import kotlin.random.Random

class Utils {

    companion object {
        fun randomColor(): Int {
            val red = Random.nextInt(0, 256)
            val green = Random.nextInt(0, 256)
            val blue = Random.nextInt(0, 256)

            return android.graphics.Color.rgb(red, green, blue)
        }

        fun getTodayDate(): String {
            val pattern = "yyyy-MM-dd"
            val dateFormat = SimpleDateFormat(pattern, Locale.getDefault())
            val currentDate = Date()
            return dateFormat.format(currentDate)
        }

        fun isDateGreaterThanToday(dateString: String): Boolean {
            if (dateString.trim().isEmpty()) {
                return false
            }
            val pattern = "yyyy-MM-dd"

            // Parse the input date string
            val inputDate = parseDateString(dateString, pattern)

            // Get the current date
            val currentDate = Date()

            inputDate.hours = 0
            inputDate.minutes = 0
            inputDate.seconds = 0

            currentDate.hours = 0
            currentDate.minutes = 0
            currentDate.seconds = 0

            // Compare the input date with the current date
            return inputDate.date >= currentDate.date
        }

        private fun parseDateString(dateString: String, pattern: String): Date {
            val dateFormat = SimpleDateFormat(pattern, Locale.getDefault())
            return dateFormat.parse(dateString) ?: Date()
        }

        fun updateMetadataInManifest(activity: Activity, key: String?, value: String?) {
            try {
                val ai = activity.packageManager.getApplicationInfo(
                    activity.packageName,
                    PackageManager.GET_META_DATA
                )
                val bundle = ai.metaData
                bundle.putString(key, value)
                // Reflectively update the metadata
                val metaData = Bundle()
                metaData.putBundle("metaData", bundle)
                val metaDataField: Field = ApplicationInfo::class.java.getDeclaredField("metaData")
                metaDataField.setAccessible(true)
                metaDataField.set(ai, metaData)
            } catch (e: Exception) {
                Log.d(TAG, "updateMetadataInManifest Error: " + e.message)
            }
        }

        fun getYouTubeThumbnailUrl(youtubeUrl: String): String? {
            val videoId = extractVideoId(youtubeUrl)
            return if (videoId != null) {
                "https://img.youtube.com/vi/$videoId/mqdefault.jpg"
            } else {
                null
            }
        }

        fun extractVideoId(youtubeUrl: String): String? {
            val pattern =
                "(?<=watch\\?v=|/videos/|embed\\/|youtu.be\\/|\\/v\\/|\\/e\\/|watch\\?v%3D|watch\\?feature=player_embedded&v=|%2Fvideos%2F|embed\\/|youtu.be\\/|\\/v\\/|\\/e\\/|watch\\?v=|%2Fvideos%2F|embed\\/|youtu.be\\/|\\/v\\/|\\/e\\/%2Fvideos%2F|embed\\/|youtu.be\\/|\\/v\\/|\\/e\\/?%s=?)([\\w-]{11})(?![^&\\?]*\\))"

            val compiledPattern = Pattern.compile(pattern)
            val matcher = compiledPattern.matcher(youtubeUrl)

            return if (matcher.find()) {
                matcher.group(1)
            } else {
                null
            }
        }

        fun getDayOfWeek(): Int {
            val calendar = Calendar.getInstance()
            val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
            // Adjust to return values from 1 to 7 instead of 1 to 8 (where 1 is Sunday and 7 is Saturday)
            return if (dayOfWeek == Calendar.SUNDAY) 7 else dayOfWeek - 1
        }

        fun isInternetAvailable(context: Context): Boolean {
            var result = false
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val networkCapabilities = connectivityManager.activeNetwork ?: return false
                val actNw = connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
                result = when {
                    actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                    actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                    actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                    else -> false
                }
            } else {
                connectivityManager.run {
                    connectivityManager.activeNetworkInfo?.run {
                        result = when (type) {
                            ConnectivityManager.TYPE_WIFI -> true
                            ConnectivityManager.TYPE_MOBILE -> true
                            ConnectivityManager.TYPE_ETHERNET -> true
                            else -> false
                        }
                    }
                }
            }
            return result
        }
    }
}