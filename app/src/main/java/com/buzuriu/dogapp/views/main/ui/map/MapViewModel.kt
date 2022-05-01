package com.buzuriu.dogapp.views.main.ui.map

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.buzuriu.dogapp.adapters.FilterAppliedAdapter
import com.buzuriu.dogapp.adapters.MeetingAdapter
import com.buzuriu.dogapp.enums.MeetingStateEnum
import com.buzuriu.dogapp.listeners.IClickListener
import com.buzuriu.dogapp.listeners.IOnCompleteListener
import com.buzuriu.dogapp.models.*
import com.buzuriu.dogapp.utils.MapUtils
import com.buzuriu.dogapp.viewModels.*
import com.buzuriu.dogapp.views.FilterMeetingsFragment
import com.buzuriu.dogapp.views.MeetingDetailActivity
import com.buzuriu.dogapp.views.MeetingsOnMapFragment
import com.buzuriu.dogapp.views.SelectDogForJoinMeetFragment
import com.buzuriu.dogapp.views.main.ui.OverlayActivity
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class MapViewModel : BaseViewModel() {

    var meetingAdapter: MeetingAdapter?
    var filterAdapter: FilterAppliedAdapter?

    private var pastMeetingsList = ArrayList<MyCustomMeetingObj>()
    private var meetingsList = ArrayList<MyCustomMeetingObj>()
    private var filtersList = ArrayList<IFilterObj>()
    private var breedsList = ArrayList<String>()
    private var locationPoints = ArrayList<LatLng>()
    private var lastViewModel: String? = null
    private val userJoinedMeetings = ArrayList<MyCustomMeetingObj>()
    var mapOfMeetingUidAndCurrentUserAsParticipant: MutableMap<String, ParticipantObj> =
        mutableMapOf()


    init {
        meetingAdapter = MeetingAdapter(meetingsList, ::selectedMeeting, this)
        filterAdapter = FilterAppliedAdapter(filtersList, this)
        filtersList.clear()

        viewModelScope.launch {
            fetchAllMeetings()
        }

        viewModelScope.launch {
            fetchAllPastMeetings()
        }
    }

    override fun onResume() {
        super.onResume()

        checkRadiusFilter()
        checkTypeAndTimeFilter()

        getMeetingChangedDueToJoin()

        lastViewModel = null
    }

    @SuppressLint("NotifyDataSetChanged")
    fun discardFilter(filterType: IFilterObj) {
        if (filtersList.size == 1) {
            filtersList.clear()

            viewModelScope.launch {
                fetchAllMeetings()
            }
        } else {
            filtersList.remove(filterType)
            applyFilters(filtersList)
        }
        filterAdapter?.notifyDataSetChanged()
    }

    fun joinOrLeaveMeeting(meeting: MyCustomMeetingObj) {
        if (!doesUserHaveAtLeastOneDog()) {
            dialogService.showSnackbar("Please add your pet before participating to a meeting")
            return
        }

        when {
            meeting.meetingStateEnum == MeetingStateEnum.NOT_JOINED -> {
                dataExchangeService.put(SelectDogForJoinMeetViewModel::class.java.name, meeting)

                navigationService.showOverlay(
                    OverlayActivity::class.java,
                    false,
                    OverlayActivity.fragmentClassNameParam,
                    SelectDogForJoinMeetFragment::class.qualifiedName
                )

            }
            hasUserAlreadyJoinedMeeting(meeting) -> {
                return
            }
            meeting.meetingStateEnum == MeetingStateEnum.JOINED -> {
                dialogService.showAlertDialog(
                    "Leave?",
                    "Are you sure you don't want to join this meeting with ${meeting.user!!.name}?",
                    "Yes",
                    object :
                        IClickListener {
                        override fun clicked() {
                            meeting.meetingStateEnum = MeetingStateEnum.NOT_JOINED
                            meetingAdapter!!.notifyItemChanged(meetingsList.indexOf(meeting))
                            val participant =
                                mapOfMeetingUidAndCurrentUserAsParticipant[meeting.meetingObj!!.uid]
                            if (participant != null) {
                                viewModelScope.launch(Dispatchers.IO)
                                {
                                    databaseService.leaveMeeting(meeting.meetingObj!!.uid!!,
                                        participant.uid!!,
                                        object : IOnCompleteListener {
                                            override fun onComplete(
                                                successful: Boolean,
                                                exception: Exception?
                                            ) {
                                                removeMeetFromUserJoinedMeetings(meeting)
                                                dialogService.showSnackbar("Success")
                                            }
                                        })
                                }
                            }
                        }
                    })
            }
        }
    }

    fun removeMeetFromUserJoinedMeetings(meeting: MyCustomMeetingObj) {
        val allMeetingsThatUserJoinedList =
            localDatabaseService.get<ArrayList<MyCustomMeetingObj>>("meetingsUserJoined")
        val toBeRemoved =
            allMeetingsThatUserJoinedList!!.find { it.meetingObj!!.uid == meeting.meetingObj!!.uid }
        allMeetingsThatUserJoinedList.remove(toBeRemoved)
        localDatabaseService.add("meetingsUserJoined", allMeetingsThatUserJoinedList)
    }

    private fun hasUserAlreadyJoinedMeeting(meeting: MyCustomMeetingObj): Boolean {
        var meetingsThatUserAlreadyJoined = ArrayList<MyCustomMeetingObj>()
        viewModelScope.launch {
            meetingsThatUserAlreadyJoined = getAllMeetingsThatUserJoined()
        }

        if (meetingsThatUserAlreadyJoined.find { it.meetingObj!!.uid == meeting.meetingObj!!.uid } != null) {
            return true
        }
        return false
    }

    private fun getMeetingChangedDueToJoin() {
        val changedMeeting = dataExchangeService.get<MyCustomMeetingObj>(this::class.java.name)
        if (changedMeeting != null) {
            changedMeeting.meetingStateEnum = MeetingStateEnum.JOINED
            meetingAdapter!!.notifyItemChanged(meetingsList.indexOf(changedMeeting))
        }
    }

    private fun getAllDogBreeds() {
        for (meeting in meetingsList) {
            if (breedsList.find { it == meeting.dog!!.breed } != null) continue
            breedsList.add(meeting.dog!!.breed)
        }
        localDatabaseService.add(FilterMeetingsViewModel::class.java.name, breedsList)
    }

    private fun getAllMeetingsLocation() {
        for (meeting in meetingsList) {
            val latLng = MapUtils.getLatLngFromGeoPoint(meeting.meetingObj!!.location!!)
            locationPoints.add(latLng)
        }
        localDatabaseService.add("locationPoints", locationPoints)
    }

    private suspend fun getAllMeetingsThatUserJoined(): ArrayList<MyCustomMeetingObj> {
        var allMeetingsParticipants: ArrayList<ParticipantObj>

        for (meeting in meetingsList) {
            allMeetingsParticipants =
                databaseService.fetchAllMeetingParticipants(meeting.meetingObj!!.uid!!)!!
            for (meet in allMeetingsParticipants)
                if (meet.userUid == currentUser!!.uid) {
                    userJoinedMeetings.add(meeting)
                    changeStateOfMeeting(meeting)
                    mapOfMeetingUidAndCurrentUserAsParticipant[meeting.meetingObj!!.uid!!] =
                        meet
                }
        }
        return userJoinedMeetings
    }

    private fun changeStateOfMeeting(meeting: MyCustomMeetingObj) {
        if (hasUserJoinedThisMeeting(meeting)) {
            changeStateAccordingly(meeting, MeetingStateEnum.JOINED)
        } else {
            changeStateAccordingly(meeting, MeetingStateEnum.NOT_JOINED)
        }
    }

    private fun changeStateAccordingly(
        meeting: MyCustomMeetingObj,
        meetingStateEnum: MeetingStateEnum
    ) {
        meeting.meetingStateEnum = meetingStateEnum
    }

    private fun hasUserJoinedThisMeeting(meeting: MyCustomMeetingObj): Boolean {
        for (userJoinMeeting in userJoinedMeetings)
            if (meeting.meetingObj!!.uid == userJoinMeeting.meetingObj!!.uid)
                return true
        return false
    }

    @SuppressLint("NotifyDataSetChanged")
    private suspend fun fetchAllMeetings() {
        ShowLoadingView(true)
        viewModelScope.launch(Dispatchers.IO) {
            val list = fetchAllMeetingsFromDatabase()
            ShowLoadingView(false)
            viewModelScope.launch(Dispatchers.Main) {
                meetingsList.clear()
                meetingsList.addAll(list)
                getAllDogBreeds()
                getAllMeetingsLocation()
                getAllMeetingsThatUserJoined()
                meetingAdapter!!.notifyDataSetChanged()
                filterAdapter!!.notifyDataSetChanged()
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private suspend fun fetchMeetingsWithFilters(filtersList: ArrayList<IFilterObj>) {
        viewModelScope.launch(Dispatchers.IO) {
            val list = fetchFilteredMeetingsFromDatabase(filtersList)

            viewModelScope.launch(Dispatchers.Main) {
                meetingsList.clear()
                meetingsList.addAll(list)
                getAllMeetingsThatUserJoined()
                meetingAdapter!!.notifyDataSetChanged()
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private suspend fun fetchAllPastMeetings() {
        ShowLoadingView(true)
        viewModelScope.launch(Dispatchers.IO) {
            val list = fetchAllPastMeetingsFromDatabase()
            ShowLoadingView(false)
            viewModelScope.launch(Dispatchers.Main) {
                pastMeetingsList.clear()
                pastMeetingsList.addAll(list)
                localDatabaseService.add("pastMeetingsUserJoined", pastMeetingsList)
            }
        }
    }

    private suspend fun fetchAllPastMeetingsFromDatabase(): ArrayList<MyCustomMeetingObj> {
        var user: UserInfo?
        var dog: DogObj?
        val allCustomMeetings = ArrayList<MyCustomMeetingObj>()

        // currentUser uid as parameter, because we have to ignore that user when searching new meetings
        val allMeetings: ArrayList<MeetingObj>? =
            databaseService.fetchAllOtherPastMeetings(currentUser!!.uid)

        if (allMeetings != null) {
            for (meeting in allMeetings) {
                Log.d("andreea1", "user participated to this meeting: ${meeting.uid}")
                user = databaseService.fetchUserByUid(meeting.userUid!!)
                dog = databaseService.fetchDogByUid(meeting.dogUid!!)

                if (user != null && dog != null) {
                    val meetingObj = MyCustomMeetingObj(meeting, user, dog)
                    allCustomMeetings.add(meetingObj)
                }
            }
        }

        Log.d("andreea2", "${pastMeetingsList.size}")
        return allCustomMeetings
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun checkTypeAndTimeFilter() {
        if (lastViewModel.equals(FilterMeetingsViewModel::class.qualifiedName)) {

            val filters =
                dataExchangeService.get<ArrayList<IFilterObj>>(this::class.qualifiedName!!)
                    ?: return

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

    @SuppressLint("NotifyDataSetChanged")
    private fun checkRadiusFilter() {
        if (lastViewModel.equals(MeetingsOnMapViewModel::class.qualifiedName)) {

            val mapFilter =
                dataExchangeService.get<IFilterObj>(this::class.qualifiedName!!) ?: return

            removeFilterType<FilterByLocationObj>()

            filtersList.add(mapFilter)
            filterAdapter!!.notifyDataSetChanged()

            dialogService.showSnackbar("your selected filter is " + mapFilter.name)
            applyFilters(filtersList)
        }
    }

    private fun applyFilters(filtersList: ArrayList<IFilterObj>) {
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

    private suspend fun fetchAllMeetingsFromDatabase(): ArrayList<MyCustomMeetingObj> {
        var user: UserInfo?
        var dog: DogObj?
        val allCustomMeetings = ArrayList<MyCustomMeetingObj>()

        // currentUser uid as parameter, because we have to ignore that user when searching new meetings
        val allMeetings: ArrayList<MeetingObj>? =
            databaseService.fetchAllOtherMeetings(currentUser!!.uid)

        if (allMeetings != null) {
            for (meeting in allMeetings) {
                user = databaseService.fetchUserByUid(meeting.userUid!!)
                dog = databaseService.fetchDogByUid(meeting.dogUid!!)

                if (user != null && dog != null) {
                    val meetingObj = MyCustomMeetingObj(meeting, user, dog)
                    allCustomMeetings.add(meetingObj)
                }
            }
        }

        return allCustomMeetings
    }

    private suspend fun fetchFilteredMeetingsFromDatabase(filters: ArrayList<IFilterObj>): ArrayList<MyCustomMeetingObj> {
        var user: UserInfo?
        var dog: DogObj?
        val allCustomMeetings = ArrayList<MyCustomMeetingObj>()

        val allMeetings: ArrayList<MeetingObj>? =
            databaseService.fetchMeetingsByFilters(filters, currentUser!!.uid)

        if (allMeetings != null) {
            for (meeting in allMeetings) {
                user = databaseService.fetchUserByUid(meeting.userUid!!)
                dog = databaseService.fetchDogByUid(meeting.dogUid!!)

                if (user != null && dog != null) {
                    val meetingObj = MyCustomMeetingObj(meeting, user, dog)
                    allCustomMeetings.add(meetingObj)
                }
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

    private fun doesUserHaveAtLeastOneDog(): Boolean {
        if (localDatabaseService.get<ArrayList<DogObj>>("localDogsList")!!.size < 1)
            return false
        return true
    }
}