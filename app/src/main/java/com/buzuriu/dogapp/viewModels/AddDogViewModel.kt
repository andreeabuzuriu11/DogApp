package com.buzuriu.dogapp.viewModels

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.widget.RadioGroup
import androidx.activity.result.ActivityResult
import androidx.compose.ui.input.key.Key.Companion.D
import androidx.constraintlayout.helper.widget.MotionPlaceholder
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.buzuriu.dogapp.R
import com.buzuriu.dogapp.enums.AgeEnum
import com.buzuriu.dogapp.enums.GenderEnum
import com.buzuriu.dogapp.listeners.IGetActivityForResultListener
import com.buzuriu.dogapp.listeners.IOnCompleteListener
import com.buzuriu.dogapp.models.AlertBuilderSettings
import com.buzuriu.dogapp.models.BreedObj
import com.buzuriu.dogapp.models.DogObj
import com.buzuriu.dogapp.utils.ImageUtils
import com.buzuriu.dogapp.utils.StringUtils
import com.buzuriu.dogapp.views.SelectBreedFragment
import com.buzuriu.dogapp.views.main.ui.OverlayActivity
import com.buzuriu.dogapp.views.main.ui.dashboard.DashboardViewModel
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.suspendCoroutine

class AddDogViewModel : BaseViewModel() {

    var spinnerEntries = listOf(AgeEnum.MONTHS.toString(), AgeEnum.YEARS.toString())

    var dogBitmapImage = MutableLiveData<Bitmap>()
    var name = MutableLiveData("")
    var breed = MutableLiveData("")
    var ageValue = MutableLiveData("")
    var ageString = MutableLiveData("")
    var currentGender: GenderEnum? = null
    var currentGenderString:String? = null

    var dogPlaceHolder : MutableLiveData<Drawable>
    var dogImageUrl = MutableLiveData<String>()

    init{
        dogPlaceHolder = MutableLiveData<Drawable>(getDogPlaceHolder())
    }

    fun takePicture() {

        val methodsMap = HashMap<String, () -> Unit>()
        val itemsName = arrayOf<CharSequence>("Camera", "Gallery", "Cancel")
        methodsMap["Camera"] = ::takeImage
        methodsMap["Gallery"] = ::uploadPictureFromGallery
        methodsMap["Cancel"] = {}

        val alertBuilderSettings = AlertBuilderSettings(itemsName, methodsMap)
        alertBuilderService.showAlertDialog(alertBuilderSettings)
    }

    private fun takeImage()
    {
        viewModelScope.launch(Dispatchers.Main) {

            val hasPermission = askCameraPermission().await()
            if (!hasPermission) {
                dialogService.showSnackbar(R.string.err_camera_permission_needed)
                return@launch
            }

            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            viewModelScope.launch(Dispatchers.Main) {

                activityResultService.launchCurrentActivityResultLauncher(
                    cameraIntent,
                    object : IGetActivityForResultListener {
                        override fun activityForResult(activityResult: ActivityResult) {
                            if (activityResult.resultCode == Activity.RESULT_OK)
                            {
                                val imageBitmap = activityResult.data?.extras?.get("data") as Bitmap
                                dogBitmapImage.value = imageBitmap;
                            }
                        }
                    })
            }
        }
    }

    private fun uploadPictureFromGallery()
    {
        viewModelScope.launch(Dispatchers.Main) {

            val hasReadExternalPermission = askReadExternalPermission().await()
            if (!hasReadExternalPermission) {
                dialogService.showSnackbar("Error permission")
                return@launch
            }
            val intent =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            viewModelScope.launch(Dispatchers.Main) {

                activityResultService.launchCurrentActivityResultLauncher(
                    intent,
                    object : IGetActivityForResultListener {
                        @SuppressLint("NewApi")
                        override fun activityForResult(activityResult: ActivityResult) {
                            if (activityResult.resultCode == Activity.RESULT_OK)
                            {
                                val imageUri = activityResult.data?.data as Uri
                                dogBitmapImage.value = ImageUtils.getBitmap(activityService.activity!!,imageUri)
                            }
                        }
                    })
            }
        }
    }

    override fun onResume()
    {
        val selectedBreed = dataExchangeService.get<BreedObj>(this::class.qualifiedName!!)
        if (selectedBreed!=null)
        {
            breed.value = selectedBreed.breedName
        }

    }

    fun setGenderType(gender : GenderEnum)
    {
        if(gender == GenderEnum.MALE)
            currentGenderString = "male"
        else
            currentGenderString = "female"
    }

    fun addDog()
    {
        if(!areFieldsCompleted()) return

        val uid = StringUtils.getRandomUID()
        val dog = DogObj(
            uid,
            name.value!!,
            ageValue.value!!,
            ageString.value!!,
            breed.value!!,
            currentGenderString!!)

        val currentUserUid = currentUser?.uid
        if(currentUserUid.isNullOrEmpty()) return

        ShowLoadingView(true)

        viewModelScope.launch(Dispatchers.IO) {
            if(dogBitmapImage.value!=null)
            {
                val compressedImage = ImageUtils.getCompressedImage(dogBitmapImage.value!!)
                dog.imageUrl = storageService.uploadImageToDatabase(uid, compressedImage)
            }

            databaseService.storeDogInfo(currentUserUid, dog, object : IOnCompleteListener {
                override fun onComplete(successful: Boolean, exception: Exception?) {

                    if (successful) {
                        viewModelScope.launch(Dispatchers.Main) {
                            dataExchangeService.put(DashboardViewModel::class.java.name, true)
                            dialogService.showSnackbar(R.string.added_success_message)
                            delay(2000)
                            navigationService.closeCurrentActivity()

                        }
                    } else
                    {
                        viewModelScope.launch(Dispatchers.Main) {
                            if (!exception?.message.isNullOrEmpty())
                                dialogService.showSnackbar(exception!!.message!!)
                            else dialogService.showSnackbar(R.string.unknown_error)
                            delay(2000)
                        }
                    }

                    ShowLoadingView(false)
                }
            })
        }
    }

    fun selectBreed()
    {
        navigationService.showOverlay(
            OverlayActivity::class.java,
            false,
            OverlayActivity.fragmentClassNameParam,
            SelectBreedFragment::class.qualifiedName
        )
    }

    private fun areFieldsCompleted() :Boolean {
        if(name.value.isNullOrEmpty()) {
            dialogService.showSnackbar("Please add a name")
            return false
        }
        if(breed.value.isNullOrEmpty()) {
            dialogService.showSnackbar("Please add a breed")
            return false
        }
        if(ageValue.value.isNullOrEmpty()) {
            dialogService.showSnackbar("Please add an age")
            return false
        }
        if(currentGenderString.isNullOrEmpty()) {
            dialogService.showSnackbar("Please select a gender")
            return false
        }

        return true
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun getDogPlaceHolder(): Drawable? {
        return activityService.activity!!.getDrawable(ImageUtils.getDogPlaceholder())
    }
}
