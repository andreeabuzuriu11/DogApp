package com.buzuriu.dogapp.bindingadapters

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.databinding.BindingAdapter
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
        if (imageUrl == null) {
            this.setImageDrawable(placeHolder)
        } else {
            Picasso.get()
                .load(imageUrl)
                .placeholder(placeHolder)
                .error(placeHolder)
                .into(this)
        }
    }
}