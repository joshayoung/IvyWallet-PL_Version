package com.ivy.common.androidtest

import com.ivy.common.di.CommonModuleDI
import com.ivy.common.time.provider.TimeProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [CommonModuleDI::class]
)
abstract class TestCommonModuleDI {
    // this will replace the common module di
    // bind our time provider fake instead:
    @Binds
    abstract fun timeProvider(provider: TimeProviderFake): TimeProvider

}