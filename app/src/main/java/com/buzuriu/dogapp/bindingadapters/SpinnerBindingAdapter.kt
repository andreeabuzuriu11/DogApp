package com.buzuriu.dogapp.bindingadapters

import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.widget.AppCompatSpinner
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener


object SpinnerBindingAdapter {

    @BindingAdapter("itemSelected")
    @JvmStatic
    fun Spinner.setSelectionItem(selectedValue: Any?) {
        val position = (adapter as ArrayAdapter<Any>).getPosition(selectedValue)
        this.setSelection(position, false)
        this.tag = position
    }

    @JvmStatic
    @InverseBindingAdapter(attribute = "itemSelected")
    fun Spinner.setSelectionItem(): String {
        return this.selectedItem as String
    }

    @BindingAdapter("itemSelectedAttrChanged")
    @JvmStatic
    fun Spinner.bindSpinnerData(
        newTextAttrChanged: InverseBindingListener?
    ) {
        this.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View,
                position: Int,
                id: Long
            ) {
                newTextAttrChanged?.onChange()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }
}