package com.devshiv.ytchannel.screens

import android.content.Intent
import android.content.pm.ActivityInfo
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIos
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.viewinterop.AndroidViewBinding
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import coil.compose.AsyncImage
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import com.devshiv.ytchannel.MainActivity
import com.devshiv.ytchannel.R
import com.devshiv.ytchannel.databinding.BannerAdsLayoutBinding
import com.devshiv.ytchannel.db.entity.FavouritesEntity
import com.devshiv.ytchannel.model.VidCatModel
import com.devshiv.ytchannel.model.VideosModel
import com.devshiv.ytchannel.ui.theme.AccentColor
import com.devshiv.ytchannel.ui.theme.PrimaryDarkColor
import com.devshiv.ytchannel.ui.theme.PrimaryDarkLightColor
import com.devshiv.ytchannel.utils.AdsManager
import com.devshiv.ytchannel.utils.AutoResizeText
import com.devshiv.ytchannel.utils.Constants
import com.devshiv.ytchannel.utils.Constants.TAG
import com.devshiv.ytchannel.utils.CustomPlayerUiController
import com.devshiv.ytchannel.utils.FontSizeRange
import com.devshiv.ytchannel.utils.LoadingDialog
import com.devshiv.ytchannel.utils.OnLifecycleEvent
import com.devshiv.ytchannel.utils.Utils
import com.devshiv.ytchannel.viewmodels.PlayVideoViewModel

