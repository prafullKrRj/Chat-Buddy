package com.prafull.chatbuddy.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.SaverScope
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import coil.size.Precision
import java.io.ByteArrayOutputStream
import java.util.Base64

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

fun Bitmap.toBase64(): String? {
    val outputStream = ByteArrayOutputStream()
    this.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
    val byteArray = outputStream.toByteArray()
    return Base64.getEncoder().encodeToString(byteArray)
}

fun String.base64ToBitmap(): Bitmap? {
    return try {
        val decodedString = Base64.getDecoder().decode(this)
        BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
    } catch (e: IllegalArgumentException) {
        e.printStackTrace()
        null
    }
}