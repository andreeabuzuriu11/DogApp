package com.buzuriu.dogapp.bindingadapters

import android.R
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener

object SpinnerBindingAdapter {
    @BindingAdapter("cb_entries")
    @JvmStatic
    fun Spinner.setEntries(entries: List<Any>?) {
        if (entries != null) {
            val arrayAdapter = ArrayAdapter(context, R.layout.simple_spinner_item, entries)
            arrayAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
            this.adapter = arrayAdapter
        }
    }

    @BindingAdapter("cb_selectedValue")
    @JvmStatic
    fun Spinner.setSelectedValue(selectedValue: Any?) {
        if (adapter != null) {
            val position = (adapter as ArrayAdapter<Any>).getPosition(selectedValue)
            this.setSelection(position, false)
            this.tag = position
        }
    }

    @BindingAdapter("cb_selectedValueAttrChanged")
    @JvmStatic
    fun Spinner.setInverseBindingListener(inverseBindingListener: InverseBindingListener?) {
        if (inverseBindingListener == null) {
            this.onItemSelectedListener = null
        } else {
            this.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View,
                    position: Int,
                    id: Long
                ) {
                    if (tag != position) {
                        inverseBindingListener.onChange()
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>) {}
            }
        }
    }

    @JvmStatic
    @InverseBindingAdapter(attribute = "cb_selectedValue")
    fun Spinner.getSelectedValue(): String? {
        return this.selectedItem as String
    }
}