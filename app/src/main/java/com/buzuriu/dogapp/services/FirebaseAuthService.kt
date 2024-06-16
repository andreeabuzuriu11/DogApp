package com.buzuriu.dogapp.services

import com.buzuriu.dogapp.listeners.IOnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

interface IFirebaseAuthService {
    fun getCurrentUser(): FirebaseUser?

    suspend fun registerWithEmailAndPassword(
        email: String,
        password: String,
        onCompleteListener: IOnCompleteListener
    )

    suspend fun login(
        email: String,
        password: String,
        onCompleteListener: IOnCompleteListener
    )

    suspend fun resetPassword(email: String, onCompleteListener: IOnCompleteListener)
    fun deleteAccount(onCompleteListener: IOnCompleteListener)

    fun logout()
}

class FirebaseAuthService : IFirebaseAuthService {

    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    override fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }

    override suspend fun login(
        email: String,
        password: String,
        onCompleteListener: IOnCompleteListener
    ) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
            onCompleteListener.onComplete(it.isSuccessful, it.exception)
        }
    }

    override suspend fun resetPassword(email: String, onCompleteListener: IOnCompleteListener) {
        auth.sendPasswordResetEmail(email).addOnCompleteListener {
            onCompleteListener.onComplete(it.isSuccessful, it.exception)
        }
    }

    override suspend fun registerWithEmailAndPassword(
        email: String, password: String, onCompleteListener: IOnCompleteListener
    ) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
            onCompleteListener.onComplete(it.isSuccessful, it.exception)
        }
    }

    override fun logout() {
        auth.signOut()
    }

    override fun deleteAccount(onCompleteListener: IOnCompleteListener) {
        var currentUser = getCurrentUser()
        currentUser!!.delete().addOnCompleteListener{
            onCompleteListener.onComplete(it.isSuccessful, it.exception)
        }
    }
}