package com.buzuriu.dogapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.buzuriu.dogapp.databinding.RatingUserCellBinding
import com.buzuriu.dogapp.models.UserWithReviewObj
import com.buzuriu.dogapp.viewModels.ReviewParticipantsViewModel


class RatingUserCellAdapter(
    private var reviewUserList: ArrayList<UserWithReviewObj>,
    var viewModel: ReviewParticipantsViewModel
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val applicationBinding = RatingUserCellBinding.inflate(
            layoutInflater, parent, false
        )
        return RatingUserCellViewHolder(applicationBinding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val userWithReviewObj = reviewUserList[position]
        if (holder is RatingUserCellAdapter.RatingUserCellViewHolder) {
            (holder).bind(userWithReviewObj)
        }
    }

    override fun getItemCount(): Int {
        return reviewUserList.size
    }

    inner class RatingUserCellViewHolder(private var applicationBinding: RatingUserCellBinding)
        : RecyclerView.ViewHolder(applicationBinding.root) {
        fun bind(userWithReviewObj: UserWithReviewObj) {
            applicationBinding.userWithReviewObj = userWithReviewObj
            applicationBinding.rateIt.setOnClickListener {
                viewModel.saveReviewInDatabase(userWithReviewObj)
            }
        }
    }
}
