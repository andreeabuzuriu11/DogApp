package com.buzuriu.dogapp.services

import com.buzuriu.dogapp.listeners.IOnCompleteListener
import com.buzuriu.dogapp.models.UserInfo
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

interface IDatabaseService {
    val fireAuth: FirebaseAuth
    suspend fun storeUserInfo(userUid: String, userInfo: UserInfo, onCompleteListener: IOnCompleteListener)
}

class DatabaseService : IDatabaseService {
    override val fireAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val userInfoCollection = "UserInfo"
    private var firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    override suspend fun storeUserInfo(userUid: String, userInfo: UserInfo, onCompleteListener: IOnCompleteListener) {
        firestore.collection(userInfoCollection)
            .document(userUid)
            .set(userInfo)
            .addOnCompleteListener { onCompleteListener.onComplete(it.isSuccessful,it.exception) }
            .await()
    }
}
