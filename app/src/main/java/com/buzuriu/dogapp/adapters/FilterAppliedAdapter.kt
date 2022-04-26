package com.buzuriu.dogapp.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.buzuriu.dogapp.databinding.FilterItemAppliedCellBinding
import com.buzuriu.dogapp.models.IFilterObj
import com.buzuriu.dogapp.views.main.ui.map.MapViewModel

class FilterAppliedAdapter(
    var filterList: ArrayList<IFilterObj>,
    var viewModel: MapViewModel
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = filterList[position]
        if (holder is FilterAppliedAdapter.FilterAppliedViewHolder) {
            (holder).bind(item)
        }
    }

    override fun getItemCount(): Int {
        return filterList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val applicationBinding = FilterItemAppliedCellBinding.inflate(layoutInflater, parent, false)
        return FilterAppliedViewHolder(applicationBinding)
    }

    inner class FilterAppliedViewHolder(var applicationBinding: FilterItemAppliedCellBinding) :
        RecyclerView.ViewHolder(applicationBinding.root) {
        fun bind(item: IFilterObj) {
            applicationBinding.filterItem = item
            applicationBinding.discardFilter.setOnClickListener {
                Log.d("debug", "ajunge aici")
                viewModel.discardFilter(item)
            }
        }
    }
}