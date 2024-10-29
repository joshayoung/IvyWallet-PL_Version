package com.ivy.core.domain.action.exchange

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isEqualTo
import com.ivy.MainCoroutineExtension
import com.ivy.TestDispatchers
import com.ivy.core.domain.action.settings.basecurrency.BaseCurrencyFlow
import com.ivy.core.persistence.entity.exchange.ExchangeRateEntity
import com.ivy.core.persistence.entity.exchange.ExchangeRateOverrideEntity
import com.ivy.data.SyncState
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.RegisterExtension

@OptIn(ExperimentalCoroutinesApi::class)
//@ExtendWith(MainCoroutineExtension::class)
class ExchangeRatesFlowTest {

    private lateinit var exchangeRatesFlow: ExchangeRatesFlow
    private lateinit var baseCurrencyFlow: BaseCurrencyFlow
    private lateinit var exchangeRateDao: ExchangeRateDaoFake
    private lateinit var exchangeRateOverrideDao: ExchangeRateOverrideDaoFake

    companion object {
        // we need to get an instance of the MainCoroutineExtension:
        // this is equivalent to @ExtendWith, but we get an instance of it:
        @JvmField
        @RegisterExtension
        val mainCoroutineExtension = MainCoroutineExtension()
    }

    @BeforeEach
    fun setUp() {
        // there would be a lot of work to setup a fake for this,
        // so we will just mock it:
        baseCurrencyFlow = mockk()

        // 'every' a function we use with mocking:
        // just return 'EURs':
        // returns our own flow:
        // start with a default emission of ""
        every { baseCurrencyFlow.invoke() } returns flowOf("", "EUR")

        exchangeRateDao = ExchangeRateDaoFake()
        exchangeRateOverrideDao = ExchangeRateOverrideDaoFake()

        // pass in mainCoroutineExtension here:
        val testDispatchers = TestDispatchers(mainCoroutineExtension.testDispatcher)

        exchangeRatesFlow = ExchangeRatesFlow(
            baseCurrencyFlow = baseCurrencyFlow,
            exchangeRateDao = exchangeRateDao,
            exchangeRateOverrideDao = exchangeRateOverrideDao,
            // provide dispatchers:
            dispatchers = testDispatchers
        )
    }

    @Test
    fun `Test exchange rates flow emissions`() = runTest {
        val exchangeRates = listOf(
            exchangeRateEntity("USD", 1.3),
            exchangeRateEntity("CAD", 1.7),
            exchangeRateEntity("AUD", 1.9),
        )
        val exchangeRateOverrides = listOf(
            exchangeRateOverrideEntity("CAD", 1.5)
        )

        exchangeRatesFlow().test {
            // skip the initial emission:
            awaitItem() // Initial emission, ignore

            // save exchange rates in dao:
            exchangeRateDao.save(exchangeRates)
            exchangeRateOverrideDao.save(exchangeRateOverrides)

            // emission:
            val rates1 = awaitItem()

            // expect 3 currencies:
            assertThat(rates1.rates).hasSize(3)

            assertThat(rates1.rates["USD"]).isEqualTo(1.3)

            // expect our overridden value:
            assertThat(rates1.rates["CAD"]).isEqualTo(1.5) // Override rate

            assertThat(rates1.rates["AUD"]).isEqualTo(1.9)

            // remove override:
            exchangeRateOverrideDao.save(emptyList())

            // triggers another emission:
            val rates2 = awaitItem()

            assertThat(rates2.rates).hasSize(3)
            assertThat(rates2.rates["USD"]).isEqualTo(1.3)
            assertThat(rates2.rates["CAD"]).isEqualTo(1.7) // Real rate
            assertThat(rates2.rates["AUD"]).isEqualTo(1.9)
        }
    }
}