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
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.TaskCompletionSource
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import kotlinx.coroutines.tasks.await
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.temporal.TemporalAdjusters
import java.util.*


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
    suspend fun fetchDogByUid(dogUid: String, onCompleteListener: IOnCompleteListener? = null): DogObj?
    suspend fun fetchUserByUid(userUid: String, onCompleteListener: IOnCompleteListener): UserObj?
    suspend fun fetchUsers(onCompleteListener: IOnCompleteListener): List<UserObj>?
    suspend fun fetchUserDogs(userUid: String): ArrayList<DogObj>?
    suspend fun fetchReviewsFor(field: String, userUid: String): ArrayList<ReviewObj>?
    suspend fun fetchMeetings(
        filtersList: ArrayList<IFilterObj>?,
        userUid: String,
        onCompleteListener: IOnCompleteListener
    ): ArrayList<MeetingObj>

    suspend fun fetchAllMeetingParticipants(meetingUid: String): ArrayList<ParticipantObj>?
    suspend fun fetchUserParticipantUidForMeeting(meetingUid: String, userUid: String): String?

    suspend fun fetchAllOtherMeetings(
        userUid: String,
        onCompleteListener: IOnCompleteListener
    ): ArrayList<MeetingObj>?

    suspend fun fetchAllOtherPastMeetings(userUid: String): ArrayList<MeetingObj>?
    suspend fun fetchAllPastMeetings(userUid: String): ArrayList<MeetingObj>?
    suspend fun fetchUserMeetings(userUid: String, onCompleteListener: IOnCompleteListener) : ArrayList<MeetingObj>?
    suspend fun fetchDogMeetings(dogUid: String): ArrayList<MeetingObj>?
    suspend fun fetchMeetingsByFilters(
        filters: ArrayList<IFilterObj>,
        userUid: String
    ): ArrayList<MeetingObj>?

    suspend fun fetchFriendsOrRequestsUsersList(
        userUid: String,
        listToFetch: String
    ): List<String>?

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

    // to delete
    suspend fun sendFriendRequest(
        userIdThatSends: String,
        userIdThatReceives: String,
        onCompleteListener: IOnCompleteListener
    )

    suspend fun newSendFriendRequest(
        userId: String,
        requestObj: RequestObj,
        onCompleteListener: IOnCompleteListener
    )

    // to delete
    suspend fun deleteRequest(
        userIdThatAccepts: String,
        userIdThatSentRequest: String,
        onCompleteListener: IOnCompleteListener
    )

    // to delete
    suspend fun addFriendToList(
        userIdThatAccepts: String,
        userIdThatSentRequest: String,
        onCompleteListener: IOnCompleteListener
    )

    suspend fun fetchRequestObj(
        userId: String,
        onCompleteListener: IOnCompleteListener
    ): RequestObj?

    suspend fun acceptRequest(
        userAccepting: String,
        userRequesting: String
    )

    suspend fun declineRequest(
        userDeclining: String,
        userRequesting: String
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
    private val friendRequestsCollection = "Requests"
    private val ownRequests = "OwnRequests"
    private val friendRequests = "FriendRequests"
    private val myFriends = "MyFriends"
    private val meetingParticipants = "MeetingParticipants"
    private var meetingsQuery: Query? = null
    private var tasksQueryList = ArrayList<Task<QuerySnapshot>>()
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

    override suspend fun sendFriendRequest(
        userIdThatSends: String,
        userIdThatReceives: String,
        onCompleteListener: IOnCompleteListener
    ) {
        val ownRequests = hashMapOf(ownRequests to FieldValue.arrayUnion(userIdThatReceives));
        val friendRequests = hashMapOf(friendRequests to FieldValue.arrayUnion(userIdThatSends))

        firestore.collection(friendRequestsCollection)
            .document(userIdThatSends)
            .set(ownRequests, SetOptions.merge())
            .addOnCompleteListener { onCompleteListener.onComplete(it.isSuccessful, it.exception) }
            .await()

        firestore.collection(friendRequestsCollection)
            .document(userIdThatReceives)
            .set(friendRequests, SetOptions.merge())
            .addOnCompleteListener { onCompleteListener.onComplete(it.isSuccessful, it.exception) }
            .await()
    }

    override suspend fun newSendFriendRequest(
        userId: String,
        requestObj: RequestObj,
        onCompleteListener: IOnCompleteListener
    ) {
        firestore.collection(friendRequestsCollection)
            .document(userId)
            .set(requestObj)
            .addOnCompleteListener { onCompleteListener.onComplete(it.isSuccessful, it.exception) }
            .await()
    }

    override suspend fun deleteRequest(
        userIdThatAccepts: String,
        userIdThatSentRequest: String,
        onCompleteListener: IOnCompleteListener
    ) {
        var userThatSentOwnReqList =
            fetchFriendsOrRequestsUsersList(userIdThatSentRequest, ownRequests)
        var userThatAcceptsFriendReqList =
            fetchFriendsOrRequestsUsersList(userIdThatAccepts, friendRequests)

        userThatSentOwnReqList = userThatSentOwnReqList!!.filter { !it.equals(userIdThatAccepts) }
        userThatAcceptsFriendReqList =
            userThatAcceptsFriendReqList!!.filter { !it.equals(userIdThatSentRequest) }

        val userThatSentOwnReqHashMap = hashMapOf(ownRequests to userThatSentOwnReqList);
        val userThatAcceptsFriendReqHashMap =
            hashMapOf(friendRequests to userThatAcceptsFriendReqList);

        firestore.collection(friendRequestsCollection)
            .document(userIdThatSentRequest)
            .set(userThatSentOwnReqHashMap)
            .addOnCompleteListener { onCompleteListener.onComplete(it.isSuccessful, it.exception) }
            .addOnSuccessListener {
                println("Successfully deleted $userIdThatSentRequest")
            }
            .addOnFailureListener { exception ->
                println("Didn't manage to delete $userIdThatSentRequest")
            }

        firestore.collection(friendRequestsCollection)
            .document(userIdThatAccepts)
            .set(userThatAcceptsFriendReqHashMap)
            .addOnCompleteListener { onCompleteListener.onComplete(it.isSuccessful, it.exception) }
            .addOnSuccessListener {
                println("Successfully deleted $userIdThatAccepts")
            }
            .addOnFailureListener { exception ->
                println("Didn't manage to delete $userIdThatAccepts")
            }

    }

    override suspend fun addFriendToList(
        userIdThatAccepts: String,
        userIdThatSentRequest: String,
        onCompleteListener: IOnCompleteListener
    ) {
        var friendsOfUserThatSentRequest =
            fetchFriendsOrRequestsUsersList(userIdThatSentRequest, myFriends)
        var friendsOfUserThatAcceptedRequest =
            fetchFriendsOrRequestsUsersList(userIdThatAccepts, myFriends)
        if (friendsOfUserThatSentRequest == null)
            friendsOfUserThatSentRequest = listOf()

        if (friendsOfUserThatAcceptedRequest == null)
            friendsOfUserThatAcceptedRequest = listOf()

        friendsOfUserThatAcceptedRequest =
            friendsOfUserThatAcceptedRequest!!.plus(userIdThatSentRequest)
        friendsOfUserThatSentRequest = friendsOfUserThatSentRequest!!.plus(userIdThatAccepts)

        var listUpdatedWithNewFriend1 = hashMapOf(myFriends to friendsOfUserThatAcceptedRequest);

        var listUpdatedWithNewFriend2 = hashMapOf(myFriends to friendsOfUserThatSentRequest)


        firestore.collection(friendRequestsCollection)
            .document(userIdThatSentRequest)
            .set(listUpdatedWithNewFriend2)
            .addOnCompleteListener { onCompleteListener.onComplete(it.isSuccessful, it.exception) }
            .addOnSuccessListener {
                println("Successfully deleted $userIdThatSentRequest")
            }
            .addOnFailureListener { exception ->
                println("Didn't manage to delete $userIdThatSentRequest")
            }

        firestore.collection(friendRequestsCollection)
            .document(userIdThatAccepts)
            .set(listUpdatedWithNewFriend1)
            .addOnCompleteListener { onCompleteListener.onComplete(it.isSuccessful, it.exception) }
            .addOnSuccessListener {
                println("Successfully deleted $userIdThatSentRequest")
            }
            .addOnFailureListener { exception ->
                println("Didn't manage to delete $userIdThatSentRequest")
            }
    }

    override suspend fun fetchRequestObj(
        userId: String,
        onCompleteListener: IOnCompleteListener
    ): RequestObj? {
        var requestObj: RequestObj? = null
        val documentSnapshot =
            firestore.collection(friendRequestsCollection)
                .document(userId)
                .get()
                .await()

        if (documentSnapshot != null && documentSnapshot.exists()) {
            try {
                requestObj = documentSnapshot.toObject(RequestObj::class.java)

                println("user " + userId + " ownRequests: ${requestObj?.ownRequests}")
                println("user " + userId + " friendsRequests: ${requestObj?.friendsRequests}")
                println("user " + userId + " myFriends: ${requestObj?.myFriends}")

                return requestObj

            } catch (e: Exception) {
                Log.d("Error", e.message.toString())
                return null
            }
        }

        return null
    }

    override suspend fun acceptRequest(userAccepting: String, userRequesting: String) {
        // delete req from both lists + update

        var userRequestingReq = fetchRequestObj(userRequesting, object : IOnCompleteListener {
            override fun onComplete(successful: Boolean, exception: Exception?) {
            }
        })
        var userRequestingOwnRequests = userRequestingReq!!.ownRequests
        var userRequestingMyFriends = userRequestingReq!!.myFriends

        // delete user accepting from  "own requests" user requesting
        userRequestingOwnRequests!!.remove(userAccepting)

        // add user accepting to "my friends" user requesting
        userRequestingMyFriends!!.add(userAccepting)

        var userAcceptingReq = fetchRequestObj(userAccepting, object : IOnCompleteListener {
            override fun onComplete(successful: Boolean, exception: Exception?) {
            }
        })
        var userAcceptingFriendRequests = userAcceptingReq!!.friendsRequests
        var userAcceptingMyFriends = userAcceptingReq!!.myFriends

        // delete user requesting from "friend requests" user accepting
        userAcceptingFriendRequests!!.remove(userRequesting)

        // add requesting to "my friends" user accepting
        userAcceptingMyFriends!!.add(userRequesting)


        // update the new req
        newSendFriendRequest(userAccepting, userAcceptingReq, object : IOnCompleteListener {
            override fun onComplete(successful: Boolean, exception: java.lang.Exception?) {}
        })
        newSendFriendRequest(userRequesting, userRequestingReq, object : IOnCompleteListener {
            override fun onComplete(successful: Boolean, exception: java.lang.Exception?) {}
        })

    }

    override suspend fun declineRequest(userDeclining: String, userRequesting: String) {
        // delete req from both lists + update
        // todo this logic is duplicated also for accept req

        var userRequestingReq = fetchRequestObj(userRequesting, object : IOnCompleteListener {
            override fun onComplete(successful: Boolean, exception: Exception?) {
            }
        })
        var userRequestingOwnRequests = userRequestingReq!!.ownRequests

        // delete user declining from  "own requests" user requesting
        userRequestingOwnRequests!!.remove(userDeclining)

        var userDecliningReq = fetchRequestObj(userDeclining, object : IOnCompleteListener {
            override fun onComplete(successful: Boolean, exception: Exception?) {
            }
        })
        var userDecliningFriendRequests = userDecliningReq!!.friendsRequests

        // delete user requesting from "friend requests" user declining
        userDecliningFriendRequests!!.remove(userRequesting)

        // update the new req
        newSendFriendRequest(userDeclining, userDecliningReq, object : IOnCompleteListener {
            override fun onComplete(successful: Boolean, exception: java.lang.Exception?) {}
        })
        newSendFriendRequest(userRequesting, userRequestingReq, object : IOnCompleteListener {
            override fun onComplete(successful: Boolean, exception: java.lang.Exception?) {}
        })
    }


    inline fun <reified T> Array<T>.removeValue(value: T) =
        filterNot { it == value }.toTypedArray()

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

    override suspend fun fetchDogByUid(dogUid: String, onCompleteListener: IOnCompleteListener?): DogObj? {
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

    override suspend fun fetchUserByUid(
        userUid: String,
        onCompleteListener: IOnCompleteListener
    ): UserObj? {
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

    override suspend fun fetchUsers(onCompleteListener: IOnCompleteListener): List<UserObj>? {
        var usersList: List<UserObj>? = null
        val querySnapshot =
            firestore.collection(userCollection)
                .get()
                .await()

        if (querySnapshot != null) {
            try {
                usersList = querySnapshot.toObjects(UserObj::class.java)
            } catch (e: Exception) {
                Log.d("Error", e.message.toString())
            }
        }

        return usersList
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

    override suspend fun fetchAllOtherMeetings(
        userUid: String,
        onCompleteListener: IOnCompleteListener
    ): ArrayList<MeetingObj> {
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

    override suspend fun fetchAllPastMeetings(userUid: String): ArrayList<MeetingObj> {
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

    override suspend fun fetchUserMeetings(userUid: String, onCompleteListener: IOnCompleteListener): ArrayList<MeetingObj> {
        val meetingsList = ArrayList<MeetingObj>()
        val queryList = ArrayList<Task<QuerySnapshot>>()
        val query = firestore.collection(meetingsCollection)
            .whereEqualTo(FieldsItems.userUid, userUid)
            .get()

        queryList.add(query)

        val allTasks =
            Tasks.whenAllSuccess<QuerySnapshot>(queryList)

        allTasks.addOnSuccessListener {
            println("the reading is successful")

            for (meetingDocSnapshot in it) {
                for (querySnapshot in meetingDocSnapshot) {
                    val meeting = querySnapshot.toObject(MeetingObj::class.java)
                    meetingsList.add(meeting)
                }
            }

            println("meetingList for " + userUid + " = " + meetingsList.count())

        }
            .addOnFailureListener {
                println("the reading is not successful")
            }

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

        tasksQueryList.add(query)
    }


    private fun setDogGenderTypeQuery(filterType: IFilterObj) {
        val query = meetingsQuery!!
            .whereEqualTo(FieldsItems.dogGender, filterType.name)
            .get()
        tasksQueryList.add(query)
    }

    private fun setUserGenderTypeQuery(filterType: IFilterObj) {
        val query = meetingsQuery!!
            .whereEqualTo(FieldsItems.userGender, filterType.name)
            .get()
        tasksQueryList.add(query)
    }

    private fun setDogBreedTypeQuery(filterType: IFilterObj) {
        val query = meetingsQuery!!
            .whereEqualTo(FieldsItems.dogBreed, filterType.name)
            .get()
        tasksQueryList.add(query)
    }

    private fun setMeetingsDistanceQuery(radiusInKM: Int) {
        val myCurrentUserLocation = localDatabaseService.get<LatLng>(LocalDBItems.userLocation)
        if (myCurrentUserLocation == null) {
            Log.d("Error", "User's location does not exits. Check LocalDbService")
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

        tasksQueryList.add(distanceQuery)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setFiltersQuery(filtersList: ArrayList<IFilterObj>) {
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

        setFiltersQuery(filters)

        val allTasks =
            Tasks.whenAllSuccess<QuerySnapshot>(tasksQueryList)

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
                tasksQueryList.clear()
            }
            .addOnFailureListener { throw it }

        allTasks.await()

        return meetingsList
    }

    override suspend fun fetchFriendsOrRequestsUsersList(
        userUid: String,
        listToFetch: String
    ): List<String>? {
        // Fetch the document
        var taskCompletionSource = TaskCompletionSource<List<String>>()

        firestore.collection(friendRequestsCollection)
            .document(userUid).get().addOnSuccessListener { document ->
                if (document.exists()) {
                    // Document exists, access the array field
                    val dataArray = document[listToFetch] as? List<String>
                    if (dataArray != null) {
                        // Use the array of strings
                        taskCompletionSource.trySetResult(dataArray)
                    } else {
                        taskCompletionSource.trySetResult(null)
                        println("Array field is null or not a List<String>")
                    }
                } else {
                    taskCompletionSource.trySetResult(null)
                    println("Document not found")
                }
            }.addOnFailureListener { exception ->
                {
                    taskCompletionSource.trySetResult(null)
                    println("Error fetching document: $exception")
                }
            }
        return taskCompletionSource.task.await()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun fetchMeetings(
        filtersList: ArrayList<IFilterObj>?,
        userUid: String, onCompleteListener: IOnCompleteListener
    ): ArrayList<MeetingObj> {
        val meetingsList: ArrayList<MeetingObj> = try {

            if (filtersList.isNullOrEmpty()) {
                fetchAllOtherMeetings(userUid, object : IOnCompleteListener {
                    override fun onComplete(successful: Boolean, exception: java.lang.Exception?) {}
                })
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
