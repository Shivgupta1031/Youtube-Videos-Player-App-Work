package com.devshiv.ytchannel.model

import com.google.firebase.Timestamp

data class VideosModel(
    var Category: String = "",
    var Title: String = "",
    var Link: String = "",
    var Created_At: Timestamp? = null
)