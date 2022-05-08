package com.buzuriu.dogapp.services

import android.util.Log
import com.buzuriu.dogapp.listeners.IOnCompleteListener
import com.buzuriu.dogapp.models.*
import com.buzuriu.dogapp.utils.MapUtils
import com.buzuriu.dogapp.utils.MeetingUtils
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.tasks.await
import java.util.*
import kotlin.collections.ArrayList


interface IDatabaseService {
    val fireAuth: FirebaseAuth

    suspend fun storeUserInfo(
        userUid: String,
        userInfo: UserInfo,
        onCompleteListener: IOnCompleteListener
    )

    suspend fun storeDogInfo(userUid: String, dog: DogObj, onCompleteListener: IOnCompleteListener)
    suspend fun storeDogUidToUser(
        userUid: String,
        dogUid: String,
        onCompleteListener: IOnCompleteListener
    )

    suspend fun storeReview(
        reviewUid: String,
        reviewObj: ReviewObj,
        onCompleteListener: IOnCompleteListener
    )

    suspend fun storeMeetingInfo(
        meetingUid: String,
        meetingObj: MeetingObj,
        onCompleteListener: IOnCompleteListener
    )

    suspend fun joinMeeting(
        meetingUid: String,
        participantUid: String,
        participantObj: ParticipantObj,
        onCompleteListener: IOnCompleteListener
    )

    suspend fun updateParticipantDog(
        meetingUid: String,
        participantUid: String,
        newDogUid: String,
        onCompleteListener: IOnCompleteListener
    )

    suspend fun updateReview(
        reviewUid: String,
        newNumberOfStars: Float,
        onCompleteListener: IOnCompleteListener
    )

    suspend fun leaveMeeting(
        meetingUid: String,
        participantUid: String,
        onCompleteListener: IOnCompleteListener
    )

    suspend fun fetchMeetingByUid(meetingUid: String): MeetingObj?
    suspend fun fetchDogByUid(dogUid: String): DogObj?
    suspend fun fetchUserByUid(userUid: String): UserInfo?
    suspend fun fetchUserDogs(userUid: String): ArrayList<DogObj>?
    suspend fun fetchUserReviews(userUid: String) : ArrayList<ReviewObj>?
    suspend fun fetchReviewsThatUserLeft(userUid: String) : ArrayList<ReviewObj>?
    suspend fun fetchMeetings(
        filtersList: ArrayList<IFilterObj>,
        userUid: String
    ): ArrayList<MeetingObj>

    suspend fun fetchAllMeetingParticipants(meetingUid: String): ArrayList<ParticipantObj>?
    suspend fun fetchUserParticipantUidForMeeting(meetingUid: String, userUid: String): String?
    suspend fun fetchReviewUidForUser(userThatLeftReview: String, userThatReviewIsFor: String) : String?
    suspend fun fetchAllOtherMeetings(userUid: String): ArrayList<MeetingObj>?
    suspend fun fetchAllOtherPastMeetings(userUid: String) : ArrayList<MeetingObj>?
    suspend fun fetchUserMeetings(userUid: String): ArrayList<MeetingObj>?
    suspend fun fetchDogMeetings(dogUid: String): ArrayList<MeetingObj>?
    suspend fun fetchMeetingsByFilters(
        filters: ArrayList<IFilterObj>,
        userUid: String
    ): ArrayList<MeetingObj>?


    suspend fun deleteDog(
        dogUid: String,
        onCompleteListener: IOnCompleteListener
    )

    suspend fun deleteDogRelatedToUser(
        userUid: String,
        dogUid: String,
        onCompleteListener: IOnCompleteListener
    )

    suspend fun deleteMeeting(
        meetingUid: String,
        onCompleteListener: IOnCompleteListener
    )

    suspend fun deleteParticipant(
        meetingUid: String,
        participantUid: String,
        onCompleteListener: IOnCompleteListener
    )
}

