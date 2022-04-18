package com.buzuriu.dogapp.views.main.ui.map

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.buzuriu.dogapp.adapters.FilterAdapter
import com.buzuriu.dogapp.adapters.FilterAppliedAdapter
import com.buzuriu.dogapp.adapters.MeetingAdapter
import com.buzuriu.dogapp.models.*
import com.buzuriu.dogapp.utils.MapUtils
import com.buzuriu.dogapp.viewModels.BaseViewModel
import com.buzuriu.dogapp.viewModels.FilterMeetingsViewModel
import com.buzuriu.dogapp.viewModels.MeetingDetailViewModel
import com.buzuriu.dogapp.viewModels.MeetingsOnMapViewModel
import com.buzuriu.dogapp.views.FilterMeetingsFragment
import com.buzuriu.dogapp.views.MeetingDetailActivity
import com.buzuriu.dogapp.views.MeetingsOnMapFragment
import com.buzuriu.dogapp.views.main.ui.OverlayActivity
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class MapViewModel : BaseViewModel() {

    var meetingsList = ArrayList<MyCustomMeetingObj>()
    var meetingAdapter : MeetingAdapter?
    var filtersList = ArrayList<IFilterObj>()
    var filterAdapter: FilterAppliedAdapter?
    var breedsList = ArrayList<String>()
    var locationPoints = ArrayList<LatLng>()
    var lastViewModel : String? = null

    init {
        meetingAdapter = MeetingAdapter(meetingsList, ::selectedMeeting)
        filterAdapter = FilterAppliedAdapter(filtersList, this)
        filtersList.clear()

        viewModelScope.launch {
            fetchAllMeetings()
        }
    }

    override fun onResume() {
        super.onResume()

        checkRadiusFilter()
        checkTypeAndTimeFilter()

        lastViewModel = null
    }

    private fun getAllDogBreeds()
    {
        for (meeting in meetingsList)
        {
            if(breedsList.find { it == meeting.dog!!.breed}!=null)continue
            breedsList.add(meeting.dog!!.breed)
        }
        localDatabaseService.add(FilterMeetingsViewModel::class.java.name, breedsList)
    }

    private fun getAllMeetingsLocation()
    {
        for (meeting in meetingsList)
        {
            var latLng = MapUtils.getLatLngFromGeoPoint(meeting.meetingObj!!.location!!)
            locationPoints.add(latLng)
        }
        localDatabaseService.add("locationPoints", locationPoints)
    }

    private suspend fun fetchAllMeetings()
    {
        ShowLoadingView(true)
        viewModelScope.launch(Dispatchers.IO) {
            var list = fetchAllMeetingsFromDatabase()
            ShowLoadingView(false)
            viewModelScope.launch(Dispatchers.Main) {
                meetingsList.clear()
                meetingsList.addAll(list)
                getAllDogBreeds()
                getAllMeetingsLocation()
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

    private fun checkTypeAndTimeFilter() {
        if (lastViewModel.equals(FilterMeetingsViewModel::class.qualifiedName)) {

            val filters =
                dataExchangeService.get<ArrayList<IFilterObj>>(this::class.qualifiedName!!)

            Log.d("noi", "filters = $filters")

            if (filters == null) return
            removeFilterType<FilterByTimeObj>()
            removeFilterType<FilterByDogBreedObj>()
            removeFilterType<FilterByDogGenderObj>()
            removeFilterType<FilterByUserGenderObj>()

            dialogService.showSnackbar("your selected filter is " + filters[0].name)
            filtersList.addAll(filters)
            filterAdapter!!.notifyDataSetChanged()

            applyFilters(filtersList)
        }
    }

    private fun checkRadiusFilter() {
        if (lastViewModel.equals(MeetingsOnMapViewModel::class.qualifiedName)) {

            val mapFilter =
                dataExchangeService.get<IFilterObj>(this::class.qualifiedName!!)

            Log.d("noi", "mapFilter = $mapFilter")

            if (mapFilter == null) return

            removeFilterType<FilterByLocationObj>()

            filtersList.add(mapFilter)
            filterAdapter!!.notifyDataSetChanged()

            dialogService.showSnackbar("your selected filter is " + mapFilter.name)
            applyFilters(filtersList)
        }
    }

    private fun applyFilters(filtersList : ArrayList<IFilterObj>) {
        viewModelScope.launch(Dispatchers.IO) {
            fetchMeetingsWithFilters(filtersList)
        }
    }

    fun filterMeetingsByTypeOrTimeClicked() {
        lastViewModel = FilterMeetingsViewModel::class.qualifiedName
        navigationService.showOverlay(
            OverlayActivity::class.java,
            false,
            OverlayActivity.fragmentClassNameParam,
            FilterMeetingsFragment::class.qualifiedName
        )
    }

    fun filterMeetingsByRadiusClicked() {
        viewModelScope.launch(Dispatchers.Main) {
            val hasLocationPermission = askLocationPermission().await()
            if (hasLocationPermission) {

                lastViewModel = MeetingsOnMapViewModel::class.qualifiedName
                navigationService.showOverlay(
                    OverlayActivity::class.java,
                    false,
                    OverlayActivity.fragmentClassNameParam,
                    MeetingsOnMapFragment::class.qualifiedName
                )
            } else {
                dialogService.showSnackbar("Permission for location needed")
            }
        }
    }


    private suspend fun fetchAllMeetingsFromDatabase() : ArrayList<MyCustomMeetingObj>{
        var user: UserInfo?
        var dog: DogObj?
        val allCustomMeetings = ArrayList<MyCustomMeetingObj>()
        var allMeetings: ArrayList<MeetingObj>? = null

        // currentUser uid as parameter, because we have to ignore that user when searching new meetings
        allMeetings = databaseService.fetchAllOtherMeetings(currentUser!!.uid)

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

        val allMeetings: ArrayList<MeetingObj>? = databaseService.fetchMeetingsByFilters(filters, currentUser!!.uid)

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

    private inline fun <reified T> removeFilterType() {
        filtersList.forEach {
            if (it is T) {
                filtersList.remove(it)
                return
            }
        }
    }

    fun discardFilter(filterType : IFilterObj)
    {
        if (filtersList.size == 1) {
            filtersList.clear()

            viewModelScope.launch {
                fetchAllMeetings()
            }
        }
        else
        {
            filtersList.remove(filterType)


            applyFilters(filtersList)
        }
        filterAdapter?.notifyDataSetChanged()
    }
}