@Composable
fun VideoPlayScreen(
    onNavigateRequired: (screen: String, back: Boolean) -> Unit,
    data: VidCatModel,
    selected: Int
) {

    var viewModel: PlayVideoViewModel = hiltViewModel()

    var selectedPos by remember {
        mutableStateOf(selected)
    }
    val lifecycleOwner = LocalLifecycleOwner.current
    val backPressedDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
    val context = LocalContext.current as MainActivity
    var isFullscreen by remember {
        mutableStateOf(false)
    }

    var addToFav by remember { mutableStateOf<VideosModel?>(null) }
    var playerView by remember { mutableStateOf<YouTubePlayerView?>(null) }
    var youtubePlayer by remember { mutableStateOf<YouTubePlayer?>(null) }
    var videoLink by remember { mutableStateOf(data.videos[selectedPos].Link) }
    var favVideos by remember { mutableStateOf(ArrayList<FavouritesEntity>()) }
    var bannerAdLoaded by remember { mutableStateOf(false) }
    var bannerAdFailed by remember { mutableStateOf(false) }
    var showAdsLoading by remember { mutableStateOf(false) }

    var videoClickTimes by remember { mutableStateOf(0) }

    LaunchedEffect(addToFav, block = {
        if (addToFav != null) {
            if (viewModel.addToFav(addToFav!!)) {
                Toast.makeText(context, "Added Successfully", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Removed Successfully", Toast.LENGTH_SHORT).show()
            }
            addToFav = null
        }
        favVideos = ArrayList(viewModel.getFavVideos())
    })

    BackHandler {
        if (isFullscreen) {
            isFullscreen = false
            playerView!!.wrapContent()
            context.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        } else {
            onNavigateRequired(Constants.HOME_NAV, true)
        }
    }

    DisposableEffect(context, videoLink) {
        val iFramePlayerOptions: IFramePlayerOptions = IFramePlayerOptions.Builder()
            .controls(1)
            .build()
        val listener = object : AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: YouTubePlayer) {
                youtubePlayer = youTubePlayer
                val defaultPlayerUiController =
                    CustomPlayerUiController(playerView!!, youtubePlayer!!)

                defaultPlayerUiController.setFullscreenButtonClickListener {
                    if (isFullscreen) {
                        isFullscreen = false
                        playerView!!.wrapContent()
                        context.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                    } else {
                        isFullscreen = true
                        playerView!!.matchParent()
                        context.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                    }
                }

                playerView!!.setCustomPlayerUi(defaultPlayerUiController.rootView)
                if (videoLink.isNotEmpty()) {
                    youtubePlayer?.loadVideo(Utils.extractVideoId(videoLink)!!, 0f)
                }
            }
        }
        if (youtubePlayer != null && playerView != null) {
            if (videoLink.isNotEmpty()) {
                youtubePlayer?.loadVideo(Utils.extractVideoId(videoLink)!!, 0f)
            }
        } else {
            playerView?.enableAutomaticInitialization = false
            playerView?.initialize(listener, iFramePlayerOptions)
        }
        onDispose {
            playerView?.removeYouTubePlayerListener(listener)
        }
    }

    OnLifecycleEvent { owner, event ->
        when (event) {
            Lifecycle.Event.ON_RESUME -> {
                youtubePlayer?.play()
            }

            Lifecycle.Event.ON_PAUSE -> {
                youtubePlayer?.pause()
            }

            else -> { /* other stuff */
            }
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .background(PrimaryDarkColor)
    ) {

        if (!isFullscreen) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .background(AccentColor)
            ) {
                Image(
                    imageVector = Icons.Default.ArrowBackIos,
                    contentDescription = "",
                    colorFilter = ColorFilter.tint(
                        Color.White
                    ),
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(start = 12.dp, end = 4.dp)
                        .size(32.dp)
                        .padding(4.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .clickable {
                            backPressedDispatcher?.onBackPressed()
                        }
                )

                AutoResizeText(
                    modifier = Modifier
                        .weight(1f)
                        .align(Alignment.CenterVertically)
                        .padding(end = 10.dp),
                    text = data.category,
                    style = MaterialTheme.typography.titleSmall,
                    fontSizeRange = FontSizeRange(
                        min = 14.sp,
                        max = 18.sp,
                    ),
                    fontFamily = FontFamily(Font(R.font.main_font)),
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White,
                    textAlign = TextAlign.Start
                )
            }
        }
        AndroidView(
            factory = { ctx ->
                YouTubePlayerView(ctx).apply {
                    playerView = this
                    lifecycleOwner.lifecycle.addObserver(this)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(6.dp)
                .clip(RoundedCornerShape(12.dp)),
        )
        if (!isFullscreen) {
            Text(
                modifier = Modifier
                    .padding(start = 10.dp, end = 10.dp, bottom = 10.dp, top = 4.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(PrimaryDarkLightColor)
                    .padding(start = 8.dp, end = 8.dp, top = 5.dp, bottom = 5.dp),
                text = data.videos[selectedPos].Title,
                style = MaterialTheme.typography.titleSmall,
                fontSize = 16.sp,
                fontFamily = FontFamily(Font(R.font.main_font)),
                fontWeight = FontWeight.Normal,
                color = Color.White,
                textAlign = TextAlign.Start
            )

            Log.d(TAG, "VideoPlayScreen: $bannerAdFailed")
            if (!bannerAdFailed) {
                AndroidViewBinding(
                    factory = BannerAdsLayoutBinding::inflate,
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                ) {
                    if (!bannerAdLoaded) {
                        bannerAdLoaded = true
                        AdsManager.loadBannerAd(
                            context,
                            adContainer,
                            object : AdsManager.AdsCallback {
                                override fun onAdFailedToLoad() {
                                    super.onAdFailedToLoad()
                                    bannerAdFailed = true
                                }

                                override fun onAdLoaded() {
                                    super.onAdLoaded()
                                    bannerAdFailed = false
                                }
                            })
                    }
                }
            }

            LazyColumn(content = {
                itemsIndexed(data.videos) { index, it ->
                    val isFav = favVideos.any { it.link == data.videos[index].Link }
                    VideoItem2(video = it, isFav, {
                        try {
                            val shareIntent = Intent(Intent.ACTION_SEND)
                            shareIntent.type = "text/plain"
                            shareIntent.putExtra(
                                Intent.EXTRA_SUBJECT,
                                context.getString(R.string.app_name)
                            )
                            var shareMessage = "Download Now\n"
                            shareMessage =
                                shareMessage + "https://play.google.com/store/apps/details?id=" + context.packageName
                            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
                            context.startActivity(Intent.createChooser(shareIntent, "choose one"))
                        } catch (e: Exception) {
                            Toast.makeText(context, "Unable To Share App!", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }, {
                        addToFav = data.videos[index]
                        AdsManager.showInterstitialAdWithLoading(context,
                            object : AdsManager.AdsCallback {
                                override fun loadingStatus(show: Boolean) {
                                    super.loadingStatus(show)
                                    showAdsLoading = show
                                }

                            })
                    }) {
                        videoClickTimes++
                        selectedPos = index
                        videoLink = data.videos[index].Link
                        if (videoClickTimes % 3 == 0) {
                            videoClickTimes = 0
                            AdsManager.showInterstitialAdWithLoading(
                                context,
                                object : AdsManager.AdsCallback {
                                    override fun loadingStatus(show: Boolean) {
                                        super.loadingStatus(show)
                                        showAdsLoading = show
                                    }
                                })
                        }
                    }
                }
            })
        }
    }

    LoadingDialog(showLoad = showAdsLoading) {
        showAdsLoading = false
    }

}

@Composable
fun VideoItem2(
    video: VideosModel,
    isFav: Boolean = false,
    onShareClick: () -> Unit,
    onFavClick: () -> Unit,
    onItemClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .clickable { onItemClick() }
    ) {
        AsyncImage(
            model = Utils.getYouTubeThumbnailUrl(video.Link),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .width(140.dp)
                .fillMaxHeight()
                .padding(start = 12.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(AccentColor)
        )
        Column(
            modifier = Modifier
                .weight(1f)
        ) {
            AutoResizeText(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.7f)
                    .padding(start = 8.dp, end = 12.dp, top = 2.dp, bottom = 10.dp),
                text = video.Title,
                fontFamily = FontFamily(Font(R.font.main_font)),
                fontWeight = FontWeight.Light,
                maxLines = 2,
                style = MaterialTheme.typography.titleSmall,
                color = Color.White,
                fontSizeRange = FontSizeRange(
                    min = 12.sp,
                    max = 14.sp,
                ),
                textAlign = TextAlign.Start,
            )
            Row(
                modifier = Modifier
                    .wrapContentWidth()
                    .weight(0.3f)
                    .padding(end = 12.dp)
                    .align(Alignment.End)
            ) {
                Image(
                    imageVector = Icons.Default.Share,
                    contentDescription = "",
                    colorFilter = ColorFilter.tint(
                        Color.White
                    ),
                    modifier = Modifier
                        .padding(start = 12.dp, end = 8.dp)
                        .size(22.dp)
                        .clickable {
                            onShareClick()
                        }
                )
                Image(
                    imageVector = if (isFav) {
                        Icons.Default.Favorite
                    } else {
                        Icons.Default.FavoriteBorder
                    },
                    contentDescription = "",
                    colorFilter = ColorFilter.tint(
                        AccentColor
                    ),
                    modifier = Modifier
                        .padding(start = 12.dp, end = 8.dp)
                        .size(22.dp)
                        .clickable {
                            onFavClick()
                        }
                )
            }
        }
    }

    Spacer(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 6.dp, bottom = 6.dp, start = 6.dp, end = 6.dp)
            .height(2.dp)
            .background(PrimaryDarkLightColor)
    )
}

@Preview(showSystemUi = true)
@Composable
fun PreviewVideoPlay() {
    VideoPlayScreen({ it, back -> }, VidCatModel("", ArrayList()), 0)
}