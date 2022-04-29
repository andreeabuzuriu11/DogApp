package com.buzuriu.dogapp.viewModels

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.buzuriu.dogapp.R
import com.buzuriu.dogapp.listeners.IClickListener
import com.buzuriu.dogapp.listeners.IOnCompleteListener
import com.buzuriu.dogapp.models.DogObj
import com.buzuriu.dogapp.models.MeetingObj
import com.buzuriu.dogapp.models.MyCustomMeetingObj
import com.buzuriu.dogapp.models.ParticipantObj
import com.buzuriu.dogapp.views.AddDogActivity
import com.buzuriu.dogapp.views.main.ui.my_dogs.MyDogsViewModel
import com.buzuriu.dogapp.views.main.ui.my_meetings.MyMeetingsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class DogDetailViewModel : BaseViewModel() {

    var dog = MutableLiveData<DogObj>()

    init {
        dog.value = dataExchangeService.get<DogObj>(this::class.java.name)!!
    }

    override fun onResume() {
        super.onResume()
        val editedDog = dataExchangeService.get<DogObj>(this::class.java.name)
        if (editedDog != null) {
            dog.value = editedDog!!
        }
    }

    fun editDog() {
        dataExchangeService.put(AddDogViewModel::class.java.name, dog.value!!)
        navigationService.navigateToActivity(AddDogActivity::class.java)
    }

    fun deleteDog() {
        dialogService.showAlertDialog(
            "Delete dog?",
            "Are you sure you want to delete ${dog.value!!.name}? This action cannot be undone.",
            "Yes, delete it",
            object :
                IClickListener {
                override fun clicked() {
                    deleteDogFromDatabase()
                    deleteDogRelatedToUserFromDatabase()
                    dataExchangeService.put(
                        MyDogsViewModel::class.java.name,
                        true
                    ) // is refresh list needed
                }
            })
    }

    fun deleteDogFromDatabase() {
        viewModelScope.launch(Dispatchers.IO) {
            databaseService.deleteDog(dog.value!!.uid, object :
                IOnCompleteListener {
                @RequiresApi(Build.VERSION_CODES.N)
                override fun onComplete(successful: Boolean, exception: Exception?) {
                    val allDogsList = localDatabaseService.get<ArrayList<DogObj>>("localDogsList")
                    allDogsList!!.remove(dog.value!!)

                    localDatabaseService.add("localDogsList", allDogsList)
                    deleteMeetingRelatedToDogFromDatabase(dog.value!!.uid)
                    deleteMeetingRelatedToDogFromLocalDatabase()
                    deleteParticipantObjWhereThisDogWasAttending()
                    navigationService.closeCurrentActivity()
                }
            })
        }
    }

    fun deleteDogRelatedToUserFromDatabase() {
        viewModelScope.launch(Dispatchers.IO) {
            databaseService.deleteDogRelatedToUser(currentUser!!.uid, dog.value!!.uid, object : IOnCompleteListener {
                @RequiresApi(Build.VERSION_CODES.N)
                override fun onComplete(successful: Boolean, exception: Exception?) {
                    Log.d("andreea", "dog related to user was deleted")
                }
            })
        }
    }

    fun deleteParticipantObjWhereThisDogWasAttending()
    {
        var meetings: ArrayList<MeetingObj>
        var participants: ArrayList<ParticipantObj>

        viewModelScope.launch(Dispatchers.IO) {
            meetings = databaseService.fetchDogMeetings(dog.value!!.uid)!!

            viewModelScope.launch(Dispatchers.Main) {
                for (meeting in meetings) {
                    participants = databaseService.fetchAllMeetingParticipants(meeting.uid!!)!!

                    for(participant in participants)
                    {
                        if (participant.dogUid == dog.value!!.uid)
                        {
                            // this participant should be deleted from database
                            databaseService.deleteParticipant(meeting.uid!!, participant.uid!!, object : IOnCompleteListener {
                                override fun onComplete(
                                    successful: Boolean,
                                    exception: java.lang.Exception?
                                ) {
                                    if (successful) {
                                        viewModelScope.launch(Dispatchers.Main) {
                                            dialogService.showSnackbar("This dog is deleted as participant to all meetings he was attending")
                                            delay(2000)
                                        }
                                    } else {
                                        viewModelScope.launch(Dispatchers.Main) {
                                            if (!exception?.message.isNullOrEmpty())
                                                dialogService.showSnackbar(exception!!.message!!)
                                            else dialogService.showSnackbar(R.string.unknown_error)
                                            delay(2000)
                                        }
                                    }

                                }


                            })
                        }
                    }

                }
            }
        }

    }

    fun deleteMeetingRelatedToDogFromDatabase(dogUid: String) {
        var meetings: ArrayList<MeetingObj>

        viewModelScope.launch(Dispatchers.IO) {
            meetings = databaseService.fetchDogMeetings(dogUid)!!

            viewModelScope.launch(Dispatchers.Main) {
                for (meeting in meetings) {

                    databaseService.deleteMeeting(
                        meeting.uid!!,
                        object : IOnCompleteListener {
                            override fun onComplete(successful: Boolean, exception: Exception?) {
                                if (successful) {
                                    viewModelScope.launch(Dispatchers.Main) {
                                        dialogService.showSnackbar("All meetings with this dog have been deleted")
                                        delay(2000)
                                    }
                                } else {
                                    viewModelScope.launch(Dispatchers.Main) {
                                        if (!exception?.message.isNullOrEmpty())
                                            dialogService.showSnackbar(exception!!.message!!)
                                        else dialogService.showSnackbar(R.string.unknown_error)
                                        delay(2000)
                                    }
                                }
                            }
                        })
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun deleteMeetingRelatedToDogFromLocalDatabase()
    {
        val meetingsList: ArrayList<MyCustomMeetingObj> =
            localDatabaseService.get<ArrayList<MyCustomMeetingObj>>("localMeetingsList") ?: return

        if (meetingsList.any { it.meetingObj!!.dogUid == dog.value!!.uid }) {
            meetingsList.removeIf { x: MyCustomMeetingObj -> x.meetingObj!!.dogUid == dog.value!!.uid }
            dataExchangeService.put(
                MyMeetingsViewModel::class.java.name,
                true
            )
        }
        localDatabaseService.add("localMeetingsList", meetingsList)
    }

}