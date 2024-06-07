package com.buzuriu.dogapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.buzuriu.dogapp.databinding.RatingWithTextBinding
import com.buzuriu.dogapp.models.ReviewObj
import com.buzuriu.dogapp.models.UserWithReviewObj
import com.buzuriu.dogapp.viewModels.UserReviewsViewModel
import com.google.firebase.firestore.auth.User

class RatingWithTextAdapter(
    private var reviewUserList: ArrayList<UserWithReviewObj>,
    var viewModel: UserReviewsViewModel
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val applicationBinding = RatingWithTextBinding.inflate(
            layoutInflater, parent, false
        )
        return RatingWithTextViewHolder(applicationBinding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val userWithReviewObj = reviewUserList[position]
        if (holder is RatingWithTextAdapter.RatingWithTextViewHolder) {
            (holder).bind(userWithReviewObj)
        }
    }

    override fun getItemCount(): Int {
        return reviewUserList.size
    }

    inner class RatingWithTextViewHolder(private var applicationBinding: RatingWithTextBinding)
        : RecyclerView.ViewHolder(applicationBinding.root) {
        fun bind(userWithReviewObj: UserWithReviewObj) {
            applicationBinding.userWithReviewObj = userWithReviewObj
        }
    }
}