class DatabaseService(
    private val sharedPreferencesService: ISharedPreferencesService
) : IDatabaseService {
    override val fireAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val userInfoCollection = "UserInfo"
    private val dogInfoCollection = "Dog"
    private val meetingsCollection = "Meeting"
    private val reviewCollection = "Review"
    private val meetingParticipants = "MeetingParticipants"
    private var meetingsQuery: Query? = null
    private var tasksList = ArrayList<Task<QuerySnapshot>>()
    private var firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    override suspend fun storeUserInfo(
        userUid: String,
        userInfo: UserInfo,
        onCompleteListener: IOnCompleteListener
    ) {
        firestore.collection(userInfoCollection)
            .document(userUid)
            .set(userInfo)
            .addOnCompleteListener { onCompleteListener.onComplete(it.isSuccessful, it.exception) }
            .await()
    }

    override suspend fun storeDogUidToUser(
        userUid: String,
        dogUid: String,
        onCompleteListener: IOnCompleteListener
    ) {
        firestore.collection(userInfoCollection)
            .document(userUid)
            .collection(dogInfoCollection)
            .document(dogUid)
            .set({
                userUid
            })
            .addOnCompleteListener { onCompleteListener.onComplete(it.isSuccessful, it.exception) }
            .await()
    }

    override suspend fun storeReview(
        reviewUid: String,
        reviewObj: ReviewObj,
        onCompleteListener: IOnCompleteListener
    ) {
        firestore.collection(reviewCollection)
            .document(reviewUid)
            .set(reviewObj)
            .addOnCompleteListener { onCompleteListener.onComplete(it.isSuccessful, it.exception) }
            .await()
    }



    override suspend fun storeDogInfo(
        userUid: String,
        dog: DogObj,
        onCompleteListener: IOnCompleteListener
    ) {
        firestore.collection(dogInfoCollection)
            .document(dog.uid).set(dog)
            .addOnCompleteListener { onCompleteListener.onComplete(it.isSuccessful, it.exception) }
            .await()
    }

    override suspend fun storeMeetingInfo(
        meetingUid: String,
        meetingObj: MeetingObj,
        onCompleteListener: IOnCompleteListener
    ) {
        firestore.collection(meetingsCollection)
            .document(meetingUid)
            .set(meetingObj)
            .addOnCompleteListener { onCompleteListener.onComplete(it.isSuccessful, it.exception) }
            .await()
    }

    override suspend fun joinMeeting(
        meetingUid: String,
        participantUid: String,
        participantObj: ParticipantObj,
        onCompleteListener: IOnCompleteListener
    ) {
        firestore.collection(meetingsCollection)
            .document(meetingUid)
            .collection(meetingParticipants)
            .document(participantUid)
            .set(participantObj)
            .addOnCompleteListener { onCompleteListener.onComplete(it.isSuccessful, it.exception) }
            .await()
    }

    override suspend fun updateParticipantDog(
        meetingUid: String,
        participantUid: String,
        newDogUid: String,
        onCompleteListener: IOnCompleteListener
    ) {
        firestore.collection(meetingsCollection)
            .document(meetingUid)
            .collection(meetingParticipants)
            .document(participantUid)
            .update("dogUid", newDogUid)
            .addOnCompleteListener { onCompleteListener.onComplete(it.isSuccessful, it.exception) }
            .await()
    }

    override suspend fun updateReview(
        reviewUid: String,
        newNumberOfStars: Float,
        onCompleteListener: IOnCompleteListener
    ) {
        firestore.collection(reviewCollection)
            .document(reviewUid)
            .update("numberOfStars", newNumberOfStars)
            .addOnCompleteListener { onCompleteListener.onComplete(it.isSuccessful, it.exception) }
            .await()
    }

    override suspend fun leaveMeeting(
        meetingUid: String,
        participantUid: String,
        onCompleteListener: IOnCompleteListener
    ) {
        firestore.collection(meetingsCollection)
            .document(meetingUid)
            .collection(meetingParticipants)
            .document(participantUid)
            .delete()
            .addOnCompleteListener { onCompleteListener.onComplete(it.isSuccessful, it.exception) }
            .await()
    }

    override suspend fun fetchDogByUid(dogUid: String): DogObj? {
        var dogObj: DogObj? = null
        val documentSnapshot =
            firestore.collection(dogInfoCollection)
                .document(dogUid)
                .get()
                .await()

        if (documentSnapshot != null) {
            try {
                dogObj = documentSnapshot.toObject(DogObj::class.java)
            } catch (e: Exception) {
                Log.d("Error", e.message.toString())
            }
        }

        return dogObj
    }

    override suspend fun fetchMeetingByUid(meetingUid: String): MeetingObj? {
        var meetingObj: MeetingObj? = null
        val documentSnapshot =
            firestore.collection(meetingsCollection)
                .document(meetingUid)
                .get()
                .await()

        if (documentSnapshot != null) {
            try {
                meetingObj = documentSnapshot.toObject(MeetingObj::class.java)
            } catch (e: Exception) {
                Log.d("Error", e.message.toString())
            }
        }

        return meetingObj
    }

    override suspend fun fetchUserByUid(userUid: String): UserInfo? {
        var userInfo: UserInfo? = null
        val documentSnapshot =
            firestore.collection(userInfoCollection)
                .document(userUid)
                .get()
                .await()

        if (documentSnapshot != null) {
            try {
                userInfo = documentSnapshot.toObject(UserInfo::class.java)
            } catch (e: Exception) {
                Log.d("Error", e.message.toString())
            }
        }

        return userInfo
    }

    override suspend fun fetchUserDogs(userUid: String): ArrayList<DogObj> {
        val dogList = ArrayList<DogObj>()
        val queryList = ArrayList<Task<QuerySnapshot>>()
        val query = firestore.collection(dogInfoCollection)
            .whereEqualTo("owner", userUid)
            .get()

        queryList.add(query)

        val allTasks =
            Tasks.whenAllSuccess<QuerySnapshot>(queryList)

        allTasks.addOnSuccessListener {

            for (dogDocSnapshot in it) {
                for (querySnapshot in dogDocSnapshot) {
                    val dog = querySnapshot.toObject(DogObj::class.java)
                    dogList.add(dog)
                }
            }
        }
            .addOnFailureListener { throw it }

        allTasks.await()

        return dogList
    }

    //TODO make a function out of these 2
    override suspend fun fetchUserReviews(
        userUid: String
    ): ArrayList<ReviewObj> {
        val reviewList = ArrayList<ReviewObj>()
        val queryList = ArrayList<Task<QuerySnapshot>>()
        val query = firestore.collection(reviewCollection)
            .whereEqualTo("userThatReviewIsFor", userUid)
            .get()

        queryList.add(query)

        val allTasks =
            Tasks.whenAllSuccess<QuerySnapshot>(queryList)

        allTasks.addOnSuccessListener {
            for (dogDocSnapshot in it) {
                for (querySnapshot in dogDocSnapshot) {
                    val review = querySnapshot.toObject(ReviewObj::class.java)
                    reviewList.add(review)
                }
            }
        }
            .addOnFailureListener { throw it }

        allTasks.await()


        return reviewList
    }

    override suspend fun fetchReviewsThatUserLeft(userUid: String): ArrayList<ReviewObj> {
        var reviewList = ArrayList<ReviewObj>()
        val queryList = ArrayList<Task<QuerySnapshot>>()
        val query = firestore
            .collection(reviewCollection)
            .whereEqualTo("userIdThatLeftReview", userUid)
            .get()

        queryList.add(query)

        val allTasks =
            Tasks.whenAllSuccess<QuerySnapshot>(queryList)

        allTasks.addOnSuccessListener {
            for (dogDocSnapshot in it) {
                for (querySnapshot in dogDocSnapshot) {
                    val review = querySnapshot.toObject(ReviewObj::class.java)

                    reviewList.add(review)
                }
            }
        }
            .addOnFailureListener { throw it }

        allTasks.await()
        return reviewList
    }

    override suspend fun fetchAllOtherMeetings(userUid: String): ArrayList<MeetingObj> {
        val meetingsList = ArrayList<MeetingObj>()
        val queryList = ArrayList<Task<QuerySnapshot>>()
        val query = firestore.collection(meetingsCollection)
            .get()

        queryList.add(query)

        val allTasks =
            Tasks.whenAllSuccess<QuerySnapshot>(queryList)

        allTasks.addOnSuccessListener {

            for (meetingDocSnapshot in it) {
                for (querySnapshot in meetingDocSnapshot) {
                    val meeting = querySnapshot.toObject(MeetingObj::class.java)
                    if (meeting.userUid != userUid && !MeetingUtils.isMeetingInThePast(meeting))
                        meetingsList.add(meeting)
                }
            }
        }
            .addOnFailureListener { throw it }

        allTasks.await()

        return meetingsList
    }

    override suspend fun fetchAllOtherPastMeetings(userUid: String): ArrayList<MeetingObj> {
        val meetingsList = ArrayList<MeetingObj>()
        val queryList = ArrayList<Task<QuerySnapshot>>()
        val query = firestore.collection(meetingsCollection)
            .get()

        queryList.add(query)

        val allTasks =
            Tasks.whenAllSuccess<QuerySnapshot>(queryList)

        allTasks.addOnSuccessListener {

            for (meetingDocSnapshot in it) {
                for (querySnapshot in meetingDocSnapshot) {
                    val meeting = querySnapshot.toObject(MeetingObj::class.java)
                    if (meeting.userUid != userUid && MeetingUtils.isMeetingInThePast(meeting))
                        meetingsList.add(meeting)
                }
            }
        }
            .addOnFailureListener { throw it }

        allTasks.await()

        return meetingsList
    }

    override suspend fun fetchUserMeetings(userUid: String): ArrayList<MeetingObj> {
        val meetingsList = ArrayList<MeetingObj>()
        val queryList = ArrayList<Task<QuerySnapshot>>()
        val query = firestore.collection(meetingsCollection)
            .whereEqualTo("userUid", userUid)
            .get()

        queryList.add(query)

        val allTasks =
            Tasks.whenAllSuccess<QuerySnapshot>(queryList)

        allTasks.addOnSuccessListener {

            for (meetingDocSnapshot in it) {
                for (querySnapshot in meetingDocSnapshot) {
                    val meeting = querySnapshot.toObject(MeetingObj::class.java)
                    meetingsList.add(meeting)
                }
            }
        }
            .addOnFailureListener { throw it }

        allTasks.await()

        return meetingsList
    }

    override suspend fun fetchDogMeetings(dogUid: String): ArrayList<MeetingObj> {
        val meetingsList = ArrayList<MeetingObj>()
        val queryList = ArrayList<Task<QuerySnapshot>>()
        val query = firestore.collection(meetingsCollection)
            .whereEqualTo("dogUid", dogUid)
            .get()

        queryList.add(query)

        val allTasks =
            Tasks.whenAllSuccess<QuerySnapshot>(queryList)

        allTasks.addOnSuccessListener {

            for (meetingDocSnapshot in it) {
                for (querySnapshot in meetingDocSnapshot) {
                    val meeting = querySnapshot.toObject(MeetingObj::class.java)
                    meetingsList.add(meeting)
                }
            }
        }
            .addOnFailureListener { throw it }

        allTasks.await()

        return meetingsList
    }

    private fun setMeetingsTimeQuery(filterType: IFilterObj) {
        val start = Calendar.getInstance()
        val end = Calendar.getInstance()

        val query: Task<QuerySnapshot>?

        start.timeInMillis = System.currentTimeMillis()
        end.time = Calendar.getInstance().time
        end[Calendar.HOUR_OF_DAY] = 23
        end[Calendar.MINUTE] = 59
        end[Calendar.SECOND] = 59
        end[Calendar.MILLISECOND] = 999

        when (filterType.name) {
            "Today" -> {
                // all options for today are already selected above
            }
            "Tomorrow" -> {
                start.time = Calendar.getInstance().time
                start.add(Calendar.DATE, 1)
                start[Calendar.HOUR_OF_DAY] = 0
                start[Calendar.MINUTE] = 0
                start[Calendar.SECOND] = 0
                start[Calendar.MILLISECOND] = 0

                end.add(Calendar.DATE, 1)
            }
            "This week" -> {
                start.timeInMillis = System.currentTimeMillis()
                end.timeInMillis = System.currentTimeMillis()

                end[Calendar.HOUR_OF_DAY] = 23
                end[Calendar.MINUTE] = 59
                end[Calendar.SECOND] = 59
                end[Calendar.MILLISECOND] = 999

                when {
                    Calendar.DAY_OF_MONTH > 24 -> {
                        when (Calendar.MONTH) {
                            Calendar.JANUARY, Calendar.MARCH, Calendar.MAY, Calendar.JULY, Calendar.AUGUST, Calendar.OCTOBER, Calendar.DECEMBER -> {
                                end.timeInMillis = System.currentTimeMillis()
                                end.add(Calendar.MONTH, 1)
                                end.add(Calendar.DAY_OF_MONTH, 7 - (31 - Calendar.DAY_OF_MONTH))
                            }
                            Calendar.APRIL, Calendar.JUNE, Calendar.SEPTEMBER, Calendar.NOVEMBER -> {
                                end.timeInMillis = System.currentTimeMillis()
                                end.add(Calendar.MONTH, 1)
                                end.add(Calendar.DAY_OF_MONTH, 7 - (30 - Calendar.DAY_OF_MONTH))

                            }
                            Calendar.FEBRUARY -> {
                                end.timeInMillis = System.currentTimeMillis()
                                end.add(Calendar.MONTH, 1)
                                end.add(Calendar.DAY_OF_MONTH, 7 - (28 - Calendar.DAY_OF_MONTH))

                            }
                        }
                    }
                    Calendar.DAY_OF_MONTH == 24 -> {
                        when (Calendar.MONTH) {
                            Calendar.JANUARY, Calendar.MARCH, Calendar.MAY, Calendar.JULY, Calendar.AUGUST, Calendar.OCTOBER, Calendar.DECEMBER -> {
                                end.timeInMillis = System.currentTimeMillis()
                                end[Calendar.DAY_OF_MONTH] = 31

                            }
                            Calendar.APRIL, Calendar.JUNE, Calendar.SEPTEMBER, Calendar.NOVEMBER -> {
                                end.timeInMillis = System.currentTimeMillis()
                                end.add(Calendar.MONTH, 1)
                                end[Calendar.DAY_OF_MONTH] = 1

                            }
                            Calendar.FEBRUARY -> {
                                end.timeInMillis = System.currentTimeMillis()
                                end.add(Calendar.MONTH, 1)
                                end.add(Calendar.DAY_OF_MONTH, 7 - (28 - Calendar.DAY_OF_MONTH))
                            }
                        }
                    }
                    Calendar.DAY_OF_MONTH in 22..23 -> {
                        when (Calendar.MONTH) {
                            Calendar.FEBRUARY -> {
                                end.timeInMillis = System.currentTimeMillis()
                                end.add(Calendar.MONTH, 1)
                                end.add(Calendar.DAY_OF_MONTH, 7 - (28 - Calendar.DAY_OF_MONTH))
                            }
                        }
                    }
                    Calendar.DAY_OF_MONTH <= 21 -> {
                        end.timeInMillis = System.currentTimeMillis()
                        end.add(Calendar.DAY_OF_MONTH, 7)
                    }
                }
            }
            "This month" -> {
                end.add(Calendar.MONTH, 1)
                end.set(Calendar.DAY_OF_MONTH, 1)
                end.add(Calendar.DATE, -1)
            }
            "Next week" -> {
                start.time = Calendar.getInstance().time
                start.add(Calendar.WEEK_OF_MONTH, 1)
                start[Calendar.DAY_OF_WEEK] = 1
                start[Calendar.HOUR_OF_DAY] = 0
                start[Calendar.MINUTE] = 0
                start[Calendar.SECOND] = 0
                start[Calendar.MILLISECOND] = 0

                end.timeInMillis = System.currentTimeMillis()
                end.add(Calendar.WEEK_OF_MONTH, 1)
                end[Calendar.DAY_OF_WEEK] = 7
            }
            "Next month" -> {
                start.time = Calendar.getInstance().time
                start.add(Calendar.MONTH, 1)
                start[Calendar.DAY_OF_MONTH] = 1
                start[Calendar.HOUR_OF_DAY] = 0
                start[Calendar.MINUTE] = 0
                start[Calendar.SECOND] = 0
                start[Calendar.MILLISECOND] = 0

                end.timeInMillis = System.currentTimeMillis()
                end.add(Calendar.MONTH, 2)
                end.set(Calendar.DAY_OF_MONTH, 1)
                end.add(Calendar.DATE, -1)
                end[Calendar.HOUR_OF_DAY] = 23
                end[Calendar.MINUTE] = 59
                end[Calendar.SECOND] = 59
                end[Calendar.MILLISECOND] = 999
            }
        }

        query = meetingsQuery!!
            .whereGreaterThan("date", start.timeInMillis)
            .whereLessThan("date", end.timeInMillis)
            .get()

        tasksList.add(query)
    }


    private fun setDogGenderTypeQuery(filterType: IFilterObj) {
        val query = meetingsQuery!!
            .whereEqualTo("dogGender", filterType.name)
            .get()
        tasksList.add(query)
    }

    private fun setUserGenderTypeQuery(filterType: IFilterObj) {
        val query = meetingsQuery!!
            .whereEqualTo("userGender", filterType.name)
            .get()
        tasksList.add(query)
    }

    private fun setDogBreedTypeQuery(filterType: IFilterObj) {
        val query = meetingsQuery!!
            .whereEqualTo("dogBreed", filterType.name)
            .get()
        tasksList.add(query)
    }

    private fun setMeetingsDistanceQuery(radiusInKM: Int) {
        val myCurrentUserLocation =
            sharedPreferencesService
                .readFromSharedPref<LatLng>(
                    SharedPreferences.userLocationKey,
                    LatLng::class.java
                )
        if (myCurrentUserLocation == null) {
            Log.d("Error", "Current User Location is null in SharedPref")
        }

        val center = LatLng(
            myCurrentUserLocation!!.latitude,
            myCurrentUserLocation.longitude
        )
        val greaterPoint = MapUtils.getGreaterPoint(radiusInKM, center)
        val lesserPoint = MapUtils.getLesserPoint(radiusInKM, center)

        val distanceQuery =
            meetingsQuery?.whereGreaterThan(
                "location",
                GeoPoint(lesserPoint.latitude, lesserPoint.longitude)
            )!!
                .whereLessThan(
                    "location",
                    GeoPoint(
                        greaterPoint.latitude,
                        greaterPoint.longitude
                    )
                ).get()

        tasksList.add(distanceQuery)
    }

    private fun createFilterQuery(filtersList: ArrayList<IFilterObj>) {
        filtersList.forEach {
            when (it) {
                is FilterByTimeObj -> {
                    setMeetingsTimeQuery(it)
                }
                is FilterByDogGenderObj -> {
                    setDogGenderTypeQuery(it)
                }
                is FilterByUserGenderObj -> {
                    setUserGenderTypeQuery(it)
                }
                is FilterByDogBreedObj -> {
                    setDogBreedTypeQuery(it)
                }
                is FilterByLocationObj -> {
                    setMeetingsDistanceQuery(it.distance!!)
                }
            }
        }
    }

    override suspend fun fetchMeetingsByFilters(
        filters: ArrayList<IFilterObj>,
        userUid: String
    ): ArrayList<MeetingObj> {
        val meetingsList = ArrayList<MeetingObj>()

        meetingsQuery =
            firestore.collection(meetingsCollection)

        createFilterQuery(filters)

        val allTasks =
            Tasks.whenAllSuccess<QuerySnapshot>(tasksList)

        allTasks.addOnSuccessListener { it ->

            for (meetingDocSnapshot in it) {
                for (querySnapshot in meetingDocSnapshot) {
                    val meeting = querySnapshot.toObject(MeetingObj::class.java)

                    Log.d("MEETING", "ACTUAL DATE= ${meeting.date}")

                    if (MeetingUtils.checkFiltersAreAllAccomplished(
                            meeting, filters,
                            sharedPreferencesService.readFromSharedPref<LatLng>(
                                SharedPreferences.userLocationKey, LatLng::class.java
                            )
                        )
                    ) {

                        if (meetingsList.find { it.uid == meeting.uid } != null || meeting.userUid == userUid) continue
                        meetingsList.add(meeting)
                    }

                }
            }
        }
            .addOnSuccessListener {
                tasksList.clear()
            }
            .addOnFailureListener { throw it }

        allTasks.await()

        return meetingsList
    }

    override suspend fun fetchMeetings(
        filtersList: ArrayList<IFilterObj>,
        userUid: String
    ): ArrayList<MeetingObj> {
        val meetingsList: ArrayList<MeetingObj> = try {
            if (filtersList.isNullOrEmpty()) {
                fetchAllOtherMeetings(userUid)
            } else {
                fetchMeetingsByFilters(filtersList, userUid)
            }
        } catch (e: Exception) {
            throw e
        }

        return meetingsList
    }

    override suspend fun fetchAllMeetingParticipants(meetingUid: String): ArrayList<ParticipantObj> {
        val participantsList = ArrayList<ParticipantObj>()
        val queryList = ArrayList<Task<QuerySnapshot>>()
        val query = firestore.collection(meetingsCollection)
            .document(meetingUid)
            .collection(meetingParticipants)
            .get()

        queryList.add(query)

        val allTasks =
            Tasks.whenAllSuccess<QuerySnapshot>(queryList)

        allTasks.addOnSuccessListener {

            for (participantsDocSnapshot in it) {
                for (querySnapshot in participantsDocSnapshot) {
                    val participantObj = querySnapshot.toObject(ParticipantObj::class.java)
                    participantsList.add(participantObj)
                }
            }
        }
            .addOnFailureListener { throw it }

        allTasks.await()

        return participantsList
    }

    override suspend fun fetchUserParticipantUidForMeeting(meetingUid: String, userUid: String): String {
        var participantUid = String()
        val queryList = ArrayList<Task<QuerySnapshot>>()
        val query = firestore.collection(meetingsCollection)
            .document(meetingUid)
            .collection(meetingParticipants)
            .whereEqualTo("userUid", userUid)
            .get()

        queryList.add(query)

        val allTasks =
            Tasks.whenAllSuccess<QuerySnapshot>(queryList)

        allTasks.addOnSuccessListener {

            for (participantsDocSnapshot in it) {
                for (querySnapshot in participantsDocSnapshot) {
                    val participantObj = querySnapshot.toObject(ParticipantObj::class.java)
                    participantUid = participantObj.uid!!
                }
            }
        }
            .addOnFailureListener { throw it }

        allTasks.await()

        return participantUid
    }

    override suspend fun fetchReviewUidForUser(
        userThatLeftReview: String,
        userThatReviewIsFor: String
    ): String? {
        var reviewUid: String? = null
        val queryList = ArrayList<Task<QuerySnapshot>>()
        val query = firestore
            .collection(reviewCollection)
            .whereEqualTo("userIdThatLeftReview", userThatLeftReview)
            .whereEqualTo("userThatReviewIsFor", userThatReviewIsFor)
            .get()

        queryList.add(query)

        val allTasks =
            Tasks.whenAllSuccess<QuerySnapshot>(queryList)

        allTasks.addOnSuccessListener {
            for (dogDocSnapshot in it) {
                for (querySnapshot in dogDocSnapshot) {
                    reviewUid = querySnapshot.toObject(ReviewObj::class.java).uid!!
                }
            }
        }
            .addOnFailureListener { throw it }

        allTasks.await()

        return reviewUid
    }

    override suspend fun deleteDog(
        dogUid: String,
        onCompleteListener: IOnCompleteListener
    ) {
        firestore.collection(dogInfoCollection)
            .document(dogUid)
            .delete()
            .addOnCompleteListener { onCompleteListener.onComplete(it.isSuccessful, it.exception) }
            .await()
    }

    override suspend fun deleteDogRelatedToUser(
        userUid: String,
        dogUid: String,
        onCompleteListener: IOnCompleteListener
    ) {
        firestore.collection(userInfoCollection)
            .document(userUid)
            .collection(dogInfoCollection)
            .document(dogUid)
            .delete()
            .addOnCompleteListener { onCompleteListener.onComplete(it.isSuccessful, it.exception) }
            .await()
    }

    override suspend fun deleteMeeting(
        meetingUid: String,
        onCompleteListener: IOnCompleteListener
    ) {
        firestore.collection(meetingsCollection)
            .document(meetingUid)
            .delete()
            .addOnCompleteListener { onCompleteListener.onComplete(it.isSuccessful, it.exception) }
            .await()
    }

    override suspend fun deleteParticipant(
        meetingUid: String,
        participantUid: String,
        onCompleteListener: IOnCompleteListener
    ) {
        firestore.collection(meetingsCollection)
            .document(meetingUid)
            .collection(meetingParticipants)
            .document(participantUid)
            .delete()
            .addOnCompleteListener { onCompleteListener.onComplete(it.isSuccessful, it.exception) }
            .await()
    }

}
