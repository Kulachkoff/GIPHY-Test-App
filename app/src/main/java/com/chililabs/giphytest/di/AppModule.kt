package com.chililabs.giphytest.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import com.chililabs.giphytest.proto.AppSettings
import com.chililabs.giphytest.utils.annotation.AppCoroutineScope
import com.chililabs.giphytest.utils.annotation.DefaultDispatcher
import com.chililabs.giphytest.utils.annotation.IODispatcher
import com.chililabs.giphytest.utils.network.ConnectivityNetworkMonitor
import com.chililabs.giphytest.utils.network.NetworkMonitor
import com.chililabs.giphytest.utils.serializer.AppSettingsSerializer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideNetworkMonitor(
        @ApplicationContext context: Context
    ): NetworkMonitor = ConnectivityNetworkMonitor(context)

    @Provides
    @IODispatcher
    fun provideIODispatcher(): CoroutineDispatcher = Dispatchers.IO

    @Provides
    @DefaultDispatcher
    fun provideDefaultDispatcher(): CoroutineDispatcher = Dispatchers.Default

    @Provides
    @Singleton
    @AppCoroutineScope
    fun providesCoroutineScope(
        @DefaultDispatcher dispatcher: CoroutineDispatcher
    ): CoroutineScope = CoroutineScope(SupervisorJob() + dispatcher)

    @Provides
    @Singleton
    fun provideAppSettingsStore(
        @ApplicationContext context: Context,
        @IODispatcher ioDispatcher: CoroutineDispatcher,
        @AppCoroutineScope scope: CoroutineScope,
        appSettingsSerializer: AppSettingsSerializer
    ): DataStore<AppSettings> =
        DataStoreFactory.create(
            serializer = appSettingsSerializer,
            scope = CoroutineScope(scope.coroutineContext + ioDispatcher)
        ) {
            context.dataStoreFile("app_settings.pb")
        }
}