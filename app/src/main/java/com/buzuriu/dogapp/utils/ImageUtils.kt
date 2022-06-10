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
        return R.drawable.ic_dog_svgrepo_com
    }

    fun getCompressedImage(image: Bitmap): ByteArray {
        val byteArrOutStream = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.JPEG, 100, byteArrOutStream)
        return byteArrOutStream.toByteArray()
    }

    @RequiresApi(Build.VERSION_CODES.P)
    fun convertToBitmap(activity: Activity, imageUri: Uri?): Bitmap? {
        val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            ImageDecoder.decodeBitmap(ImageDecoder.createSource(activity.contentResolver, imageUri!!))
        } else {
            MediaStore.Images.Media.getBitmap(activity.contentResolver, imageUri)
        }

        return bitmap

    }
}