package com.buzuriu.dogapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.buzuriu.dogapp.databinding.BreedCellBinding
import com.buzuriu.dogapp.databinding.DogCellBinding
import com.buzuriu.dogapp.databinding.DogNameCellBinding
import com.buzuriu.dogapp.models.BreedObj
import com.buzuriu.dogapp.models.DogObj
import com.buzuriu.dogapp.viewModels.SelectBreedViewModel
import com.buzuriu.dogapp.viewModels.SelectDogViewModel

class DogNameAdapter(var dogsList : ArrayList<DogObj>,
                     private var viewModel: SelectDogViewModel? = null
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val applicationBinding =
            DogNameCellBinding.inflate(layoutInflater, parent, false)
        return DogNameCellViewHolder(applicationBinding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val dogObj = dogsList[position];
        (holder as DogNameAdapter.DogNameCellViewHolder).bind(dogObj)
    }

    inner class DogNameCellViewHolder(var applicationBinding: DogNameCellBinding) :
        RecyclerView.ViewHolder(applicationBinding.root) {
        fun bind(item: DogObj)
        {
            applicationBinding.dogItem = item
            if (viewModel != null) {
                applicationBinding.dogCellLayout.setOnClickListener {
                    viewModel?.selectDog(item)

                }
            }
        }
    }

    override fun getItemCount(): Int {
        return dogsList.size
    }
}