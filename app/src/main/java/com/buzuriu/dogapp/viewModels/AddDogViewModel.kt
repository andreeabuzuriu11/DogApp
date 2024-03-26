package com.buzuriu.dogapp.viewModels

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.activity.result.ActivityResult
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.buzuriu.dogapp.R
import com.buzuriu.dogapp.listeners.IGetActivityForResultListener
import com.buzuriu.dogapp.listeners.IOnCompleteListener
import com.buzuriu.dogapp.models.*
import com.buzuriu.dogapp.utils.ImageUtils
import com.buzuriu.dogapp.utils.LocalDBItems
import com.buzuriu.dogapp.utils.StringUtils
import com.buzuriu.dogapp.views.SelectBreedFragment
import com.buzuriu.dogapp.views.main.ui.OverlayActivity
import com.buzuriu.dogapp.views.main.ui.my_dogs.MyDogsViewModel
import com.buzuriu.dogapp.views.main.ui.my_meetings.MyMeetingsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlin.system.exitProcess

class AddDogViewModel : BaseViewModel() {

    var dogBitmapImage = MutableLiveData<Bitmap>()
    var name = MutableLiveData("")
    var breed = MutableLiveData("")
    var ageValue = MutableLiveData("")
    var ageString = MutableLiveData("")
    var buttonText = MutableLiveData("add")
    var dogImageUrl = MutableLiveData<String>()
    var isEdit: Boolean = false
    var isFemaleGenderSelected = MutableLiveData<Boolean>()

    private var currentDogUid: String? = ""
    private var currentGenderString: String? = null

    init {
        val value = exchangeInfoService.get<Any>(this::class.qualifiedName!!)
        if (value is BreedObj) {
            breed.value = value.breedName
        }

        var dog: DogObj? = null
        if (value is DogObj) {
            dog = value
        }

        if (dog != null) {
            isEdit = true
            name.value = dog.name
            ageValue.value = dog.ageValue
            ageString.value = dog.ageString
            dogImageUrl.value = dog.imageUrl
            breed.value = dog.breed
            isFemaleGenderSelected.value = dog.gender == "female"
            buttonText.value = "Edit"
            currentDogUid = dog.uid
        }
    }

    override fun onResume() {
        val selectedBreed = exchangeInfoService.get<BreedObj>(this::class.qualifiedName!!)
        if (selectedBreed != null) {
            breed.value = selectedBreed.breedName
        }
    }

