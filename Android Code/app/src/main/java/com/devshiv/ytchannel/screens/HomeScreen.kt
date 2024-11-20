package com.devshiv.ytchannel.screens

import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidViewBinding
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.calculateCurrentOffsetForPage
import com.google.accompanist.pager.rememberPagerState
import com.google.gson.Gson
import com.devshiv.ytchannel.App
import com.devshiv.ytchannel.MainActivity
import com.devshiv.ytchannel.R
import com.devshiv.ytchannel.databinding.BannerAdsLayoutBinding
import com.devshiv.ytchannel.db.entity.FavouritesEntity
import com.devshiv.ytchannel.model.VidCatModel
import com.devshiv.ytchannel.model.VideosModel
import com.devshiv.ytchannel.ui.theme.AccentColor
import com.devshiv.ytchannel.ui.theme.PrimaryDarkColor
import com.devshiv.ytchannel.ui.theme.PrimaryLightColor
import com.devshiv.ytchannel.utils.AdsManager
import com.devshiv.ytchannel.utils.ApiState
import com.devshiv.ytchannel.utils.AutoResizeText
import com.devshiv.ytchannel.utils.Constants
import com.devshiv.ytchannel.utils.FontSizeRange
import com.devshiv.ytchannel.utils.LoadingDialog
import com.devshiv.ytchannel.utils.MyDropDownMenu
import com.devshiv.ytchannel.utils.ProgressIndicatorLoading
import com.devshiv.ytchannel.utils.Utils
import com.devshiv.ytchannel.viewmodels.HomeViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.yield
import kotlin.math.absoluteValue

