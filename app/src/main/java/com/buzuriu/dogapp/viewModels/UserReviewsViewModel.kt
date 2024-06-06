package com.buzuriu.dogapp.viewModels

import androidx.lifecycle.viewModelScope
import com.buzuriu.dogapp.adapters.RatingWithTextAdapter
import com.buzuriu.dogapp.models.ReviewObj
import com.buzuriu.dogapp.models.UserWithReviewObj
import com.buzuriu.dogapp.utils.FieldsItems
import com.buzuriu.dogapp.utils.LocalDBItems
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UserReviewsViewModel : BaseViewModel() {

    var ratingWithTextAdapter: RatingWithTextAdapter? = null
    private var userWithReviewList = ArrayList<UserWithReviewObj>()
    private var reviewList = ArrayList<ReviewObj>()

    init {
        ratingWithTextAdapter = RatingWithTextAdapter(userWithReviewList, this)


        viewModelScope.launch {
            fetchAllReviewsUserLeft()
        }
    }

    // todo replace with reviews user got
    private suspend fun fetchAllReviewsUserLeft() {
        viewModelScope.launch(Dispatchers.IO) {
            val list = getAllReviewsThatUserLeft()
            viewModelScope.launch(Dispatchers.Main) {
                userWithReviewList.clear()

                reviewList.addAll(list)
            }
        }
    }

    private suspend fun getAllReviewsThatUserLeft(): ArrayList<ReviewObj> {
        val allReviewsUserHasLeft = ArrayList<ReviewObj>()

        val list: ArrayList<ReviewObj>? =
            databaseService.fetchReviewsFor(FieldsItems.userIdThatLeftReview, currentUser!!.uid)
        if (list != null) {
            for (review in list) {
                val reviewObj = ReviewObj(
                    review.uid!!,
                    review.userIdThatLeftReview!!,
                    review.userThatReviewIsFor!!,
                    review.numberOfStars!!,
                    review.reviewText
                )
                allReviewsUserHasLeft.add(reviewObj)
            }
        }
        return allReviewsUserHasLeft
    }

    private suspend fun fetchUserReviews(userUid: String) {
        databaseService.fetchReviewsFor(FieldsItems.userIdThatLeftReview, userUid)
    }

    fun close() {
        navigationService.closeCurrentActivity()
    }
}