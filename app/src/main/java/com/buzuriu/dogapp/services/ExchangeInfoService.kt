package com.buzuriu.dogapp.services

interface IExchangeInfoService {
    fun put(key: String, data: Any)
    fun <T> get(key: String): T?
}

class ExchangeInfoService : IExchangeInfoService {

    var exchangeMap: MutableMap<String, Any> = mutableMapOf()

    override fun put(key: String, data: Any) {
        exchangeMap[key] = data
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T> get(key: String): T? {
        val tmp = exchangeMap[key]

        if (tmp != null) {
            exchangeMap.remove(key)
            return tmp as? T
        }

        return null
    }
}