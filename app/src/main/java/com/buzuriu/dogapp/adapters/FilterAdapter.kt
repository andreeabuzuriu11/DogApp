package com.buzuriu.dogapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.buzuriu.dogapp.databinding.FilterItemCellBinding
import com.buzuriu.dogapp.models.IFilterObj
import com.buzuriu.dogapp.viewModels.FilterMeetingsViewModel


class FilterAdapter(var filterList: ArrayList<IFilterObj>,
                    var viewModel: FilterMeetingsViewModel? = null
) : RecyclerView.Adapter<RecyclerView.ViewHolder>()
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val applicationBinding = FilterItemCellBinding.inflate(layoutInflater, parent, false)
        return FilterViewHolder(applicationBinding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = filterList[position]
        if (holder is FilterAdapter.FilterViewHolder) {
            (holder).bind(item)
        }
    }

    override fun getItemCount(): Int {
        return filterList.size
    }

    inner class FilterViewHolder(var applicationBinding: FilterItemCellBinding) :
        RecyclerView.ViewHolder(applicationBinding.root) {
        fun bind(item : IFilterObj)
        {
            applicationBinding.filterItem = item
            applicationBinding.filterTypeLayout.setOnClickListener {
                viewModel?.selectTime(item)
            }
        }
    }

}