package com.buzuriu.dogapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.buzuriu.dogapp.databinding.BreedCellBinding
import com.buzuriu.dogapp.models.BreedObj
import com.buzuriu.dogapp.viewModels.SelectBreedViewModel

class BreedAdapter(
    var breedsList: ArrayList<BreedObj>,
    private var viewModel: SelectBreedViewModel? = null
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun getItemCount(): Int {
        return breedsList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val applicationBinding =
            BreedCellBinding.inflate(layoutInflater, parent, false)
        return BreedCellViewHolder(applicationBinding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val breedObj = breedsList[position];
        (holder as BreedCellViewHolder).bind(breedObj)
    }

    inner class BreedCellViewHolder(var applicationBinding: BreedCellBinding) :
        RecyclerView.ViewHolder(applicationBinding.root) {
        fun bind(item: BreedObj) {
            applicationBinding.breedItem = item
            if (viewModel != null) {
                applicationBinding.breedCellLayout.setOnClickListener {
                    viewModel?.selectBreed(item)
                }
            }
        }
    }

    fun filterList(auxList: ArrayList<BreedObj>) {
        breedsList = auxList
        notifyDataSetChanged()
    }

}