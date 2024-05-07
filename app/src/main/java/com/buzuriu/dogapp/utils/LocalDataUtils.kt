package com.buzuriu.dogapp.utils

import com.buzuriu.dogapp.models.DogObj
import com.buzuriu.dogapp.services.ILocalDatabaseService

class LocalDataUtils {
    companion object {
        fun doesUserHaveAtLeastOneDog(localDatabaseService: ILocalDatabaseService): Boolean {
            val listOfDogs = localDatabaseService.get<ArrayList<DogObj>>(LocalDBItems.localDogsList)
            if (listOfDogs != null) {
                if (listOfDogs.size < 1)
                    return false
            }
            return true
        }
    }
}