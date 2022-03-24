package com.buzuriu.dogapp.views.main.ui.map

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
import com.buzuriu.dogapp.views.main.ui.OverlayActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class MapViewModel : BaseViewModel() {

    var meetingsList = ArrayList<MyCustomMeetingObj>()
    var meetingAdapter : MeetingAdapter?
    var filtersList = ArrayList<IFilterObj>()
    var filterAdapter: FilterAdapter?
    var breedsList = ArrayList<String>()

    init{
        meetingAdapter = MeetingAdapter(meetingsList, ::selectedMeeting)
        filterAdapter = FilterAdapter(filtersList)
        filtersList.clear()

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

    fun getAllDogBreeds()
    {
        for (meeting in meetingsList)
        {
            if(breedsList.find { it == meeting.dog!!.breed}!=null)continue
            breedsList.add(meeting.dog!!.breed)
        }
        localDatabaseService.add(FilterMeetingsViewModel::class.java.name, breedsList)
    }

    private suspend fun fetchAllMeetings()
    {
        viewModelScope.launch(Dispatchers.IO) {
            var list = fetchAllMeetingsFromDatabase()

            viewModelScope.launch(Dispatchers.Main) {
                meetingsList.clear()
                meetingsList.addAll(list)
                getAllDogBreeds()
                meetingAdapter!!.notifyDataSetChanged()
                filterAdapter!!.notifyDataSetChanged()
            }
        }
    }

    private suspend fun fetchMeetingsWithFilters(filtersList: ArrayList<IFilterObj>)
    {
        viewModelScope.launch(Dispatchers.IO) {
            var list = fetchFilteredMeetingsFromDatabase(filtersList)

            viewModelScope.launch(Dispatchers.Main) {
                meetingsList.clear()
                meetingsList.addAll(list)
                meetingAdapter!!.notifyDataSetChanged()
            }
        }

    }

    private suspend fun fetchMeetingsByFilter()
    {
        val filterList =
            dataExchangeService.get<ArrayList<IFilterObj>>(this::class.qualifiedName!!)

        if (filterList == null)
            return

        if (filterList.size == 0)
            return

        filtersList.clear()
        dialogService.showSnackbar("your selected filter is " + filterList[0].name)
        if (filterList != null) {
            filtersList.addAll(filterList)
            fetchMeetingsWithFilters(filtersList)
        }
        else
            fetchAllMeetings()
        filterAdapter!!.notifyDataSetChanged()

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

    private suspend fun fetchFilteredMeetingsFromDatabase(filters: ArrayList<IFilterObj>) : ArrayList<MyCustomMeetingObj>{
        var user: UserInfo?
        var dog: DogObj?
        val allCustomMeetings = ArrayList<MyCustomMeetingObj>()
        var allMeetings: ArrayList<MeetingObj>? = null

        allMeetings = databaseService.fetchMeetingsByFilters(filters)

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
    
}