package com.buzuriu.dogapp.bindingadapters

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.buzuriu.dogapp.R
import com.buzuriu.dogapp.models.DogObj
import com.buzuriu.dogapp.models.UserObj
import com.buzuriu.dogapp.utils.ImageUtils
import com.squareup.picasso.Picasso

object ImageViewBindingAdapter {
    @BindingAdapter("bitmapBinding")
    @JvmStatic
    fun setImageBitmap(view: ImageView, bitmap: Bitmap?) {
        view.setImageBitmap(bitmap)
    }

    @BindingAdapter("imageBinding")
    @JvmStatic
    fun ImageView.imageUrl(imageUrl: String?) {
        if (imageUrl != "") {
            Picasso.get()
                .load(imageUrl)
                .placeholder(R.drawable.ic_dog_svgrepo_com)
                .into(this)
        }
    }

    @BindingAdapter("dogImageBinding")
    @JvmStatic
    fun ImageView.imageUrl(dog: DogObj?) {
        if (dog?.imageUrl != "")
            Picasso.get()
                .load(dog?.imageUrl)
                .placeholder(R.drawable.ic_dog_svgrepo_com)
                .into(this)
    }

    @BindingAdapter("userImageBinding")
    @JvmStatic
    fun ImageView.imageUrl(user: UserObj?) {
        if (user?.imageUrl != "")
            Picasso.get()
                .load(user?.imageUrl)
                .placeholder(R.drawable.ic_user_account)
                .into(this)
    }
}