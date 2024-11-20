package com.devshiv.ytchannel.model

import com.google.firebase.Timestamp
import java.io.Serializable

data class VidCatModel(
    var category: String = "",
    var videos: List<VideosModel>,
    var Created_At:Timestamp? = null
) : Serializable