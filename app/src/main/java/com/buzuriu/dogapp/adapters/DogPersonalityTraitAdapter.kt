package com.buzuriu.dogapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.buzuriu.dogapp.databinding.DogPersonalityTraitCellBinding
import com.buzuriu.dogapp.models.DogPersonalityTraitObj

class DogPersonalityTraitAdapter
    (var dogPersonalityTraitList: ArrayList<DogPersonalityTraitObj>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val applicationBinding =
            DogPersonalityTraitCellBinding.inflate(layoutInflater, parent, false)
        return DogPersonalityTraitViewHolder(applicationBinding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val dog = dogPersonalityTraitList[position]
        if (holder is DogPersonalityTraitViewHolder) {
            (holder).bind(dog)
        }
    }

    override fun getItemCount(): Int {
        return dogPersonalityTraitList.size
    }

    inner class DogPersonalityTraitViewHolder(var applicationBinding: DogPersonalityTraitCellBinding) :
        RecyclerView.ViewHolder(applicationBinding.root) {
        fun bind(dogPersonalityTraitObj: DogPersonalityTraitObj) {
            applicationBinding.dogPersonalityTrait = dogPersonalityTraitObj
        }
    }
}
