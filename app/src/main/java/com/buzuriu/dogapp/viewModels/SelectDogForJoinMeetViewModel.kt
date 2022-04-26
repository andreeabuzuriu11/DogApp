package com.buzuriu.dogapp.viewModels

import androidx.lifecycle.viewModelScope
import com.buzuriu.dogapp.adapters.DogJoinMeetAdapter
import com.buzuriu.dogapp.listeners.IOnCompleteListener
import com.buzuriu.dogapp.models.DogObj
import com.buzuriu.dogapp.models.MyCustomMeetingObj
import com.buzuriu.dogapp.models.ParticipantObj
import com.buzuriu.dogapp.utils.StringUtils
import com.buzuriu.dogapp.views.main.ui.map.MapViewModel
import com.google.android.gms.maps.MapView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SelectDogForJoinMeetViewModel : BaseViewModel() {

    var dogJoinMeetAdapter: DogJoinMeetAdapter? = null
    private var dogsList = ArrayList<DogObj>()
    private var selectedDog: DogObj? = null
    private var attendedMeeting: MyCustomMeetingObj? = null

    init {
        attendedMeeting = dataExchangeService.get<MyCustomMeetingObj>(this::class.java.name)

        initDogs()
    }

    private fun initDogs() {
        dogsList = localDatabaseService.get<ArrayList<DogObj>>("localDogsList")!!
        dogJoinMeetAdapter = DogJoinMeetAdapter(dogsList, this)
    }

    fun saveDog() {
        if (selectedDog == null) {
            dialogService.showSnackbar("Please select a dog to attend this meeting")
            return
        }
        viewModelScope.launch(Dispatchers.IO)
        {
            val participantObjUid = StringUtils.getRandomUID()
            databaseService.joinMeeting(attendedMeeting!!.meetingObj!!.uid!!,
                participantObjUid,
                ParticipantObj(
                    participantObjUid,
                    currentUser!!.uid,
                    selectedDog!!.uid
                ),
                object : IOnCompleteListener {
                    override fun onComplete(successful: Boolean, exception: Exception?) {
                        dialogService.showSnackbar("Success")
                    }
                })
        }

        dataExchangeService.put(MapViewModel::class.java.name, attendedMeeting!!)

        close()
    }




    fun selectDog(dogObj: DogObj) {
        unselectPreviousDog()
        dogObj.isSelected = true
        selectedDog = dogObj

        dogJoinMeetAdapter?.notifyItemChanged(dogJoinMeetAdapter?.dogsList!!.indexOf(dogObj))
    }

    private fun unselectPreviousDog() {
        for (dog in dogsList) {
            if (dog.isSelected!!) {
                dog.isSelected = false
                dogJoinMeetAdapter?.notifyItemChanged(dogJoinMeetAdapter?.dogsList!!.indexOf(dog))
                return
            }
        }
    }

    fun close() {
        navigationService.closeCurrentActivity()
    }

}