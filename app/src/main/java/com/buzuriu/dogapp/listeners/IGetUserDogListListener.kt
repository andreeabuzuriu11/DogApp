package com.buzuriu.dogapp.listeners

import com.buzuriu.dogapp.models.DogObj

interface IGetUserDogListListener {
    fun getDogList(dogList : ArrayList<DogObj>)
}