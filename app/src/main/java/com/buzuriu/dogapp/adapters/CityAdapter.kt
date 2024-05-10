package com.buzuriu.dogapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.buzuriu.dogapp.databinding.CityCellBinding
import com.buzuriu.dogapp.models.CityObj
import com.buzuriu.dogapp.viewModels.SelectCityViewModel

class CityAdapter(
    var citiesList: ArrayList<CityObj>,
    private var viewModel: SelectCityViewModel? = null
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun getItemCount(): Int {
        return citiesList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val applicationBinding =
            CityCellBinding.inflate(layoutInflater, parent, false)
        return CityCellViewHolder(applicationBinding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val cityObj = citiesList[position];
        (holder as CityCellViewHolder).bind(cityObj)
    }

    inner class CityCellViewHolder(var applicationBinding: CityCellBinding) :
        RecyclerView.ViewHolder(applicationBinding.root) {
        fun bind(item: CityObj) {
            applicationBinding.cityObj = item
            applicationBinding.cityCellLayout.setOnClickListener {
                viewModel?.selectCity(item)
            }
        }
    }

    fun filterList(auxList: ArrayList<CityObj>) {
        citiesList = auxList
        notifyDataSetChanged()
    }

}