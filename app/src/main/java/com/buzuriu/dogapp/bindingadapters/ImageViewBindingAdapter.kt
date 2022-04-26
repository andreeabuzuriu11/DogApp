package com.buzuriu.dogapp.bindingadapters

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.buzuriu.dogapp.models.DogObj
import com.buzuriu.dogapp.utils.ImageUtils
import com.squareup.picasso.Picasso

object ImageViewBindingAdapter {
    @BindingAdapter("cb_bitmap")
    @JvmStatic
    fun setImageBitmap(view: ImageView, bitmap: Bitmap?) {
        view.setImageBitmap(bitmap)
    }

    @BindingAdapter("cb_imageUrl", "cb_placeholder", requireAll = true)
    @JvmStatic
    fun ImageView.imageUrl(imageUrl: String?, placeHolder: Drawable) {
        if (imageUrl == null || imageUrl == "") {
            this.setImageDrawable(placeHolder)
        } else {
            Picasso.get()
                .load(imageUrl)
                .placeholder(placeHolder)
                .error(placeHolder)
                .into(this)
        }
    }

    @BindingAdapter("cb_dogImg")
    @JvmStatic
    fun ImageView.imageUrl(dog: DogObj) {
        val placeHolder = ImageUtils.getDogPlaceholder()
        if (dog.imageUrl.isEmpty()) {
            this.setImageResource(placeHolder)
        } else {
            Picasso.get()
                .load(dog.imageUrl)
                .placeholder(placeHolder)
                .error(placeHolder)
                .into(this)
        }
    }
}