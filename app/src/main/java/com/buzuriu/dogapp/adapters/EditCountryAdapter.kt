package com.buzuriu.dogapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.buzuriu.dogapp.databinding.CountryCellBinding
import com.buzuriu.dogapp.models.CountryObj
import com.buzuriu.dogapp.viewModels.EditCountryViewModel

class EditCountryAdapter(
    var countriesList: ArrayList<CountryObj>,
    private var viewModel: EditCountryViewModel? = null
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun getItemCount(): Int {
        return countriesList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val applicationBinding =
            CountryCellBinding.inflate(layoutInflater, parent, false)
        return CityCellViewHolder(applicationBinding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val countryObj = countriesList[position];
        (holder as CityCellViewHolder).bind(countryObj)
    }

    inner class CityCellViewHolder(var applicationBinding: CountryCellBinding) :
        RecyclerView.ViewHolder(applicationBinding.root) {
        fun bind(item: CountryObj) {
            applicationBinding.countryObj = item
            applicationBinding.countryCellLayout.setOnClickListener {
                viewModel?.selectCountry(item)
            }
        }
    }

    fun filterList(auxList: ArrayList<CountryObj>) {
        countriesList = auxList
        notifyDataSetChanged()
    }

}