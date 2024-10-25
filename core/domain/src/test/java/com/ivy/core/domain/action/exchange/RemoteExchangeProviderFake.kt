package com.ivy.core.domain.action.exchange

import com.ivy.data.CurrencyCode
import com.ivy.data.ExchangeRatesMap
import com.ivy.data.exchange.ExchangeProvider
import com.ivy.exchange.RemoteExchangeProvider

class RemoteExchangeProviderFake: RemoteExchangeProvider {

    var ratesMap = mapOf(
        "USD" to mapOf(
            // one us dollar would map to 0.91 euros:
            "EUR" to 0.91,
            "AUD" to 1.49,
            // we want to test something negative as well:
            "CAD" to -3.0,
        ),
        "EUR" to mapOf(
            "EUR" to 1.08,
            "AUD" to 1.62,
            "CAD" to 1.43,
        )
    )

    // we need this to simulate fetching from the api, but not really make the api call:
    override suspend fun fetchExchangeRates(baseCurrency: CurrencyCode): RemoteExchangeProvider.Result {
        return RemoteExchangeProvider.Result(
            ratesMap = ratesMap[baseCurrency] as ExchangeRatesMap,
            // this is depricated, but we can ignore that for now:
            provider = ExchangeProvider.Fawazahmed0
        )
    }
}