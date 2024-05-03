package com.buzuriu.dogapp.viewModels

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.result.ActivityResult
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.buzuriu.dogapp.R
import com.buzuriu.dogapp.listeners.IGetActivityForResultListener
import com.buzuriu.dogapp.listeners.IOnCompleteListener
import com.buzuriu.dogapp.models.AlertDialogTextObj
import com.buzuriu.dogapp.models.MeetingObj
import com.buzuriu.dogapp.models.UserObj
import com.buzuriu.dogapp.utils.ImageUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class EditAccountViewModel : BaseViewModel() {

    var userBitmapImage = MutableLiveData<Bitmap>()
    var user = MutableLiveData<UserObj>()
    var userImageUrl = MutableLiveData<String>()
    var isFemaleGenderSelected = MutableLiveData<Boolean>()
    var currentGenderString: String? = null
    private val userImages = "userImages" // todo move this somewhere useful


    init {
        user.value = exchangeInfoService.get<UserObj>(this::class.java.name)
        checkUserGender(user.value!!.gender!!)

        if (user.value?.imageUrl != null)
            userImageUrl.value = user.value?.imageUrl
    }

    private fun checkUserGender(gender: String) {
        if (gender == "female")
            isFemaleGenderSelected.value = true
        else if (gender == "male")
            isFemaleGenderSelected.value = false
    }

    fun editAccount() {
        currentGenderString = if (isFemaleGenderSelected.value!!) {
            "female"
        } else
            "male"

        val user = UserObj(
            currentUser!!.uid,
            user.value!!.email,
            user.value!!.name,
            user.value!!.phone,
            currentGenderString,
        )

        viewModelScope.launch(Dispatchers.IO) {
            if (userBitmapImage.value != null) {
                val compressedImage = ImageUtils.getCompressedImage(userBitmapImage.value!!)
                user.imageUrl =
                    storageService.uploadImageToDatabase(currentUser!!.uid, userImages, compressedImage)
            }

            viewModelScope.launch(Dispatchers.IO)
            {
                databaseService.storeUser(currentUser!!.uid, user, object : IOnCompleteListener {
                    override fun onComplete(successful: Boolean, exception: Exception?) {

                        if (successful) {
                            viewModelScope.launch(Dispatchers.Main) {
                                snackMessageService.displaySnackBar("Edited successful")
                                exchangeInfoService.put(
                                    AccountDetailViewModel::class.java.name,
                                    user
                                )
                                delay(2000)
                                changeMeetingInfoRelatedToThisUser()
                                navigationService.closeCurrentActivity()
                            }
                        } else {
                            viewModelScope.launch(Dispatchers.Main) {
                                if (!exception?.message.isNullOrEmpty())
                                    snackMessageService.displaySnackBar(exception!!.message!!)
                                else snackMessageService.displaySnackBar(R.string.unknown_error)
                                delay(2000)
                            }
                        }

                        showLoadingView(false)
                    }
                })
            }
        }
    }

    fun changeMeetingInfoRelatedToThisUser() {
        var meetings = ArrayList<MeetingObj>()
        viewModelScope.launch(Dispatchers.IO) {
            meetings = databaseService.fetchUserMeetings(currentUser!!.uid, object : IOnCompleteListener{
                override fun onComplete(successful: Boolean, exception: java.lang.Exception?) {

                }
            })!!


            viewModelScope.launch(Dispatchers.Main) {
                for (meeting in meetings) {
                    meeting.userGender = currentGenderString

                    databaseService.storeMeeting(
                        meeting.uid!!,
                        meeting,
                        object : IOnCompleteListener {
                            override fun onComplete(successful: Boolean, exception: Exception?) {

                                if (successful) {
                                    viewModelScope.launch(Dispatchers.Main) {
                                        snackMessageService.displaySnackBar("All meetings have been updated with the new info")
                                        delay(2000)
                                    }
                                } else {
                                    viewModelScope.launch(Dispatchers.Main) {
                                        if (!exception?.message.isNullOrEmpty())
                                            snackMessageService.displaySnackBar(exception!!.message!!)
                                        else snackMessageService.displaySnackBar(R.string.unknown_error)
                                        delay(2000)
                                    }
                                }
                            }
                        })
                }
            }
        }
    }

    fun takePictureClicked() {
        val waysToUpload = mapOf(
            "Camera" to ::shootPictureUsingCamera,
            "Gallery" to ::choosePictureUsingGallery,
            "Cancel" to ::exit
        )
        val options = arrayOf("Camera", "Gallery", "Cancel")
        val alertDialogTextObj = AlertDialogTextObj(options, waysToUpload)
        alertMessageService.displayAlertDialog(alertDialogTextObj)
    }

    private fun shootPictureUsingCamera() {
        viewModelScope.launch(Dispatchers.Main) {

            val hasPermission = requestPermissionKind(listOf(Manifest.permission.CAMERA)).await()
            if (!hasPermission) {
                snackMessageService.displaySnackBar(R.string.err_camera_permission_needed)
                return@launch
            }

            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            viewModelScope.launch(Dispatchers.Main) {

                activityForResultService.launchCurrentActivityResultLauncher(
                    cameraIntent,
                    object : IGetActivityForResultListener {
                        override fun activityForResult(activityResult: ActivityResult) {
                            if (activityResult.resultCode == Activity.RESULT_OK) {
                                val imageBitmap = activityResult.data?.extras?.get("data") as Bitmap
                                userBitmapImage.value = imageBitmap
                            }
                        }
                    })
            }
        }
    }

    private fun choosePictureUsingGallery() {
        viewModelScope.launch(Dispatchers.Main) {

            val hasReadExternalPermission = requestPermissionKind(
                listOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            ).await()
            if (!hasReadExternalPermission) {
                snackMessageService.displaySnackBar("Error permission")
                return@launch
            }
            val intent =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            viewModelScope.launch(Dispatchers.Main) {

                activityForResultService.launchCurrentActivityResultLauncher(
                    intent,
                    object : IGetActivityForResultListener {
                        @SuppressLint("NewApi")
                        override fun activityForResult(activityResult: ActivityResult) {

                            if (activityResult.resultCode == Activity.RESULT_OK) {
                                val imageUri = activityResult.data?.data as Uri
                                userBitmapImage.value =
                                    ImageUtils.convertToBitmap(activityService.activity!!, imageUri)
                            }
                        }
                    })
            }
        }
    }

    private fun exit() {
        navigationService.closeCurrentActivity()
    }
}