package com.buzuriu.dogapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.buzuriu.dogapp.databinding.DogCellBinding
import com.buzuriu.dogapp.models.DogObj
import kotlin.reflect.KFunction1

class DogAdapter (var dogList: ArrayList<DogObj>, var selectedDog: KFunction1<DogObj, Unit>) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val applicationBinding = DogCellBinding.inflate(layoutInflater, parent, false)
        return DogViewHolder(applicationBinding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val dog = dogList[position]
        if (holder is DogViewHolder) {
            (holder).bind(dog)
        }
    }

    override fun getItemCount(): Int {
        return dogList.size
    }

    inner class DogViewHolder(var applicationBinding: DogCellBinding) :
        RecyclerView.ViewHolder(applicationBinding.root) {
        fun bind(dog : DogObj)
        {
            applicationBinding.dog = dog
            applicationBinding.dogCell.setOnClickListener {
                selectedDog(dog)
            }
        }
    }

}
