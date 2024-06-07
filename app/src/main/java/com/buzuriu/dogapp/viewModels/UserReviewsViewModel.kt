package com.buzuriu.dogapp.viewModels

import androidx.lifecycle.viewModelScope
import com.buzuriu.dogapp.adapters.RatingWithTextAdapter
import com.buzuriu.dogapp.listeners.IOnCompleteListener
import com.buzuriu.dogapp.models.ReviewObj
import com.buzuriu.dogapp.models.UserWithReviewObj
import com.buzuriu.dogapp.utils.FieldsItems
import com.google.firebase.firestore.auth.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception

class UserReviewsViewModel : BaseViewModel() {

    var ratingWithTextAdapter: RatingWithTextAdapter? = null
    private var userWithReviewList = ArrayList<UserWithReviewObj>()
    private var reviewList = ArrayList<UserWithReviewObj>()
    private var currentUid : String? = null

    init {
        currentUid = exchangeInfoService.get<String>(this::class.java.name)

        ratingWithTextAdapter = RatingWithTextAdapter(reviewList, this)

        viewModelScope.launch {
            fetchAllReviewsUserLeft()
        }
    }

    // todo replace with reviews user got
    private suspend fun fetchAllReviewsUserLeft() {
        viewModelScope.launch(Dispatchers.IO) {
            val list = getAllReviewsThatUserGot()
            viewModelScope.launch(Dispatchers.Main) {
                reviewList.clear()
                reviewList.addAll(list)
                ratingWithTextAdapter!!.notifyDataSetChanged()
            }
        }
    }

    private suspend fun getAllReviewsThatUserGot(): ArrayList<UserWithReviewObj> {
        val allReviewsUserHasGot = ArrayList<UserWithReviewObj>()

        val list: ArrayList<ReviewObj>? =
            databaseService.fetchReviewsFor(FieldsItems.userThatReviewIsFor, currentUid!!)
        if (list != null) {
            for (review in list) {
                val reviewObj = ReviewObj(
                    review.uid!!,
                    review.userIdThatLeftReview!!,
                    review.userThatReviewIsFor!!,
                    review.numberOfStars!!,
                    review.reviewText
                )
                var userObj = databaseService.fetchUserByUid(review.userIdThatLeftReview!!, object : IOnCompleteListener{
                    override fun onComplete(successful: Boolean, exception: Exception?) {
                        TODO("Not yet implemented")



                    }

                })

                var userWithReviewObj = UserWithReviewObj(review.userIdThatLeftReview!!, userObj!!, reviewObj)
                allReviewsUserHasGot.add(userWithReviewObj)
            }
        }
        return allReviewsUserHasGot
    }

    private suspend fun fetchUserReviews(userUid: String) {
        databaseService.fetchReviewsFor(FieldsItems.userIdThatLeftReview, userUid)
    }

    fun close() {
        navigationService.closeCurrentActivity()
    }
}