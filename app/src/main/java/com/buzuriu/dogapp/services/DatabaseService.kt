package com.buzuriu.dogapp.services

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.buzuriu.dogapp.listeners.IOnCompleteListener
import com.buzuriu.dogapp.models.*
import com.buzuriu.dogapp.utils.FieldsItems
import com.buzuriu.dogapp.utils.LocalDBItems
import com.buzuriu.dogapp.utils.MapUtils
import com.buzuriu.dogapp.utils.MeetingUtils
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.auth.User
import kotlinx.coroutines.tasks.await
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.temporal.TemporalAdjusters
import java.util.*
import kotlin.collections.ArrayList


interface IDatabaseService {
    val fireAuth: FirebaseAuth

    suspend fun storeUser(
        userUid: String,
        userObj: UserObj,
        onCompleteListener: IOnCompleteListener
    )

    suspend fun storeDog(userUid: String, dog: DogObj, onCompleteListener: IOnCompleteListener)
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

    suspend fun storeMeeting(
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
    suspend fun fetchUserByUid(userUid: String): UserObj?
    suspend fun fetchUserDogs(userUid: String): ArrayList<DogObj>?
    suspend fun fetchReviewsFor(field: String, userUid: String) : ArrayList<ReviewObj>?
    suspend fun fetchMeetings(
        filtersList: ArrayList<IFilterObj>,
        userUid: String
    ): ArrayList<MeetingObj>

    suspend fun fetchAllMeetingParticipants(meetingUid: String): ArrayList<ParticipantObj>?
    suspend fun fetchUserParticipantUidForMeeting(meetingUid: String, userUid: String): String?

    suspend fun fetchAllOtherMeetings(userUid: String): ArrayList<MeetingObj>?
    suspend fun fetchAllOtherPastMeetings(userUid: String): ArrayList<MeetingObj>?
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
    private val localDatabaseService: ILocalDatabaseService
) : IDatabaseService {
    override val fireAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val userCollection = "UserInfo"
    private val dogCollection = "Dog"
    private val meetingsCollection = "Meeting"
    private val reviewCollection = "Review"
    private val meetingParticipants = "MeetingParticipants"
    private var meetingsQuery: Query? = null
    private var tasksList = ArrayList<Task<QuerySnapshot>>()
    private var firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    override suspend fun storeUser(
        userUid: String,
        userObj: UserObj,
        onCompleteListener: IOnCompleteListener
    ) {
        firestore.collection(userCollection)
            .document(userUid)
            .set(userObj)
            .addOnCompleteListener { onCompleteListener.onComplete(it.isSuccessful, it.exception) }
            .await()
    }

    override suspend fun storeDogUidToUser(
        userUid: String,
        dogUid: String,
        onCompleteListener: IOnCompleteListener
    ) {
        firestore.collection(userCollection)
            .document(userUid)
            .collection(dogCollection)
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


    override suspend fun storeDog(
        userUid: String,
        dog: DogObj,
        onCompleteListener: IOnCompleteListener
    ) {
        firestore.collection(dogCollection)
            .document(dog.uid).set(dog)
            .addOnCompleteListener { onCompleteListener.onComplete(it.isSuccessful, it.exception) }
            .await()
    }

    override suspend fun storeMeeting(
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
            .update(FieldsItems.dogUid, newDogUid)
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
            firestore.collection(dogCollection)
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

    override suspend fun fetchUserByUid(userUid: String): UserObj? {
        var userObj: UserObj? = null
        val documentSnapshot =
            firestore.collection(userCollection)
                .document(userUid)
                .get()
                .await()

        if (documentSnapshot != null) {
            try {
                userObj = documentSnapshot.toObject(UserObj::class.java)
            } catch (e: Exception) {
                Log.d("Error", e.message.toString())
            }
        }

        return userObj
    }

    override suspend fun fetchUserDogs(userUid: String): ArrayList<DogObj> {
        val dogList = ArrayList<DogObj>()
        val queryList = ArrayList<Task<QuerySnapshot>>()
        val query = firestore.collection(dogCollection)
            .whereEqualTo(FieldsItems.owner, userUid)
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

    override suspend fun fetchReviewsFor(
        field: String,
        userUid: String
    ): ArrayList<ReviewObj> {
        val reviewList = ArrayList<ReviewObj>()
        val queryList = ArrayList<Task<QuerySnapshot>>()
        val query = firestore.collection(reviewCollection)
            .whereEqualTo(field, userUid)
            .get()

        queryList.add(query)

        val allTasks =
            Tasks.whenAllSuccess<QuerySnapshot>(queryList)

        allTasks.addOnSuccessListener {
            for (reviewDocSnapshot in it) {
                for (querySnapshot in reviewDocSnapshot) {
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
            .whereEqualTo(FieldsItems.userUid, userUid)
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
            .whereEqualTo(FieldsItems.dogUid, dogUid)
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

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setMeetingsTimeQuery(filterType: IFilterObj) {
        var start = Calendar.getInstance()
        var end = Calendar.getInstance()

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
            "Next Friday" -> {
                start.timeInMillis = System.currentTimeMillis()
                end.timeInMillis = System.currentTimeMillis()

                start[Calendar.HOUR_OF_DAY] = 0
                start[Calendar.MINUTE] = 0
                start[Calendar.SECOND] = 0
                start[Calendar.MILLISECOND] = 0

                end[Calendar.HOUR_OF_DAY] = 23
                end[Calendar.MINUTE] = 59
                end[Calendar.SECOND] = 59
                end[Calendar.MILLISECOND] = 999


                val localDate: LocalDate = LocalDate.now()

                val startLocalDate =
                    localDate.with(TemporalAdjusters.next(DayOfWeek.valueOf(DayOfWeek.FRIDAY.toString())))
                val endLocalDate =
                    localDate.with(TemporalAdjusters.next(DayOfWeek.valueOf(DayOfWeek.FRIDAY.toString())))

                val startLong =
                    startLocalDate.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
                val endLong = endLocalDate.atTime(23, 59, 59).atOffset(ZoneOffset.UTC).toInstant()
                    .toEpochMilli()

                start = longToCalendar(startLong)!!
                end = longToCalendar(endLong)!!
            }
            "Next Saturday" -> {
                start.timeInMillis = System.currentTimeMillis()
                end.timeInMillis = System.currentTimeMillis()

                start[Calendar.HOUR_OF_DAY] = 0
                start[Calendar.MINUTE] = 0
                start[Calendar.SECOND] = 0
                start[Calendar.MILLISECOND] = 0

                end[Calendar.HOUR_OF_DAY] = 23
                end[Calendar.MINUTE] = 59
                end[Calendar.SECOND] = 59
                end[Calendar.MILLISECOND] = 999


                val localDate: LocalDate = LocalDate.now()

                val startLocalDate =
                    localDate.with(TemporalAdjusters.next(DayOfWeek.valueOf(DayOfWeek.SATURDAY.toString())))
                val endLocalDate =
                    localDate.with(TemporalAdjusters.next(DayOfWeek.valueOf(DayOfWeek.SATURDAY.toString())))

                val startLong =
                    startLocalDate.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
                val endLong = endLocalDate.atTime(23, 59, 59).atOffset(ZoneOffset.UTC).toInstant()
                    .toEpochMilli()

                start = longToCalendar(startLong)!!
                end = longToCalendar(endLong)!!
            }
            "Next Sunday" -> {
                start.timeInMillis = System.currentTimeMillis()
                end.timeInMillis = System.currentTimeMillis()

                start[Calendar.HOUR_OF_DAY] = 0
                start[Calendar.MINUTE] = 0
                start[Calendar.SECOND] = 0
                start[Calendar.MILLISECOND] = 0

                end[Calendar.HOUR_OF_DAY] = 23
                end[Calendar.MINUTE] = 59
                end[Calendar.SECOND] = 59
                end[Calendar.MILLISECOND] = 999


                val localDate: LocalDate = LocalDate.now()

                val startLocalDate =
                    localDate.with(TemporalAdjusters.next(DayOfWeek.valueOf(DayOfWeek.SUNDAY.toString())))
                val endLocalDate =
                    localDate.with(TemporalAdjusters.next(DayOfWeek.valueOf(DayOfWeek.SUNDAY.toString())))

                val startLong =
                    startLocalDate.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
                val endLong = endLocalDate.atTime(23, 59, 59).atOffset(ZoneOffset.UTC).toInstant()
                    .toEpochMilli()

                start = longToCalendar(startLong)!!
                end = longToCalendar(endLong)!!
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
            .whereEqualTo(FieldsItems.dogGender, filterType.name)
            .get()
        tasksList.add(query)
    }

    private fun setUserGenderTypeQuery(filterType: IFilterObj) {
        val query = meetingsQuery!!
            .whereEqualTo(FieldsItems.userGender, filterType.name)
            .get()
        tasksList.add(query)
    }

    private fun setDogBreedTypeQuery(filterType: IFilterObj) {
        val query = meetingsQuery!!
            .whereEqualTo(FieldsItems.dogBreed, filterType.name)
            .get()
        tasksList.add(query)
    }

    private fun setMeetingsDistanceQuery(radiusInKM: Int) {
        val myCurrentUserLocation = localDatabaseService.get<LatLng>(LocalDBItems.userLocation)
        if (myCurrentUserLocation == null) {
            Log.d("Error", "Current User Location is null in Local Database")
        }

        val center = LatLng(
            myCurrentUserLocation!!.latitude,
            myCurrentUserLocation.longitude
        )

        val bounds = MapUtils.getSouthWestAndNorthEastPointsAroundLocation(radiusInKM, center)

        val distanceQuery =
            meetingsQuery?.whereGreaterThan(
                "location", GeoPoint(
                    bounds.first.latitude,
                    bounds.first.longitude
                )
            )!!
                .whereLessThan(
                    "location", GeoPoint(
                        bounds.second.latitude,
                        bounds.second.longitude
                    )
                ).get()

        tasksList.add(distanceQuery)
    }

    @RequiresApi(Build.VERSION_CODES.O)
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

    @RequiresApi(Build.VERSION_CODES.O)
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

                    if (MeetingUtils.checkFiltersAreAllAccomplished(
                            meeting, filters,
                            localDatabaseService.get<LatLng>(LocalDBItems.userLocation)
                        ) && !MeetingUtils.isMeetingInThePast(meeting)
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

    @RequiresApi(Build.VERSION_CODES.O)
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

    override suspend fun fetchUserParticipantUidForMeeting(
        meetingUid: String,
        userUid: String
    ): String {
        var participantUid = String()
        val queryList = ArrayList<Task<QuerySnapshot>>()
        val query = firestore.collection(meetingsCollection)
            .document(meetingUid)
            .collection(meetingParticipants)
            .whereEqualTo(FieldsItems.userUid, userUid)
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

    override suspend fun deleteDog(
        dogUid: String,
        onCompleteListener: IOnCompleteListener
    ) {
        firestore.collection(dogCollection)
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
        firestore.collection(userCollection)
            .document(userUid)
            .collection(dogCollection)
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

    private fun longToCalendar(time: Long?): Calendar? {
        var c: Calendar? = null
        if (time != null) {
            c = Calendar.getInstance()
            c.timeInMillis = time
        }
        return c
    }

}
