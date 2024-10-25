package com.ivy.core.domain.action.exchange

import assertk.assertThat
import assertk.assertions.isNotNull
import assertk.assertions.isNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class SyncExchangeRatesActTest {

    private lateinit var syncExchangeRatesAct: SyncExchangeRatesAct
    private lateinit var exchangeProviderFake: RemoteExchangeProviderFake
    private lateinit var exchangeRateDaoFake: ExchangeRateDaoFake

    @BeforeEach
    fun setUp() {
        exchangeProviderFake = RemoteExchangeProviderFake()
        exchangeRateDaoFake = ExchangeRateDaoFake()
        syncExchangeRatesAct = SyncExchangeRatesAct(
            exchangeProvider = exchangeProviderFake,
            exchangeRateDao = exchangeRateDaoFake
        )
    }

    @Test
    // runBlocking, blocks the current thread it is running in:
    fun `Test sync exchange rates, negative values ignored`() = runBlocking {
        // it overrides the invoke operator, so we can call it like a function:
        syncExchangeRatesAct("USD")

        // filter on the base currency:
        val usdRates = exchangeRateDaoFake
            .findAllByBaseCurrency("USD")
            // first emmision that is not empty:
            .first { it.isNotEmpty() }

        // filter on Canadian dollar rate:
        val cadRate = usdRates.find { it.currency == "CAD" }

        // Makes sure it is not in the db:
        assertThat(cadRate).isNull()
    }

    @Test
    // make sure runBlocking returns Unit here:
    fun `Test sync exchange rates, valid values saved`() = runBlocking<Unit> {
        syncExchangeRatesAct("USD")

        val usdRates = exchangeRateDaoFake
            .findAllByBaseCurrency("USD")
            .first { it.isNotEmpty() }
        val eurRate = usdRates.find { it.currency == "EUR" }
        val audRate = usdRates.find { it.currency == "AUD" }

        // make sure the valid currencies are contained:
        assertThat(eurRate).isNotNull()
        assertThat(audRate).isNotNull()
    }
}