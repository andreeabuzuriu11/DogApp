package com.buzuriu.dogapp.views.main.ui.map

import android.annotation.SuppressLint
import androidx.lifecycle.viewModelScope
import com.buzuriu.dogapp.adapters.FilterAppliedAdapter
import com.buzuriu.dogapp.adapters.MeetingAdapter
import com.buzuriu.dogapp.enums.MeetingStateEnum
import com.buzuriu.dogapp.listeners.IClickListener
import com.buzuriu.dogapp.listeners.IOnCompleteListener
import com.buzuriu.dogapp.models.*
import com.buzuriu.dogapp.utils.FieldsItems
import com.buzuriu.dogapp.utils.LocalDBItems
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

    private var pastMeetingsListUserCreated = ArrayList<MyCustomMeetingObj>()
    private var pastMeetingsListUserJoined = ArrayList<MyCustomMeetingObj>()
    private var meetingsList = ArrayList<MyCustomMeetingObj>()
    private var filtersList = ArrayList<IFilterObj>()
    private var breedsList = ArrayList<String>()
    private var locationPoints = ArrayList<LatLng>()
    private var lastViewModel: String? = null
    private val userJoinedMeetings = ArrayList<MyCustomMeetingObj>()
    private var userReviewsForOthers = ArrayList<ReviewObj>()
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

        viewModelScope.launch {
            fetchAllReviewsUserLeft()
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
            snackMessageService.displaySnackBar("Please add your pet before participating to a meeting")
            return
        }

        when {
            meeting.meetingStateEnum == MeetingStateEnum.NOT_JOINED -> {
                exchangeInfoService.put(SelectDogForJoinMeetViewModel::class.java.name, meeting)

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
                alertMessageService.displayAlertDialog(
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
                                                snackMessageService.displaySnackBar("Success")
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
            localDatabaseService.get<ArrayList<MyCustomMeetingObj>>(LocalDBItems.meetingsUserJoined)
        val toBeRemoved =
            allMeetingsThatUserJoinedList!!.find { it.meetingObj!!.uid == meeting.meetingObj!!.uid }
        allMeetingsThatUserJoinedList.remove(toBeRemoved)
        localDatabaseService.add(LocalDBItems.meetingsUserJoined, allMeetingsThatUserJoinedList)
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
        val changedMeeting = exchangeInfoService.get<MyCustomMeetingObj>(this::class.java.name)
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
        localDatabaseService.add(LocalDBItems.locationPoints, locationPoints)
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
        showLoadingView(true)
        viewModelScope.launch(Dispatchers.IO) {
            val list = fetchAllMeetingsFromDatabase()
            showLoadingView(false)
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

    private suspend fun fetchAllReviewsUserLeft() {
        showLoadingView(true)
        viewModelScope.launch(Dispatchers.IO) {
            val list = getAllReviewsThatUserLeft()
            showLoadingView(false)
            viewModelScope.launch(Dispatchers.Main) {
                userReviewsForOthers.clear()
                userReviewsForOthers.addAll(list)
                localDatabaseService.add(LocalDBItems.reviewsUserLeft, userReviewsForOthers)
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private suspend fun fetchAllPastMeetings() {
        showLoadingView(true)
        viewModelScope.launch(Dispatchers.IO) {
            val list = fetchAllPastMeetingsFromDatabase()
            showLoadingView(false)
            viewModelScope.launch(Dispatchers.Main) {
                pastMeetingsListUserCreated.clear()
                pastMeetingsListUserJoined.clear()
                for (item in list) {
                    if (item.meetingObj!!.userUid == currentUser!!.uid) {
                        // if meeting has user uid same id as current, it means that the user created
                        // that particular meeting
                        pastMeetingsListUserCreated.add(item)
                    } else // we should check that this user id is among the participant ids
                    {
                        val allMeetingsUserJoined = getAllMeetingsThatUserJoined()
                        for (meetUserJoined in allMeetingsUserJoined) {
                            if (meetUserJoined.meetingObj!!.uid == item.meetingObj!!.uid) {
                                pastMeetingsListUserJoined.add(item)
                            }
                        }
                    }
                }
                localDatabaseService.add(
                    LocalDBItems.pastMeetingsUserCreated,
                    pastMeetingsListUserCreated
                )
                localDatabaseService.add(
                    LocalDBItems.pastMeetingsUserJoined,
                    pastMeetingsListUserJoined
                )
            }
        }
    }

    private suspend fun fetchAllPastMeetingsFromDatabase(): ArrayList<MyCustomMeetingObj> {
        var user: UserObj?
        var dog: DogObj?
        val allCustomMeetings = ArrayList<MyCustomMeetingObj>()

        // currentUser uid as parameter, because we have to ignore that user when searching new meetings
        val allMeetings: ArrayList<MeetingObj>? =
            databaseService.fetchAllPastMeetings(currentUser!!.uid)

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


    private suspend fun getAllReviewsThatUserLeft(): ArrayList<ReviewObj> {
        val allReviewsUserHasLeft = ArrayList<ReviewObj>()

        val list: ArrayList<ReviewObj>? =
            databaseService.fetchReviewsFor(FieldsItems.userIdThatLeftReview, currentUser!!.uid)
        if (list != null) {
            for (review in list) {
                val reviewObj = ReviewObj(
                    review.uid!!,
                    review.userIdThatLeftReview!!,
                    review.userThatReviewIsFor!!,
                    review.numberOfStars!!
                )
                allReviewsUserHasLeft.add(reviewObj)
            }
        }
        return allReviewsUserHasLeft
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun checkTypeAndTimeFilter() {
        if (lastViewModel.equals(FilterMeetingsViewModel::class.qualifiedName)) {

            val filters =
                exchangeInfoService.get<ArrayList<IFilterObj>>(this::class.qualifiedName!!)
                    ?: return

            removeFilterType<FilterByTimeObj>()
            removeFilterType<FilterByDogBreedObj>()
            removeFilterType<FilterByDogGenderObj>()
            removeFilterType<FilterByUserGenderObj>()

            filtersList.addAll(filters)
            filterAdapter!!.notifyDataSetChanged()

            applyFilters(filtersList)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun checkRadiusFilter() {
        if (lastViewModel.equals(MeetingsOnMapViewModel::class.qualifiedName)) {

            val mapFilter =
                exchangeInfoService.get<IFilterObj>(this::class.qualifiedName!!) ?: return

            removeFilterType<FilterByLocationObj>()

            filtersList.add(mapFilter)
            filterAdapter!!.notifyDataSetChanged()

            snackMessageService.displaySnackBar("your selected filter is " + mapFilter.name)
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
                snackMessageService.displaySnackBar("Permission for location needed")
            }
        }
    }

    private suspend fun fetchAllMeetingsFromDatabase(): ArrayList<MyCustomMeetingObj> {
        var user: UserObj?
        var dog: DogObj?
        val allCustomMeetings = ArrayList<MyCustomMeetingObj>()

        // currentUser uid as parameter, because we have to ignore that user when searching new meetings
        val allMeetings: ArrayList<MeetingObj>? =
            databaseService.fetchAllOtherMeetings(currentUser!!.uid)

        if (allMeetings != null) {
            for (meeting in allMeetings) {
                user = databaseService.fetchUserByUid(meeting.userUid!!)
                dog = databaseService.fetchDogByUid(meeting.dogUid!!)

                val reviews = fetchUserReviews(meeting.userUid!!)!!
                val meanOfReviews = getMeanOfReviews(reviews)

                if (user != null && dog != null) {
                    user.rating = meanOfReviews
                    val meetingObj = MyCustomMeetingObj(meeting, user, dog)
                    allCustomMeetings.add(meetingObj)
                }


            }
        }

        return allCustomMeetings
    }

    private suspend fun fetchUserReviews(userUid: String): ArrayList<ReviewObj>? {
        return databaseService.fetchReviewsFor(FieldsItems.userThatReviewIsFor, userUid)
    }

    private fun getMeanOfReviews(reviews: ArrayList<ReviewObj>): Float {
        var sum = 0.0f
        for (review in reviews) {
            sum += review.numberOfStars!!
        }
        return sum / reviews.size
    }

    private suspend fun fetchFilteredMeetingsFromDatabase(filters: ArrayList<IFilterObj>): ArrayList<MyCustomMeetingObj> {
        var user: UserObj?
        var dog: DogObj?
        val allCustomMeetings = ArrayList<MyCustomMeetingObj>()

        val allMeetings: ArrayList<MeetingObj>? =
            databaseService.fetchMeetingsByFilters(filters, currentUser!!.uid)

        if (allMeetings != null) {
            for (meeting in allMeetings) {
                user = databaseService.fetchUserByUid(meeting.userUid!!)
                dog = databaseService.fetchDogByUid(meeting.dogUid!!)

                val reviews = fetchUserReviews(meeting.userUid!!)
                if (reviews != null) {
                    val meanOfReviews = getMeanOfReviews(reviews)
                    user!!.rating = meanOfReviews
                    if (dog != null) {
                        val meetingObj = MyCustomMeetingObj(meeting, user, dog)
                        allCustomMeetings.add(meetingObj)
                    }
                } else {
                    if (user != null && dog != null) {
                        val meetingObj = MyCustomMeetingObj(meeting, user, dog)
                        allCustomMeetings.add(meetingObj)
                    }
                }


            }
        }
        return allCustomMeetings
    }

    private fun selectedMeeting(myCustomMeetingObj: MyCustomMeetingObj) {
        exchangeInfoService.put(MeetingDetailViewModel::class.java.name, myCustomMeetingObj)
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
        val listOfDogs = localDatabaseService.get<ArrayList<DogObj>>(LocalDBItems.localDogsList)
        if (listOfDogs != null) {
            if (listOfDogs.size < 1)
                return false
        }
        return true
    }

}