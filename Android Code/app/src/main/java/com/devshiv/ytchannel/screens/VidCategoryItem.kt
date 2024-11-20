package com.devshiv.ytchannel.screens

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.google.gson.Gson
import com.devshiv.ytchannel.MainActivity
import com.devshiv.ytchannel.R
import com.devshiv.ytchannel.model.VidCatModel
import com.devshiv.ytchannel.model.VideosModel
import com.devshiv.ytchannel.ui.theme.AccentColor
import com.devshiv.ytchannel.ui.theme.PrimaryDarkLightColor
import com.devshiv.ytchannel.utils.AdsManager
import com.devshiv.ytchannel.utils.AutoResizeText
import com.devshiv.ytchannel.utils.Constants
import com.devshiv.ytchannel.utils.FontSizeRange
import com.devshiv.ytchannel.utils.LoadingDialog
import com.devshiv.ytchannel.utils.Utils

@Composable
fun VidCategoryItem(
    onNavigateRequired: (screen: String) -> Unit,
    data: VidCatModel
) {
    val context = LocalContext.current as MainActivity
    var showAdsLoading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            AutoResizeText(
                modifier = Modifier
                    .weight(1f)
                    .align(Alignment.CenterVertically)
                    .padding(start = 14.dp, end = 20.dp),
                text = data.category,
                style = MaterialTheme.typography.titleSmall,
                fontSizeRange = FontSizeRange(
                    min = 18.sp,
                    max = 20.sp,
                ),
                maxLines = 1,
                fontFamily = FontFamily(Font(R.font.main_font)),
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
                textAlign = TextAlign.Start
            )
            Text(
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(top = 6.dp, bottom = 6.dp, start = 10.dp, end = 14.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .clickable {
                        val encodedData = Gson().toJson(data)
                        val dest =
                            Constants.VIDEO_PLAY_NAV + "/${Uri.encode(encodedData)}/0"
                        onNavigateRequired(dest)
                    },
                text = "See More",
                style = MaterialTheme.typography.titleSmall,
                fontSize = 14.sp,
                fontFamily = FontFamily(Font(R.font.main_font)),
                fontWeight = FontWeight.SemiBold,
                color = AccentColor,
                textAlign = TextAlign.Start
            )
        }

        LazyRow(modifier = Modifier.padding(start = 10.dp), content = {
            itemsIndexed(data.videos.take(10)) { index, it ->
                VideoItem(video = it) {
                    AdsManager.showInterstitialAdWithLoading(
                        context,
                        object : AdsManager.AdsCallback {
                            override fun loadingStatus(show: Boolean) {
                                super.loadingStatus(show)
                                showAdsLoading = show
                            }

                            override fun onAdDismissed() {
                                navigateToVideo()
                            }

                            override fun onAdFailedToLoad() {
                                navigateToVideo()
                            }

                            private fun navigateToVideo() {
                                data.let {
                                    val encodedData = Gson().toJson(it)
                                    val dest =
                                        Constants.VIDEO_PLAY_NAV + "/${Uri.encode(encodedData)}/$index"
                                    onNavigateRequired(dest)
                                }
                            }
                        })
                }
            }
        })

        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 6.dp, start = 6.dp, end = 6.dp)
                .height(2.dp)
                .background(PrimaryDarkLightColor)
        )
    }

    LoadingDialog(showLoad = showAdsLoading){
        showAdsLoading = false
    }

}

@Composable
fun VideoItem(
    video: VideosModel,
    onNavigateRequired: () -> Unit
) {
    Column(
        modifier = Modifier
            .width(180.dp)
            .padding(end = 10.dp)
            .clickable {
                onNavigateRequired()
            }
    ) {
        AsyncImage(
            model = Utils.getYouTubeThumbnailUrl(video.Link),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .height(120.dp)
                .fillMaxWidth()
                .padding(top = 8.dp, bottom = 8.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(AccentColor)
        )
        AutoResizeText(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 2.dp, bottom = 10.dp),
            text = video.Title,
            fontFamily = FontFamily(Font(R.font.main_font)),
            fontWeight = FontWeight.Normal,
            maxLines = 2,
            style = MaterialTheme.typography.titleSmall,
            color = Color.White,
            fontSizeRange = FontSizeRange(
                min = 14.sp,
                max = 16.sp,
            ),
            textAlign = TextAlign.Start,
        )
    }
}

@Preview(showSystemUi = true)
@Composable
fun ScreenPreview() {
    VidCategoryItem(onNavigateRequired = {}, VidCatModel("", ArrayList()))
}