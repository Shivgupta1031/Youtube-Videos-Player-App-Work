package com.devshiv.ytchannel.screens

import android.os.Build.VERSION.SDK_INT
import android.util.Log
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.request.ImageRequest
import coil.size.Size
import com.applovin.sdk.AppLovinSdk
import com.devshiv.ytchannel.App
import com.devshiv.ytchannel.MainActivity
import com.devshiv.ytchannel.R
import com.devshiv.ytchannel.db.entity.SettingsEntity
import com.devshiv.ytchannel.utils.ApiState
import com.devshiv.ytchannel.utils.Constants
import com.devshiv.ytchannel.utils.Constants.TAG
import com.devshiv.ytchannel.utils.Utils
import com.devshiv.ytchannel.viewmodels.SplashViewModel
import com.onesignal.OneSignal
import com.unity3d.services.core.properties.ClientProperties
import com.devshiv.ytchannel.ui.theme.AccentColor
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onNavigateRequired: (screen: String) -> Unit) {
    val viewModel: SplashViewModel = hiltViewModel()

    var sizeState by remember { mutableStateOf(20.dp) }
    val size by animateDpAsState(
        targetValue = sizeState, tween(
            durationMillis = 500,
            easing = LinearEasing
        ), label = ""
    )

    val context = LocalContext.current as MainActivity

    LaunchedEffect(key1 = true, block = {
        sizeState = 160.dp
        viewModel.settings.collect {
            when (it) {
                is ApiState.Empty -> {
                    Log.d(TAG, "SplashScreen: Empty")
                }

                is ApiState.Loading -> {
                    Log.d(TAG, "SplashScreen: Loading")
                }

                is ApiState.Success<*> -> {
                    Log.d(TAG, "SplashScreen: Success")
                    viewModel.saveSettings(it.data as SettingsEntity)

                    OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE)
                    OneSignal.initWithContext(ClientProperties.getApplicationContext())
                    OneSignal.setAppId(App.settings.one_signal_id)

                    when (Utils.getDayOfWeek()) {
                        1 -> App.settings.interstitial_id = App.settings.interstitial_1
                        2 -> App.settings.interstitial_id = App.settings.interstitial_2
                        3 -> App.settings.interstitial_id = App.settings.interstitial_3
                        4 -> App.settings.interstitial_id = App.settings.interstitial_4
                        5 -> App.settings.interstitial_id = App.settings.interstitial_5
                        6 -> App.settings.interstitial_id = App.settings.interstitial_6
                        7 -> App.settings.interstitial_id = App.settings.interstitial_7
                    }

                    if (App.settings.show_ads) {
                        Utils.updateMetadataInManifest(
                            context,
                            "com.google.android.gms.ads.APPLICATION_ID",
                            App.settings.admob_app_id
                        )
                        Utils.updateMetadataInManifest(
                            context,
                            "applovin.sdk.key",
                            App.settings.applovin_sdk_key
                        )
                        AppLovinSdk.getInstance(ClientProperties.getApplicationContext()).mediationProvider =
                            "max"
                        AppLovinSdk.initializeSdk(
                            ClientProperties.getApplicationContext()
                        ) { Log.d(TAG, "onSdkInitialized: ") }
                    }

                    delay(1000L)
                    onNavigateRequired(Constants.HOME_NAV)
                }

                is ApiState.Failure -> {
                    Log.d(TAG, "SplashScreen: Failure ${it.msg}")
                }
            }
        }
    })

    val imageLoader = ImageLoader.Builder(context)
        .components {
            if (SDK_INT >= 28) {
                add(ImageDecoderDecoder.Factory())
            } else {
                add(GifDecoder.Factory())
            }
        }
        .build()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AccentColor),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Image(
            painter = rememberAsyncImagePainter(
                ImageRequest.Builder(context).data(data = R.drawable.splash_gif).apply(block = {
                    size(Size.ORIGINAL)
                }).build(), imageLoader = imageLoader
            ),
            contentDescription = "App Icon",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .size(size)
                .clip(RoundedCornerShape(10.dp)),
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            modifier = Modifier
                .padding(20.dp, 0.dp, 20.dp),
            text = stringResource(id = R.string.app_name),
            fontSize = 20.sp,
            fontFamily = FontFamily(Font(R.font.main_font)),
            color = Color.White,
            textAlign = TextAlign.Center
        )
    }
}