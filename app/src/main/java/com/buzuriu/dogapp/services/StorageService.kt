package com.buzuriu.dogapp.services

import com.buzuriu.dogapp.utils.StringUtils
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

        var dogImageUrl : String = ""
        val randomUUID = StringUtils.getRandomUID()

        val uploadImageTask =
            storage.reference.child(dogImages).child(dogUid)
                .child(randomUUID).putBytes(imageByteArray)
                .await()

        uploadImageTask.storage.downloadUrl.addOnSuccessListener {
            dogImageUrl = it.toString()
        }.await()

        return dogImageUrl
    }
}