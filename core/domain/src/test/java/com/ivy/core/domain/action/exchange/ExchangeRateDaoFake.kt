package com.ivy.core.domain.action.exchange

import com.ivy.core.persistence.dao.exchange.ExchangeRateDao
import com.ivy.core.persistence.entity.exchange.ExchangeRateEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class ExchangeRateDaoFake: ExchangeRateDao {

    private val rates = MutableStateFlow<List<ExchangeRateEntity>>(emptyList())

    override suspend fun save(values: List<ExchangeRateEntity>) {
        // save to state flow:
        rates.value = values
    }

    // needs to return a flow:
    // it needs to emit the new results
    // we want new saved values to be emitted in the flow as well.
    // we want reactive behavior
    override fun findAllByBaseCurrency(baseCurrency: String): Flow<List<ExchangeRateEntity>> {
        return rates
            .map {
                // map it to what is returned from the search query:
                it.filter { it.baseCurrency.uppercase() == baseCurrency.uppercase() }
            }
    }
}