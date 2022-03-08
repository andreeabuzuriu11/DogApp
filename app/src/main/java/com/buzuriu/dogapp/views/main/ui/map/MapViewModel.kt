package com.buzuriu.dogapp.views.main.ui.map

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.buzuriu.dogapp.adapters.FilterAdapter
import com.buzuriu.dogapp.adapters.MeetingAdapter
import com.buzuriu.dogapp.models.*
import com.buzuriu.dogapp.viewModels.BaseViewModel
import com.buzuriu.dogapp.viewModels.FilterMeetingsViewModel
import com.buzuriu.dogapp.viewModels.MeetingDetailViewModel
import com.buzuriu.dogapp.views.AddMeetingActivity
import com.buzuriu.dogapp.views.FilterMeetingsFragment
import com.buzuriu.dogapp.views.MeetingDetailActivity
import com.buzuriu.dogapp.views.SelectBreedFragment
import com.buzuriu.dogapp.views.main.ui.OverlayActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.lang.Exception

class MapViewModel : BaseViewModel() {

    var meetingsList = ArrayList<MyCustomMeetingObj>()
    var meetingAdapter : MeetingAdapter?
    var filtersList = ArrayList<IFilterObj>()
    var filterAdapter: FilterAdapter?

    init{
        meetingAdapter = MeetingAdapter(meetingsList, ::selectedMeeting)
        filterAdapter = FilterAdapter(filtersList)
        filtersList.clear()

        dataExchangeService.put(FilterMeetingsViewModel::class.java.name, true)

        viewModelScope.launch {
            fetchAllMeetings()
        }
    }

    override fun onResume() {
        super.onResume()

        viewModelScope.launch {
            fetchMeetingsByFilter()
        }
    }

    private suspend fun fetchAllMeetings()
    {
        viewModelScope.launch(Dispatchers.IO) {
            var list = fetchAllMeetingsFromDatabase()

            viewModelScope.launch(Dispatchers.Main) {
                meetingsList.clear()
                meetingsList.addAll(list)
                meetingAdapter!!.notifyDataSetChanged()
                filterAdapter!!.notifyDataSetChanged()
            }
        }
    }

    private suspend fun fetchMeetingsByFilter()
    {
        val item =
            dataExchangeService.get<IFilterObj>(this::class.qualifiedName!!)

        if (item == null)
            return

        filtersList.clear()
        dialogService.showSnackbar("your selected filter is " + item.name)
        filtersList.add(item)
        filterAdapter!!.notifyDataSetChanged()

        viewModelScope.launch(Dispatchers.IO) {
            var list = fetchFilteredMeetingsFromDatabase(item)

            viewModelScope.launch(Dispatchers.Main) {
                meetingsList.clear()
                meetingsList.addAll(list)
                meetingAdapter!!.notifyDataSetChanged()
            }
        }
    }

    private suspend fun fetchAllMeetingsFromDatabase() : ArrayList<MyCustomMeetingObj>{
        var user: UserInfo?
        var dog: DogObj?
        val allCustomMeetings = ArrayList<MyCustomMeetingObj>()
        var allMeetings: ArrayList<MeetingObj>? = null

        allMeetings = databaseService.fetchAllMeetings()

        if (allMeetings != null) {
            for (meeting in allMeetings) {
                user = databaseService.fetchUserByUid(meeting.userUid!!)
                dog = databaseService.fetchDogByUid(meeting.dogUid!!)
                
                val meetingObj = MyCustomMeetingObj(meeting, user!!, dog!!)
                allCustomMeetings.add(meetingObj)
            }
        }

        return allCustomMeetings
    }

    private suspend fun fetchFilteredMeetingsFromDatabase(timeFilter: IFilterObj) : ArrayList<MyCustomMeetingObj>{
        var user: UserInfo?
        var dog: DogObj?
        val allCustomMeetings = ArrayList<MyCustomMeetingObj>()
        var allMeetings: ArrayList<MeetingObj>? = null

        allMeetings = databaseService.fetchMeetingsByTime(timeFilter)

        if (allMeetings != null) {
            for (meeting in allMeetings) {
                user = databaseService.fetchUserByUid(meeting.userUid!!)
                dog = databaseService.fetchDogByUid(meeting.dogUid!!)

                val meetingObj = MyCustomMeetingObj(meeting, user!!, dog!!)
                allCustomMeetings.add(meetingObj)
            }
        }

        return allCustomMeetings
    }

    private fun selectedMeeting(myCustomMeetingObj: MyCustomMeetingObj) {
        dataExchangeService.put(MeetingDetailViewModel::class.java.name, myCustomMeetingObj)
        navigationService.navigateToActivity(MeetingDetailActivity::class.java, false)

    }
    fun showMap() {
        viewModelScope.launch(Dispatchers.Main) {

            val hasPermission = askLocationPermission().await()
            if (!hasPermission) {
                dialogService.showSnackbar("Location permission needed")
                return@launch
            }
        }
    }

    fun showFilters()
    {
        navigationService.showOverlay(
            OverlayActivity::class.java,
            false,
            OverlayActivity.fragmentClassNameParam,
            FilterMeetingsFragment::class.qualifiedName
        )
    }

    fun addMeeting()
    {
        if (!doesUserHaveAtLeastOneDog())
        {
            dialogService.showSnackbar("Please add your pet before participating to a meeting")
            return
        }

        viewModelScope.launch(Dispatchers.Main) {
            val hasPermission = askLocationPermission().await()
            if (!hasPermission) {
                dialogService.showSnackbar("Location permission needed to add a meeting!")
                return@launch
            }
            else {
                navigationService.navigateToActivity(AddMeetingActivity::class.java, false)
            }
        }
    }

    private fun doesUserHaveAtLeastOneDog() : Boolean
    {
        if (localDatabaseService.get<ArrayList<DogObj>>("localDogsList")!!.size < 1)
            return false
        return true
    }
}