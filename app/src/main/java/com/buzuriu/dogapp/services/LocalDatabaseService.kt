package com.buzuriu.dogapp.services

interface ILocalDatabaseService {
    fun add(key: String, obj: Any)
    fun <T> get(key:String): T?
    fun clear()
}

class LocalDatabaseService : ILocalDatabaseService {
    var localDatabase = mutableMapOf<String, Any>()

    override fun add(key: String, obj: Any) {
        localDatabase[key] = obj
    }

    override fun <T> get(key: String): T? {
        return localDatabase[key] as T?
    }

    override fun clear() {
        localDatabase.clear()
    }


}