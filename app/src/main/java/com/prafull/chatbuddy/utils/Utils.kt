package com.prafull.chatbuddy.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.SaverScope
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import coil.size.Precision

class UriSaver : Saver<MutableList<Uri>, List<String>> {
    override fun restore(value: List<String>): MutableList<Uri> = value.map {
        Uri.parse(it)
    }.toMutableList()

    override fun SaverScope.save(value: MutableList<Uri>): List<String> =
        value.map { it.toString() }
}

suspend fun Uri.toBitmaps(context: Context): Bitmap? {
    val imageRequestBuilder = ImageRequest.Builder(context)
    val imageLoader = ImageLoader.Builder(context).build()
    val imageRequest = imageRequestBuilder
        .data(this)
        .size(size = 768)
        .precision(Precision.EXACT)
        .build()
    return try {
        val result = imageLoader.execute(imageRequest)
        if (result is SuccessResult) {
            (result.drawable as BitmapDrawable).bitmap
        } else {
            null
        }
    } catch (e: Exception) {
        null
    }
}
