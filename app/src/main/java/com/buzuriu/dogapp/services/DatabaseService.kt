package com.buzuriu.dogapp.services

import android.util.Log
import com.buzuriu.dogapp.listeners.*
import com.buzuriu.dogapp.models.*
import com.buzuriu.dogapp.utils.MeetingUtils
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.tasks.await
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList

interface IDatabaseService {
    val fireAuth: FirebaseAuth
    suspend fun storeUserInfo(userUid: String, userInfo: UserInfo, onCompleteListener: IOnCompleteListener)
    suspend fun storeDogInfo(userUid: String, dog: DogObj, onCompleteListener: IOnCompleteListener)
    suspend fun storeDogUidToUser(userUid: String, dogUid: String, onCompleteListener: IOnCompleteListener)
    suspend fun storeMeetingInfo(meetingUid: String, meetingObj: MeetingObj, onCompleteListener: IOnCompleteListener)
    suspend fun fetchDogByUid(dogUid: String) : DogObj?
    suspend fun fetchUserByUid(userUid: String) : UserInfo?
    suspend fun fetchUserDogs(userUid: String) : ArrayList<DogObj>?
    suspend fun fetchAllMeetings() : ArrayList <MeetingObj>?
    suspend fun fetchMeetingsByFilters(filters: ArrayList<IFilterObj>) : ArrayList <MeetingObj>?
    suspend fun deleteDog(userUid: String,
                           dogUid: String,
                           onCompleteListener: IOnCompleteListener)
}

class DatabaseService : IDatabaseService {
    override val fireAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val userInfoCollection = "UserInfo"
    private val dogInfoCollection = "Dog"
    private val meetingsCollection = "Meeting"
    private var meetingsQuery: Query? = null
    private var tasksList = ArrayList<Task<QuerySnapshot>>()
    private var firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    override suspend fun storeUserInfo(userUid: String, userInfo: UserInfo, onCompleteListener: IOnCompleteListener) {
        firestore.collection(userInfoCollection)
            .document(userUid)
            .set(userInfo)
            .addOnCompleteListener { onCompleteListener.onComplete(it.isSuccessful, it.exception) }
            .await()
    }

    override suspend fun storeDogUidToUser(userUid: String, dogUid: String, onCompleteListener: IOnCompleteListener)
    {
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

    override suspend fun fetchUserDogs(userUid: String) : ArrayList<DogObj> {
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
        return dogList
    }

    override suspend fun fetchAllMeetings() : ArrayList<MeetingObj> {
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
                    meetingsList.add(meeting)
                }
            }
        }
            .addOnFailureListener { throw it }

        allTasks.await()

        return meetingsList
    }

    private fun setMeetingsTimeQuery(filterType: IFilterObj)
    {
        val start = Calendar.getInstance()
        val end = Calendar.getInstance()

        var query: Task<QuerySnapshot>?

        start.timeInMillis = System.currentTimeMillis()
        end.time = Calendar.getInstance().time
        end[Calendar.HOUR_OF_DAY] = 23
        end[Calendar.MINUTE] = 59
        end[Calendar.SECOND] = 59
        end[Calendar.MILLISECOND] = 999

        if (filterType.name == "Today") {
            // all options for today are already selected above
        }

        else if (filterType.name == "Tomorrow") {
            start.time = Calendar.getInstance().time
            start.add(Calendar.DATE, 1)
            start[Calendar.HOUR_OF_DAY] = 0
            start[Calendar.MINUTE] = 0
            start[Calendar.SECOND] = 0
            start[Calendar.MILLISECOND] = 0

            end.add(Calendar.DATE, 1)
        }

        else if (filterType.name == "This week") {
            start.time = Calendar.getInstance().time
            start[Calendar.HOUR_OF_DAY] = 0
            start[Calendar.MINUTE] = 0
            start[Calendar.SECOND] = 0

            var todayAsDayOfWeek = Calendar.DAY_OF_WEEK
            var daysTillSunday = 7 - todayAsDayOfWeek
            end.add(Calendar.DATE, daysTillSunday)
        }

        else if (filterType.name == "This month") {
            end.add(Calendar.MONTH, 1);
            end.set(Calendar.DAY_OF_MONTH, 1);
            end.add(Calendar.DATE, -1);
        }

        else if (filterType.name == "Next week") {
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

        else if (filterType.name == "Next month") {
            start.time = Calendar.getInstance().time
            start.add(Calendar.MONTH, 1)
            start[Calendar.DAY_OF_MONTH] = 1
            start[Calendar.HOUR_OF_DAY] = 0
            start[Calendar.MINUTE] = 0
            start[Calendar.SECOND] = 0
            start[Calendar.MILLISECOND] = 0

            end.timeInMillis = System.currentTimeMillis()
            end.add(Calendar.MONTH, 2);
            end.set(Calendar.DAY_OF_MONTH, 1);
            end.add(Calendar.DATE, -1);
        }
        else {
            query = meetingsQuery!!
                .whereLessThan("date", System.currentTimeMillis()).get()
        }

        query = meetingsQuery!!
            .whereGreaterThan("date", start.timeInMillis)
            .whereLessThan("date", end.timeInMillis)
            .get()

        tasksList.add(query)
    }


    private fun setDogGenderTypeQuery(filterType: IFilterObj)
    {
        val query = meetingsQuery!!
            .whereEqualTo("dogGender", filterType.name)
            .get()
        tasksList.add(query)
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
            }
        }
    }

    override suspend fun fetchMeetingsByFilters(filters: ArrayList<IFilterObj>): ArrayList<MeetingObj>? {
        val meetingsList = ArrayList<MeetingObj>()

        meetingsQuery =
            firestore.collection(meetingsCollection)
                .limit(2)

        createFilterQuery(filters)

        val allTasks =
            Tasks.whenAllSuccess<QuerySnapshot>(tasksList)

        allTasks.addOnSuccessListener {

            for (meetingDocSnapshot in it) {
                for (querySnapshot in meetingDocSnapshot) {
                    val meeting = querySnapshot.toObject(MeetingObj::class.java)

                    if (MeetingUtils.checkFiltersAreAllAccomplished(meeting, filters))
                    {
                        if(meetingsList.find { it.uid == meeting.uid }!=null)continue
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

    override suspend fun deleteDog(
        userUid: String,
        dogUid: String,
        onCompleteListener: IOnCompleteListener
    ) {
        firestore.collection(userInfoCollection)
            .document(userUid)
            .collection(dogInfoCollection)
            .document(dogUid).delete()
            .addOnCompleteListener { onCompleteListener.onComplete(it.isSuccessful, it.exception) }
            .await()
    }

}
