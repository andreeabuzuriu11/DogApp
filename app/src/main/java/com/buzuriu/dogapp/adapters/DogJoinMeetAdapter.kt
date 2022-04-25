package com.buzuriu.dogapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.buzuriu.dogapp.databinding.DogCellJoinMeetingBinding
import com.buzuriu.dogapp.models.DogObj
import com.buzuriu.dogapp.viewModels.SelectDogForJoinMeetViewModel

class DogJoinMeetAdapter(
    var dogsList: ArrayList<DogObj>,
    private var viewModel: SelectDogForJoinMeetViewModel? = null
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val applicationBinding =
            DogCellJoinMeetingBinding.inflate(layoutInflater, parent, false)
        return DogJoinMeetCellViewHolder(applicationBinding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val dogObj = dogsList[position]
        (holder as DogJoinMeetAdapter.DogJoinMeetCellViewHolder).bind(dogObj)
    }

    inner class DogJoinMeetCellViewHolder(private var applicationBinding: DogCellJoinMeetingBinding) :
        RecyclerView.ViewHolder(applicationBinding.root) {
        fun bind(item: DogObj) {
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