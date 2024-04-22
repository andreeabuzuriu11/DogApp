package com.buzuriu.dogapp.services

import com.buzuriu.dogapp.utils.StringUtils
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await

interface IStorageService {
    suspend fun uploadImageToDatabase(
        dogUid: String,
        storageFileName: String,
        imageByteArray: ByteArray
    ): String
}

class StorageService : IStorageService {

    private val storage by lazy { FirebaseStorage.getInstance() }
    private val dogImages = "dogImages" // todo move this somewhere useful
    private val userImages = "userImages" // todo move this somewhere useful

    override suspend fun uploadImageToDatabase(
        uid: String,
        storageFileName: String,
        imageByteArray: ByteArray
    ): String {

        var imageUrl : String = ""
        val randomUUID = StringUtils.getRandomUID()

        val uploadImageTask =
            storage.reference.child(storageFileName).child(uid)
                .child(randomUUID).putBytes(imageByteArray)
                .await()

        uploadImageTask.storage.downloadUrl.addOnSuccessListener {
            imageUrl = it.toString()
        }.await()

        return imageUrl
    }
}