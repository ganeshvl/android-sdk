package com.mapfit.mapfitsdk.utils

import android.graphics.drawable.Drawable
import android.util.Log
import com.mapfit.mapfitsdk.MapOptions.Companion.MAP_MAX_ZOOM
import com.mapfit.mapfitsdk.MapOptions.Companion.MAP_MIN_ZOOM
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import java.net.URL


/**
 * Created by dogangulcan on 12/21/17.
 */


fun isValidZoomLevel(zoomLevel: Float): Boolean {
    return if (zoomLevel !in MAP_MIN_ZOOM..MAP_MAX_ZOOM) {
        Log.w("Mapfit", "Zoom level must be between $MAP_MIN_ZOOM and $MAP_MAX_ZOOM")
        false
    } else {
        true
    }
}

fun loadImageFromUrl(url: String): Deferred<Drawable?> = async {
    if (isValidImageUrl(url)) {
        try {
            val inputStream = URL(url).openStream()
            val drawable = Drawable.createFromStream(inputStream, "")

            drawable
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    } else {
        Log.w("Mapfit", "Invalid image url $url")
        null
    }
}

fun isValidImageUrl(url: String): Boolean {
    val imgRg = """(?:([^:/?#]+):)?(?://([^/?#]*))?([^?#]*\.(?:jpg|gif|png))""".toRegex()
    return imgRg.containsMatchIn(url)
}
