package com.buzuriu.dogapp.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.buzuriu.dogapp.adapters.DogJoinMeetAdapter
import com.buzuriu.dogapp.listeners.IOnCompleteListener
import com.buzuriu.dogapp.models.DogObj
import com.buzuriu.dogapp.models.MyCustomMeetingObj
import com.buzuriu.dogapp.models.ParticipantObj
import com.buzuriu.dogapp.utils.LocalDBItems
import com.buzuriu.dogapp.utils.StringUtils
import com.buzuriu.dogapp.views.main.ui.map.MapViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SelectDogForJoinMeetViewModel : BaseViewModel() {

    private var dogsList = ArrayList<DogObj>()
    private var selectedDog = MutableLiveData<DogObj>()
    private var attendedMeeting: MyCustomMeetingObj? = null
    private var participantId = MutableLiveData<String>()

    var dogJoinMeetAdapter: DogJoinMeetAdapter? = null

    init {
        attendedMeeting = exchangeInfoService.get<MyCustomMeetingObj>(this::class.java.name)

        initDogs()
        automaticallySelectDogIfOnlyOne()
        getUserId()
    }

    private fun automaticallySelectDogIfOnlyOne()
    {
        if (doesUserHaveOnlyOneDog())
            localDatabaseService.get<java.util.ArrayList<DogObj>>(LocalDBItems.localDogsList)?.get(0)
                ?.let { selectDog(it) }
    }

    private fun getUserId()
    {
        viewModelScope.launch {
            participantId.value = getUserParticipantId()
        }
    }

    private fun doesUserHaveOnlyOneDog(): Boolean {
        if (localDatabaseService.get<java.util.ArrayList<DogObj>>(LocalDBItems.localDogsList)!!.size == 1)
            return true
        return false
    }

    private fun initDogs() {
        dogsList = localDatabaseService.get<ArrayList<DogObj>>(LocalDBItems.localDogsList)!!
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
                participantUid = participant.uid
                return participantUid
            }
        }
        return null
    }

    fun addMeetToUserJoinedMeetings(meetingObj: MyCustomMeetingObj) {
        val myMeetingsList =
            localDatabaseService.get<java.util.ArrayList<MyCustomMeetingObj>>(LocalDBItems.meetingsUserJoined)
                ?: return
        myMeetingsList.add(meetingObj)
        localDatabaseService.add(LocalDBItems.meetingsUserJoined, myMeetingsList)
    }

    fun selectDog(dogObj: DogObj) {
        unselectPreviousDog()
        dogObj.isSelected = true
        selectedDog.value = dogObj

        dogJoinMeetAdapter?.notifyItemChanged(dogJoinMeetAdapter?.dogsList!!.indexOf(dogObj))
    }

    fun saveDog() {
        if (selectedDog.value == null) {
            snackMessageService.displaySnackBar("Please select a dog to attend this meeting")
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
                            snackMessageService.displaySnackBar("Changed dog successfully")
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
                            addMeetToUserJoinedMeetings(attendedMeeting!!)
                            snackMessageService.displaySnackBar("Success")
                        }
                    })
            }
        }
        exchangeInfoService.put(MeetingDetailViewModel::class.java.name, selectedDog.value!!)
        exchangeInfoService.put(MapViewModel::class.java.name, attendedMeeting!!)
        close()
    }

    fun close() {
        unselectPreviousDog()
        navigationService.closeCurrentActivity()
    }
}