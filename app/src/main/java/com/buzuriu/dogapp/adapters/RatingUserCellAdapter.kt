package com.buzuriu.dogapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.recyclerview.widget.RecyclerView
import com.buzuriu.dogapp.databinding.RatingUserCellBinding
import com.buzuriu.dogapp.models.ReviewObj
import com.buzuriu.dogapp.models.UserWithReview
import com.buzuriu.dogapp.viewModels.ReviewParticipantsViewModel
import com.buzuriu.dogapp.views.ReviewParticipantsFragment
import kotlin.reflect.KFunction1

class RatingUserCellAdapter(
    private var reviewUserList: ArrayList<UserWithReview>,
    var selectedCell: KFunction1<ReviewObj, Unit>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val applicationBinding = RatingUserCellBinding.inflate(
            layoutInflater, parent, false
        )
        return RatingUserCellViewHolder(applicationBinding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val userWithReview = reviewUserList[position]
        if (holder is RatingUserCellAdapter.RatingUserCellViewHolder) {
            (holder).bind(userWithReview)
        }
    }

    override fun getItemCount(): Int {
        return reviewUserList.size
    }

    inner class RatingUserCellViewHolder(private var applicationBinding: RatingUserCellBinding)
        : RecyclerView.ViewHolder(applicationBinding.root) {
        fun bind(userWithReview: UserWithReview) {
            applicationBinding.userWithReview = userWithReview
        }
    }

}
