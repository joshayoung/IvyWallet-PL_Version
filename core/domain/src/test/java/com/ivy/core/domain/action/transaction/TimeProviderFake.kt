package com.ivy.core.domain.action.transaction

import com.ivy.common.time.provider.TimeProvider
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

// we do not really need this for the current test:
class TimeProviderFake: TimeProvider {

    override fun timeNow(): LocalDateTime {
        return LocalDateTime.now()
    }

    override fun dateNow(): LocalDate {
        return LocalDate.now()
    }

    override fun zoneId(): ZoneId {
        return ZoneId.of("UTC")
    }
}