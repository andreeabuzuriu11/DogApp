package com.buzuriu.dogapp.services

import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.util.*

interface IStorageService {
    suspend fun uploadImageToDatabase(
        dogUid: String,
        imageByteArray: ByteArray
    ): String
}

class StorageService : IStorageService {

    private val storage by lazy { FirebaseStorage.getInstance() }
    private val dogImages = "dogImages"

    override suspend fun uploadImageToDatabase(
        dogUid: String,
        imageByteArray: ByteArray
    ): String {

        val randomKey = UUID.randomUUID().toString()
        var imageUrl = ""

        val uploadTask =
            storage.reference.child(dogImages).child(dogUid)
                .child(randomKey).putBytes(imageByteArray)
                .await()

        uploadTask.storage.downloadUrl.addOnSuccessListener {
            imageUrl = it.toString()
        }.await()

        return imageUrl
    }
}