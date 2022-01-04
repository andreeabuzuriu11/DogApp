package com.buzuriu.dogapp.services

import com.buzuriu.dogapp.listeners.IGetUserDogListListener
import com.buzuriu.dogapp.listeners.IOnCompleteListener
import com.buzuriu.dogapp.models.DogObj
import com.buzuriu.dogapp.models.UserInfo
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.tasks.await

interface IDatabaseService {
    val fireAuth: FirebaseAuth
    suspend fun storeUserInfo(userUid: String, userInfo: UserInfo, onCompleteListener: IOnCompleteListener)
    suspend fun storeDogInfo(userUid: String, dog: DogObj, onCompleteListener: IOnCompleteListener)
    suspend fun fetchUserDogs(userUid: String, dogListListener: IGetUserDogListListener)
}

class DatabaseService : IDatabaseService {
    override val fireAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val userInfoCollection = "UserInfo"
    private val dogInfoCollection = "Dog"
    private var firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    override suspend fun storeUserInfo(userUid: String, userInfo: UserInfo, onCompleteListener: IOnCompleteListener) {
        firestore.collection(userInfoCollection)
            .document(userUid)
            .set(userInfo)
            .addOnCompleteListener { onCompleteListener.onComplete(it.isSuccessful, it.exception) }
            .await()
    }

    override suspend fun storeDogInfo(
        userUid: String,
        dog: DogObj,
        onCompleteListener: IOnCompleteListener
    ) {
        firestore.collection(userInfoCollection)
            .document(userUid)
            .collection(dogInfoCollection)
            .document(dog.uid).set(dog)
            .addOnCompleteListener { onCompleteListener.onComplete(it.isSuccessful, it.exception) }
            .await()
    }

    override suspend fun fetchUserDogs(userUid: String, dogListListener: IGetUserDogListListener) {

        val queryList = ArrayList<Task<QuerySnapshot>>()
        val query = firestore.collection(userInfoCollection)
            .document(userUid)
            .collection(dogInfoCollection)
            .get()

        queryList.add(query)

        val allTasks =
            Tasks.whenAllSuccess<QuerySnapshot>(queryList)

        allTasks.addOnSuccessListener {
            val dogList = ArrayList<DogObj>()
            for (dogDocSnapshot in it) {
                for (querySnapshot in dogDocSnapshot) {
                    val dog = querySnapshot.toObject(DogObj::class.java)
                    dogList.add(dog)
                }
            }

            dogListListener.getDogList(dogList)
        }
            .addOnFailureListener { throw it }
    }

}
