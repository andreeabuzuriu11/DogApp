package com.buzuriu.dogapp.viewModels

import android.os.Build
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
import com.buzuriu.dogapp.utils.LocalDBItems
import com.buzuriu.dogapp.views.AddDogActivity
import com.buzuriu.dogapp.views.main.ui.my_dogs.MyDogsViewModel
import com.buzuriu.dogapp.views.main.ui.my_meetings.MyMeetingsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class DogDetailViewModel : BaseViewModel() {

    var dog = MutableLiveData<DogObj>()

    init {
        dog.value = exchangeInfoService.get<DogObj>(this::class.java.name)!!
    }

    override fun onResume() {
        super.onResume()
        val editedDog = exchangeInfoService.get<DogObj>(this::class.java.name)
        if (editedDog != null) {
            dog.value = editedDog!!
        }
    }

    fun editDog() {
        exchangeInfoService.put(AddDogViewModel::class.java.name, dog.value!!)
        navigationService.navigateToActivity(AddDogActivity::class.java, false)
    }

    fun deleteDog() {
        alertMessageService.displayAlertDialog(
            "Delete dog?",
            "Are you sure you want to delete ${dog.value!!.name}? This action cannot be undone.",
            "Yes, delete it",
            object :
                IClickListener {
                override fun clicked() {
                    deleteDogFromDatabase()
                    deleteDogRelatedToUserFromDatabase()
                    exchangeInfoService.put(
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
                    val allDogsList = localDatabaseService.get<ArrayList<DogObj>>(LocalDBItems.localDogsList)
                    allDogsList!!.remove(dog.value!!)

                    localDatabaseService.add(LocalDBItems.localDogsList, allDogsList)
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
            databaseService.deleteDogRelatedToUser(
                currentUser!!.uid,
                dog.value!!.uid,
                object : IOnCompleteListener {
                    @RequiresApi(Build.VERSION_CODES.N)
                    override fun onComplete(successful: Boolean, exception: Exception?) {

                    }
                })
        }
    }

    fun deleteParticipantObjWhereThisDogWasAttending() {
        var meetings: ArrayList<MeetingObj>
        var participants: ArrayList<ParticipantObj>

        viewModelScope.launch(Dispatchers.IO) {
            meetings = databaseService.fetchDogMeetings(dog.value!!.uid)!!

            viewModelScope.launch(Dispatchers.Main) {
                for (meeting in meetings) {
                    participants = databaseService.fetchAllMeetingParticipants(meeting.uid!!)!!

                    for (participant in participants) {
                        if (participant.dogUid == dog.value!!.uid) {
                            // this participant should be deleted from database
                            databaseService.deleteParticipant(
                                meeting.uid!!,
                                participant.uid!!,
                                object : IOnCompleteListener {
                                    override fun onComplete(
                                        successful: Boolean,
                                        exception: java.lang.Exception?
                                    ) {
                                        if (successful) {
                                            viewModelScope.launch(Dispatchers.Main) {
                                                snackMessageService.displaySnackBar("This dog is deleted as participant to all meetings he was attending")
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
                                        snackMessageService.displaySnackBar("All meetings with this dog have been deleted")
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

    @RequiresApi(Build.VERSION_CODES.N)
    fun deleteMeetingRelatedToDogFromLocalDatabase() {
        val meetingsList: ArrayList<MyCustomMeetingObj> =
            localDatabaseService.get<ArrayList<MyCustomMeetingObj>>(LocalDBItems.localMeetingsList) ?: return

        if (meetingsList.any { it.meetingObj!!.dogUid == dog.value!!.uid }) {
            meetingsList.removeIf { x: MyCustomMeetingObj -> x.meetingObj!!.dogUid == dog.value!!.uid }
            exchangeInfoService.put(
                MyMeetingsViewModel::class.java.name,
                true
            )
        }
        localDatabaseService.add(LocalDBItems.localMeetingsList, meetingsList)
    }

}