    fun takePictureClicked() {
        val waysToUpload = mapOf(
            "Camera" to ::shootPictureUsingCamera,
            "Gallery" to ::choosePictureUsingGallery,
            "Cancel" to ::exit
        )
        val options = arrayOf<String>("Camera", "Gallery", "Cancel")
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
                                dogBitmapImage.value = imageBitmap
                            }
                        }
                    })
            }
        }
    }

    private fun choosePictureUsingGallery() {
        viewModelScope.launch(Dispatchers.Main) {

            val hasReadExternalPermission = requestPermissionKind(listOf(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE)).await()
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
                                dogBitmapImage.value =
                                    ImageUtils.convertToBitmap(activityService.activity!!, imageUri)
                            }
                        }
                    })
            }
        }
    }

    private fun exit()
    {
        navigationService.closeCurrentActivity()
    }

    fun addDog() {
        if (!areFieldsCompleted()) return

        currentGenderString = if (isFemaleGenderSelected.value!!) {
            "female"
        } else
            "male"

        var uid = StringUtils.getRandomUID()
        if (!currentDogUid.isNullOrEmpty()) uid = currentDogUid as String

        val dog = DogObj(
            uid,
            name.value!!,
            ageValue.value!!,
            ageString.value!!,
            breed.value!!,
            currentGenderString!!,
            currentUser!!.uid
        )

        val currentUserUid = currentUser?.uid
        if (currentUserUid.isNullOrEmpty()) return

        if (!dogImageUrl.value.isNullOrEmpty()) {
            dog.imageUrl = dogImageUrl.value!!
        }

        showLoadingView(true)

        viewModelScope.launch(Dispatchers.IO) {
            if (dogBitmapImage.value != null) {
                val compressedImage = ImageUtils.getCompressedImage(dogBitmapImage.value!!)
                dog.imageUrl = storageService.uploadImageToDatabase(uid, compressedImage)
            }

            viewModelScope.launch(Dispatchers.IO)
            {
                databaseService.storeDogUidToUser(
                    currentUserUid,
                    dog.uid,
                    object : IOnCompleteListener {
                        override fun onComplete(
                            successful: Boolean,
                            exception: java.lang.Exception?
                        ) {
                            if (successful) {
                                viewModelScope.launch(Dispatchers.IO) {
                                    databaseService.storeDog(
                                        currentUserUid,
                                        dog,
                                        object : IOnCompleteListener {
                                            override fun onComplete(
                                                successful: Boolean,
                                                exception: Exception?
                                            ) {

                                                if (successful) {
                                                    viewModelScope.launch(Dispatchers.Main) {
                                                        exchangeInfoService.put(
                                                            MyDogsViewModel::class.java.name,
                                                            true
                                                        )
                                                        Log.d("DEBUG", "Dog ${dog.name} successfully added")
                                                        addOrEditDogToData(dog)
                                                        if (!isEdit) {
                                                            snackMessageService.displaySnackBar(R.string.added_success_message)
                                                        } else {
                                                            val meetingsToChange =
                                                                changeMeetingInfoRelatedToThisDog(
                                                                    dog.uid
                                                                )
                                                            delay(2000)
                                                            for (meeting in meetingsToChange)
                                                                editMyMeeting(
                                                                    MyCustomMeetingObj(
                                                                        meeting,
                                                                        getCurrentUser(),
                                                                        dog
                                                                    )
                                                                )
                                                            snackMessageService.displaySnackBar(R.string.edited_success_message)
                                                        }

                                                        delay(2000)
                                                        navigationService.closeCurrentActivity()
                                                    }
                                                } else {
                                                    viewModelScope.launch(Dispatchers.Main) {
                                                        if (!exception?.message.isNullOrEmpty())
                                                            snackMessageService.displaySnackBar(
                                                                exception!!.message!!
                                                            )
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
                    })
            }
        }
    }

    fun getCurrentUser(): UserObj {
        return localDatabaseService.get<UserObj>(LocalDBItems.currentUser)!!
    }

    fun addOrEditDogToData(dog: DogObj) {
        val dogsList: ArrayList<DogObj> =
            localDatabaseService.get<ArrayList<DogObj>>(LocalDBItems.localDogsList) ?: return
        if (dogsList.any { it.uid == dog.uid }) {
            val oldDog = dogsList.find { it.uid == dog.uid }
            dogsList.remove(oldDog)
            exchangeInfoService.put(DogDetailViewModel::class.java.name, dog)
            exchangeInfoService.put(
                MyDogsViewModel::class.java.name,
                true
            ) // to know the list must be refreshed
        }
        dogsList.add(dog)
        localDatabaseService.add(LocalDBItems.localDogsList, dogsList)
    }

    fun editMyMeeting(meeting: MyCustomMeetingObj) {
        val meetingsList: ArrayList<MyCustomMeetingObj> =
            localDatabaseService.get<ArrayList<MyCustomMeetingObj>>(LocalDBItems.localMeetingsList) ?: return
        if (meetingsList.any { it.meetingObj!!.uid == meeting.meetingObj!!.uid }) {
            val oldMeeting = meetingsList.find { it.meetingObj!!.uid == meeting.meetingObj!!.uid }
            meetingsList.remove(oldMeeting)
            exchangeInfoService.put(
                MyMeetingsViewModel::class.java.name,
                true
            ) // to know the list must be refreshed
        }

        meetingsList.add(meeting)
        localDatabaseService.add(LocalDBItems.localMeetingsList, meetingsList)
    }

    fun changeMeetingInfoRelatedToThisDog(dogUid: String): ArrayList<MeetingObj> {
        var meetings: ArrayList<MeetingObj>
        val allMeetings = ArrayList<MeetingObj>()
        viewModelScope.launch(Dispatchers.IO) {
            meetings = databaseService.fetchDogMeetings(dogUid)!!

            viewModelScope.launch(Dispatchers.Main) {
                for (meeting in meetings) {
                    allMeetings.add(meeting)
                    meeting.dogGender = currentGenderString
                    meeting.dogBreed = breed.value

                    databaseService.storeMeeting(
                        meeting.uid!!,
                        meeting,
                        object : IOnCompleteListener {
                            override fun onComplete(successful: Boolean, exception: Exception?) {

                                if (successful) {
                                    viewModelScope.launch(Dispatchers.Main) {
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

        return allMeetings
    }

    fun selectBreed() {
        navigationService.showOverlay(
            OverlayActivity::class.java,
            false,
            LocalDBItems.fragmentName,
            SelectBreedFragment::class.qualifiedName
        )
        if (!breed.value.isNullOrEmpty())
            exchangeInfoService.put(
                SelectBreedViewModel::class.qualifiedName!!,
                breed.value.toString()
            )
    }

    fun predictBreed(){
        // todo add prediction logic
    }

    private fun areFieldsCompleted(): Boolean {
        if (name.value.isNullOrEmpty()) {
            snackMessageService.displaySnackBar("Please add a name")
            return false
        }
        if (breed.value.isNullOrEmpty()) {
            snackMessageService.displaySnackBar("Please add a breed")
            return false
        }
        if (ageValue.value.isNullOrEmpty()) {
            snackMessageService.displaySnackBar("Please add an age")
            return false
        }

        return true
    }

}