@Composable
fun HomeScreen(
    onNavigateRequired: (screen: String) -> Unit,
) {
    val viewModel: HomeViewModel = hiltViewModel()
    val context = LocalContext.current as MainActivity

    var bannersList: ArrayList<VideosModel> by remember {
        mutableStateOf(ArrayList<VideosModel>())
    }

    val vidCategoriesList: ArrayList<VidCatModel> by remember {
        mutableStateOf(ArrayList<VidCatModel>())
    }
    var bannerLoading by remember {
        mutableStateOf(false)
    }
    var videosLoading by remember {
        mutableStateOf(false)
    }
    var openFavs by remember {
        mutableStateOf(false)
    }
    var bannerAdLoaded by remember { mutableStateOf(false) }
    var bannerAdFailed by remember { mutableStateOf(false) }
    var showAdsLoading by remember { mutableStateOf(false) }

    BackHandler(enabled = true) {
        context.finish()
    }

    LaunchedEffect(key1 = true, block = {
        viewModel.banners.collect {
            when (it) {
                is ApiState.Empty -> {
                    Log.d(Constants.TAG, "banners: Empty")
                    videosLoading = false
                }

                is ApiState.Loading -> {
                    Log.d(Constants.TAG, "banners: Loading")
                    bannerLoading = true
                }

                is ApiState.Success<*> -> {
                    Log.d(Constants.TAG, "banners: Success")
                    if (bannersList.isNotEmpty()) {
                        bannersList.clear()
                    }
                    bannersList = ArrayList(it.data as List<VideosModel>)
                    bannersList.sortByDescending { it.Created_At }
                    bannerLoading = false
                }

                is ApiState.Failure -> {
                    Log.d(Constants.TAG, "banners: Failure ${it.msg}")
                    videosLoading = false
                }
            }
        }
    })

    LaunchedEffect(key1 = true, block = {
        viewModel.vidCategories.collect {
            when (it) {
                is ApiState.Empty -> {
                    Log.d(Constants.TAG, "vidCategories: Empty")
                    videosLoading = false
                }

                is ApiState.Loading -> {
                    Log.d(Constants.TAG, "vidCategories: Loading")
                    videosLoading = true
                }

                is ApiState.Success<*> -> {
                    Log.d(Constants.TAG, "vidCategories: Success")
                    if (vidCategoriesList.isNotEmpty()) {
                        vidCategoriesList.clear()
                    }
                    vidCategoriesList.addAll(it.data as List<VidCatModel>)
                    videosLoading = false
                }

                is ApiState.Failure -> {
                    Log.d(Constants.TAG, "vidCategories: Failure ${it.msg}")
                    videosLoading = false
                }
            }
        }
    })

    LaunchedEffect(openFavs, block = {
        if (openFavs) {
            val data = viewModel.getFavVideos()
            if (data == null || data.isEmpty()) {
                Toast.makeText(context, "No Favourite Videos Found!", Toast.LENGTH_SHORT).show()
            } else {
                val videosList = ArrayList<VideosModel>()
                for (video: FavouritesEntity in data) {
                    videosList.add(VideosModel("Favourites", video.title, video.link))
                }
                var favModel = VidCatModel("Favourites", videosList)
                val encodedData = Gson().toJson(favModel)
                val dest =
                    Constants.VIDEO_PLAY_NAV + "/${Uri.encode(encodedData)}/0"
                onNavigateRequired(dest)
            }
            openFavs = false
        }
    })

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .background(PrimaryDarkColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .background(AccentColor)
        ) {
            Text(
                modifier = Modifier
                    .weight(1f)
                    .align(Alignment.CenterVertically)
                    .padding(start = 14.dp),
                text = stringResource(id = R.string.app_name),
                style = MaterialTheme.typography.titleSmall,
                fontSize = 22.sp,
                fontFamily = FontFamily(Font(R.font.main_font)),
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
                textAlign = TextAlign.Start
            )

            Image(
                imageVector = Icons.Default.Favorite,
                contentDescription = "",
                colorFilter = ColorFilter.tint(
                    Color.White
                ),
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(start = 12.dp, end = 8.dp)
                    .size(28.dp)
                    .clickable {
                        openFavs = true
                    }
            )

            MyDropDownMenu(
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(start = 12.dp, end = 8.dp)
                    .size(28.dp)
                    .clickable {
                        openFavs = true
                    },
                content = {
                    DropdownMenuItem(
                        text = { Text("Privacy Policy") },
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW)
                            intent.data = Uri.parse(App.settings.privacy_policy)
                            context.startActivity(intent)
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Share App") },
                        onClick = {
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
                                context.startActivity(
                                    Intent.createChooser(
                                        shareIntent,
                                        "choose one"
                                    )
                                )
                            } catch (e: Exception) {
                                Toast.makeText(context, "Unable To Share App!", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Rate Us") },
                        onClick = {
                            val shareIntent = Intent(Intent.ACTION_VIEW)
                            shareIntent.data =
                                Uri.parse("https://play.google.com/store/apps/details?id=" + context.packageName)
                            context.startActivity(shareIntent)
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Exit") },
                        onClick = { context.finish() }
                    )
                })
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
        ) {
            if (bannerLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    ProgressIndicatorLoading(
                        progressIndicatorSize = 50.dp,
                        progressIndicatorColor = Color.White
                    )
                }
            } else {
                if (bannersList.size > 0) {
                    ImageSlider(onNavigateRequired, bannersList) { page ->
                        AdsManager.showInterstitialAdWithLoading(
                            context,
                            object : AdsManager.AdsCallback {
                                override fun loadingStatus(show: Boolean) {
                                    super.loadingStatus(show)
                                    showAdsLoading = show
                                }

                                override fun onAdDismissed() {
                                    val data: Pair<Int, Int>? = findVideoData()
                                    navigateToVideo(data)
                                }

                                override fun onAdFailedToLoad() {
                                    val data: Pair<Int, Int>? = findVideoData()
                                    navigateToVideo(data)
                                }

                                private fun findVideoData(): Pair<Int, Int>? {
                                    return vidCategoriesList
                                        .asSequence()
                                        .mapIndexed { categoryIndex, vidCatModel ->
                                            val videoIndex =
                                                vidCatModel.videos.indexOfFirst { videosModel ->
                                                    videosModel.Link.contains(
                                                        bannersList[page].Link,
                                                        ignoreCase = true
                                                    )
                                                }
                                            categoryIndex to videoIndex
                                        }
                                        .firstOrNull { (categoryIndex, videoIndex) ->
                                            vidCategoriesList[categoryIndex].category.contains(
                                                bannersList[page].Category,
                                                ignoreCase = true
                                            )
                                        }
                                }

                                private fun navigateToVideo(data: Pair<Int, Int>?) {
                                    data?.let {
                                        val encodedData = Gson().toJson(vidCategoriesList[it.first])
                                        val dest =
                                            "${Constants.VIDEO_PLAY_NAV}/${Uri.encode(encodedData)}/${it.second}"
                                        onNavigateRequired(dest)
                                    }
                                }
                            })
                    }
                }
            }
            Spacer(modifier = Modifier.height(4.dp))

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

            if (videosLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    ProgressIndicatorLoading(
                        progressIndicatorSize = 70.dp,
                        progressIndicatorColor = Color.White
                    )
                }
            } else {
                if (vidCategoriesList.size > 0) {
                    LazyColumn(content = {
                        items(vidCategoriesList) {
                            VidCategoryItem(onNavigateRequired = onNavigateRequired, data = it)
                        }
                    })
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        Text(
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .padding(start = 14.dp),
                            text = "No Videos Found",
                            style = MaterialTheme.typography.titleSmall,
                            fontSize = 22.sp,
                            fontFamily = FontFamily(Font(R.font.main_font)),
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            textAlign = TextAlign.Start
                        )

                        Image(
                            imageVector = Icons.Default.HourglassEmpty,
                            contentDescription = "",
                            colorFilter = ColorFilter.tint(
                                Color.White
                            ),
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .padding(start = 12.dp, end = 8.dp)
                                .size(28.dp)
                        )
                    }
                }
            }
        }
    }

    LoadingDialog(showLoad = showAdsLoading){
        showAdsLoading = false
    }

}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun ImageSlider(
    onNavigateRequired: (screen: String) -> Unit,
    bannersList: List<VideosModel>,
    onBannerClick: (page: Int) -> Unit
) {

    val pagerState = rememberPagerState(initialPage = 0)

    LaunchedEffect(Unit) {
        while (true) {
            yield()
            delay(2600)
            pagerState.animateScrollToPage(
                page = (pagerState.currentPage + 1) % (pagerState.pageCount)
            )
        }
    }

    Column {
        HorizontalPager(
            count = bannersList.size,
            state = pagerState,
            contentPadding = PaddingValues(top = 12.dp, start = 12.dp, end = 12.dp),
            modifier = Modifier
                .height(200.dp)
                .fillMaxWidth()
        ) { page ->
            Box(
                modifier = Modifier
                    .graphicsLayer {
                        val pageOffset = calculateCurrentOffsetForPage(page).absoluteValue

                        lerp(
                            start = 0.85f,
                            stop = 1f,
                            fraction = 1f - pageOffset.coerceIn(0f, 1f)
                        ).also { scale ->
                            scaleX = scale
                            scaleY = scale
                        }

                        alpha = lerp(
                            start = 0.5f,
                            stop = 1f,
                            fraction = 1f - pageOffset.coerceIn(0f, 1f)
                        )
                    }
                    .clip(RoundedCornerShape(10.dp))
                    .background(AccentColor)
                    .clickable {
                        onBannerClick(page)
                    }
            ) {
                AsyncImage(
                    model = Utils.getYouTubeThumbnailUrl(bannersList[page].Link),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.FillBounds
                )
                AutoResizeText(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .background(PrimaryDarkColor.copy(alpha = 0.5f))
                        .padding(start = 6.dp, end = 6.dp, top = 2.dp, bottom = 2.dp),
                    text = bannersList[page].Title,
                    style = MaterialTheme.typography.titleSmall,
                    fontSizeRange = FontSizeRange(
                        min = 14.sp,
                        max = 18.sp
                    ),
                    maxLines = 2,
                    fontFamily = FontFamily(Font(R.font.main_font)),
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                )
            }
        }
        HorizontalPagerIndicator(
            pagerState = pagerState,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(10.dp),
            activeColor = AccentColor,
            inactiveColor = PrimaryLightColor
        )
    }
}

fun lerp(start: Float, stop: Float, fraction: Float): Float {
    return start + fraction * (stop - start)
}

@Preview(showSystemUi = true)
@Composable
fun PreviewHome() {
    HomeScreen({})
}