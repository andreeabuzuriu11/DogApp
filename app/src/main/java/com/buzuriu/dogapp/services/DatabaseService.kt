package com.buzuriu.dogapp.services

import android.util.Log
import com.buzuriu.dogapp.listeners.*
import com.buzuriu.dogapp.models.DogObj
import com.buzuriu.dogapp.models.MeetingObj
import com.buzuriu.dogapp.models.UserInfo
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await
import java.lang.Exception
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
    suspend fun deleteDog(userUid: String,
                           dogUid: String,
                           onCompleteListener: IOnCompleteListener)
}

class DatabaseService : IDatabaseService {
    override val fireAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val userInfoCollection = "UserInfo"
    private val dogInfoCollection = "Dog"
    private val meetingsCollection = "Meeting"
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
/*        firestore.collection(userInfoCollection)
            .document(userUid)
            .collection(dogInfoCollection)
            .document(dogUid)
            .set(dogUid)
            .addOnCompleteListener { onCompleteListener.onComplete(it.isSuccessful, it.exception) }
            .await()*/
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
       /* firestore.collection(userInfoCollection)
            .document(userUid)
            .collection(dogInfoCollection)
            .document(dog.uid).set(dog)
            .addOnCompleteListener { onCompleteListener.onComplete(it.isSuccessful, it.exception) }
            .await()*/
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
