package com.buzuriu.dogapp.utils

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import com.buzuriu.dogapp.R
import java.io.ByteArrayOutputStream

object ImageUtils {

    fun getDogPlaceholder(): Int {
        return R.drawable.dog_image
    }

    fun getCompressedImage(image: Bitmap): ByteArray {
        val baos = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        return baos.toByteArray()
    }

    @RequiresApi(Build.VERSION_CODES.P)
    fun getBitmap(activity: Activity, imageUri: Uri?): Bitmap? {
        val bitmap = when {
            Build.VERSION.SDK_INT < 28 -> MediaStore.Images.Media.getBitmap(
                activity.contentResolver,
                imageUri
            )
            else -> {
                val source =
                    ImageDecoder.createSource(
                        activity.contentResolver!!,
                        imageUri!!
                    )
                ImageDecoder.decodeBitmap(source)
            }
        }
        return bitmap
    }
}