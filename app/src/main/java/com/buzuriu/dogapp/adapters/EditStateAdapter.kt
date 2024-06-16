package com.buzuriu.dogapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.buzuriu.dogapp.databinding.StateCellBinding
import com.buzuriu.dogapp.models.StateObj
import com.buzuriu.dogapp.viewModels.EditStateViewModel

class EditStateAdapter(
    var statesList: ArrayList<StateObj>,
    private var viewModel: EditStateViewModel? = null
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun getItemCount(): Int {
        return statesList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val applicationBinding =
            StateCellBinding.inflate(layoutInflater, parent, false)
        return StateCellViewHolder(applicationBinding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val stateObj = statesList[position];
        (holder as StateCellViewHolder).bind(stateObj)
    }

    inner class StateCellViewHolder(var applicationBinding: StateCellBinding) :
        RecyclerView.ViewHolder(applicationBinding.root) {
        fun bind(item: StateObj) {
            applicationBinding.stateObj = item
            applicationBinding.stateCellLayout.setOnClickListener {
                viewModel?.selectState(item)
            }
        }
    }

    fun filterList(auxList: ArrayList<StateObj>) {
        statesList = auxList
        notifyDataSetChanged()
    }

}