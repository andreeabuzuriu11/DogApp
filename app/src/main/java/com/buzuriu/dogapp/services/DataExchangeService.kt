package com.buzuriu.dogapp.services

interface IDataExchangeService {
    fun put(key: String, data: Any)
    fun<T> get(key: String) : T?
}

class DataExchangeService : IDataExchangeService {

    var exchangeMap : MutableMap<String, Any> = mutableMapOf()

    override fun put(key: String, data: Any) {
        exchangeMap[key] = data
    }

    @Suppress("UNCHECKED_CAST")
    override fun<T> get(key: String) : T?{
        var tmp = exchangeMap[key]

        if(tmp != null) {
            exchangeMap.remove(key)
            return tmp as? T
        }

        return null
    }
}