package com.buzuriu.dogapp.viewModels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.buzuriu.dogapp.adapters.DogJoinMeetAdapter
import com.buzuriu.dogapp.listeners.IOnCompleteListener
import com.buzuriu.dogapp.models.DogObj
import com.buzuriu.dogapp.models.MyCustomMeetingObj
import com.buzuriu.dogapp.models.ParticipantObj
import com.buzuriu.dogapp.utils.StringUtils
import com.buzuriu.dogapp.views.main.ui.map.MapViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SelectDogForJoinMeetViewModel : BaseViewModel() {

    var dogJoinMeetAdapter: DogJoinMeetAdapter? = null
    private var dogsList = ArrayList<DogObj>()
    private var selectedDog = MutableLiveData<DogObj>()
    private var attendedMeeting: MyCustomMeetingObj? = null
    private var participantId = MutableLiveData<String>()

    init {
        attendedMeeting = dataExchangeService.get<MyCustomMeetingObj>(this::class.java.name)

        initDogs()

        if (doesUserHaveOnlyOneDog())
            localDatabaseService.get<java.util.ArrayList<DogObj>>("localDogsList")?.get(0)
                ?.let { selectDog(it) }

        viewModelScope.launch {
            participantId.value = getUserParticipantId()
        }
    }


    fun saveDog() {
        if (selectedDog.value == null) {
            dialogService.showSnackbar("Please select a dog to attend this meeting")
            return
        } else if (selectedDog.value != null && participantId.value != null) {
            // the user is already participating to this meeting, only with a different dog
            viewModelScope.launch(Dispatchers.IO)
            {
                databaseService.updateParticipantDog(attendedMeeting!!.meetingObj!!.uid!!,
                    participantId.value!!,
                    selectedDog.value!!.uid,
                    object : IOnCompleteListener {
                        override fun onComplete(successful: Boolean, exception: Exception?) {
                            dialogService.showSnackbar("Changed dog successfully")
                        }
                    })
            }
        } else {
            // the meeting is not joined yet, so we create a new ParticipantObj and add it in database
            val participantObjUid = StringUtils.getRandomUID()
            viewModelScope.launch(Dispatchers.IO)
            {
                databaseService.joinMeeting(attendedMeeting!!.meetingObj!!.uid!!,
                    participantObjUid,
                    ParticipantObj(
                        participantObjUid,
                        currentUser!!.uid,
                        selectedDog.value!!.uid
                    ),
                    object : IOnCompleteListener {
                        override fun onComplete(successful: Boolean, exception: Exception?) {
                            dialogService.showSnackbar("Success")
                        }
                    })
            }
        }
        dataExchangeService.put(MeetingDetailViewModel::class.java.name, selectedDog.value!!)
        dataExchangeService.put(MapViewModel::class.java.name, attendedMeeting!!)
        close()
    }

    fun selectDog(dogObj: DogObj) {
        unselectPreviousDog()
        dogObj.isSelected = true
        selectedDog.value = dogObj

        dogJoinMeetAdapter?.notifyItemChanged(dogJoinMeetAdapter?.dogsList!!.indexOf(dogObj))
    }

    fun close() {
        unselectPreviousDog()
        navigationService.closeCurrentActivity()
    }

    private fun doesUserHaveOnlyOneDog(): Boolean {
        if (localDatabaseService.get<java.util.ArrayList<DogObj>>("localDogsList")!!.size == 1)
            return true
        return false
    }

    private fun initDogs() {
        dogsList = localDatabaseService.get<ArrayList<DogObj>>("localDogsList")!!
        dogJoinMeetAdapter = DogJoinMeetAdapter(dogsList, this)
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

    private suspend fun getUserParticipantId(): String? {
        val participantUid: String?

        val participants: ArrayList<ParticipantObj> =
            databaseService.fetchAllMeetingParticipants(attendedMeeting!!.meetingObj!!.uid!!)!!
        for (participant in participants) {
            if (participant.userUid == currentUser!!.uid) {
                Log.d("andreea1", "$participant.uid")
                participantUid = participant.uid
                return participantUid
            }
        }
        return null
    }
}