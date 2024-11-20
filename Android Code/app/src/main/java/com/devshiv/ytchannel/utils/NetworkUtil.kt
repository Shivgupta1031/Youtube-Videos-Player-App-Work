package com.devshiv.ytchannel.utils

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.produceState
import androidx.compose.ui.platform.LocalContext
import com.devshiv.ytchannel.utils.Constants.TAG
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn

fun Context.isNetworkConnected(): Boolean {
    val connectivityManager =
        getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    return connectivityManager.isActiveNetworkValidated
}

enum class ConnectionState {
    Available,
    Unavailable;

    companion object {
        fun fromBoolean(isConnected: Boolean): ConnectionState {
            return if (isConnected) Available else Unavailable
        }
    }
}

val ConnectivityManager.isActiveNetworkValidated: Boolean
    @SuppressLint("RestrictedApi", "MissingPermission")
    get() =
        if (Build.VERSION.SDK_INT < 23) {
            false // NET_CAPABILITY_VALIDATED not available until API 23. Used on API 26+.
        } else
            try {
                val network = activeNetwork
                val capabilities = getNetworkCapabilities(network)
                (capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)) ?: false
            } catch (exception: SecurityException) {
                Log.d(TAG, "Unable to validate active network ")
                false
            }

@SuppressLint("MissingPermission")
@ExperimentalCoroutinesApi
private fun Context.observeConnectivityAsFlow() = callbackFlow {
    val connectivityManager =
        getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    fun sendConnectionState() =
        with(connectivityManager.activeNetwork) {
            if (Build.VERSION.SDK_INT < 23) {
                val isConnected = this != null
                trySend(ConnectionState.fromBoolean(isConnected))
            } else {
                val networkCapabilities = connectivityManager.getNetworkCapabilities(this)
                val isConnected =
                    networkCapabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
                trySend(ConnectionState.fromBoolean(isConnected))
            }
        }

    val networkRequest =
        NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .build()

    val networkCallback =
        object : ConnectivityManager.NetworkCallback() {
            override fun onCapabilitiesChanged(
                network: Network,
                networkCapabilities: NetworkCapabilities
            ) {
                Log.d(TAG, "onCapabilitiesChanged: ")
                sendConnectionState()
            }

            override fun onLost(network: Network) {
                Log.d(TAG, "onLost: ")
                sendConnectionState()
            }
        }

    connectivityManager.registerNetworkCallback(networkRequest, networkCallback)

    awaitClose {
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }
}.distinctUntilChanged()
    .flowOn(Dispatchers.IO)


@ExperimentalCoroutinesApi
@Composable
fun connectivityState(): State<ConnectionState> {
    val context = LocalContext.current
    return produceState<ConnectionState>(initialValue = ConnectionState.Unavailable) {
        context.observeConnectivityAsFlow().collect { value = it }
    }
}