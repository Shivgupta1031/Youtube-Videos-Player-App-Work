package com.devshiv.ytchannel

import android.content.IntentSender
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import com.devshiv.ytchannel.model.VidCatModel
import com.devshiv.ytchannel.screens.HomeScreen
import com.devshiv.ytchannel.screens.SplashScreen
import com.devshiv.ytchannel.screens.VideoPlayScreen
import com.devshiv.ytchannel.ui.theme.PrimaryDarkColor
import com.devshiv.ytchannel.ui.theme.SoulVerseAppTheme
import com.devshiv.ytchannel.utils.ConnectionState
import com.devshiv.ytchannel.utils.Constants
import com.devshiv.ytchannel.utils.NoInternetScreen
import com.devshiv.ytchannel.utils.connectivityState
import com.devshiv.ytchannel.utils.isNetworkConnected
import com.google.android.gms.tasks.Task

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private var appUpdateManager: AppUpdateManager? = null
    private val FLEXIBLE_APP_UPDATE_REQ_CODE = 123
    private var installStateUpdatedListener: InstallStateUpdatedListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            SoulVerseAppTheme {
                AppUI()
            }
        }

        appUpdateManager = AppUpdateManagerFactory.create(applicationContext)

        installStateUpdatedListener = InstallStateUpdatedListener { state ->
            when (state.installStatus()) {
                InstallStatus.DOWNLOADED -> {
                    popupSnackBarForCompleteUpdate()
                }

                InstallStatus.INSTALLED -> {
                    removeInstallStateUpdateListener()
                }

                InstallStatus.FAILED -> {
                    Toast.makeText(
                        this@MainActivity,
                        "Update Failed! Please Go To Play Store And Do Manual Update",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                else -> {
                    Log.d(
                        Constants.TAG,
                        "InstallStateUpdatedListener: state: ${state.installStatus()}"
                    )
                }
            }
        }

        appUpdateManager?.registerListener(installStateUpdatedListener!!)

        checkUpdate()
    }

    private fun checkUpdate() {
        val appUpdateInfoTask: Task<AppUpdateInfo> = appUpdateManager!!.appUpdateInfo
        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            Log.d(Constants.TAG, "checkUpdate: ${appUpdateInfo.installStatus()}")
            when {
                appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                        && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE) -> {
                    startUpdateFlow(appUpdateInfo)
                }

                appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED -> {
                    popupSnackBarForCompleteUpdate()
                }
            }
        }
    }

    private fun startUpdateFlow(appUpdateInfo: AppUpdateInfo) {
        try {
            appUpdateManager!!.startUpdateFlowForResult(
                appUpdateInfo,
                AppUpdateType.FLEXIBLE,
                this,
                FLEXIBLE_APP_UPDATE_REQ_CODE
            )
        } catch (e: IntentSender.SendIntentException) {
            e.printStackTrace()
        }
    }

    private fun popupSnackBarForCompleteUpdate() {
//        Snackbar.make(binding.root, "New Update is ready!", Snackbar.LENGTH_INDEFINITE)
//            .setAction("Install") {
//                appUpdateManager?.completeUpdate()
//            }
//            .setActionTextColor(resources.getColor(R.color.colorAccent))
//            .show()
        Toast.makeText(this, "New Update Available!", Toast.LENGTH_SHORT).show()
    }

    private fun removeInstallStateUpdateListener() {
        appUpdateManager?.unregisterListener(installStateUpdatedListener!!)
    }

    override fun onConfigurationChanged(config: Configuration) {
        super.onConfigurationChanged(config)
        if (config.orientation === Configuration.ORIENTATION_LANDSCAPE) {
            window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        } else {
            window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }
    }
}

@Composable
fun AppUI() {
    val navController = rememberNavController()
    val context = LocalContext.current as MainActivity
    val openFullDialogCustom = remember { mutableStateOf(false) }
    val networkConnectivity by connectivityState()

    BackHandler(enabled = true) {
        context.finish()
    }

    if (networkConnectivity == ConnectionState.Unavailable && !context.isNetworkConnected()) {
        openFullDialogCustom.value = true
        NoInternetScreen(openFullDialogCustom = openFullDialogCustom)
    } else {
        openFullDialogCustom.value = false
        NavHost(
            navController = navController, startDestination = Constants.SPLASH_NAV,
            exitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Companion.Left,
                    animationSpec = tween(500)
                )
            },
            popEnterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Companion.Right,
                    animationSpec = tween(500)
                )
            },
            popExitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Companion.Right,
                    animationSpec = tween(500)
                )
            },
            modifier = Modifier.background(PrimaryDarkColor)
        ) {
            composable(route = Constants.SPLASH_NAV) {
                SplashScreen {
                    navController.navigate(it)
                }
            }
            composable(route = Constants.HOME_NAV) {
                HomeScreen(onNavigateRequired = {
                    navController.navigate(it)
                })
            }
            composable(
                route = Constants.VIDEO_PLAY_NAV + "/{data}/{selectedPos}",
                arguments = listOf(
                    navArgument("data") {
                        type = NavType.StringType
                    },
                    navArgument("selectedPos") {
                        type = NavType.IntType
                    }
                )
            ) { backStackEntry ->
                val encodedData = Uri.decode(backStackEntry.arguments?.getString("data")!!)
                val position = backStackEntry.arguments?.getInt("selectedPos")!!
                val vidCatModel = Gson().fromJson(encodedData, VidCatModel::class.java)

                VideoPlayScreen(
                    onNavigateRequired = { it, back ->
                        if (back) {
                            navController.popBackStack()
                        } else {
                            navController.navigate(it)
                        }
                    },
                    data = vidCatModel,
                    selected = position
                )
            }
        }
    }

}