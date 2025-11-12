package com.chililabs.giphytest.data.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.IOException
import androidx.datastore.dataStoreFile
import androidx.test.core.app.ApplicationProvider
import com.chililabs.giphytest.domain.model.AppTheme
import com.chililabs.giphytest.proto.AppSettings
import com.chililabs.giphytest.proto.ThemeMode
import com.chililabs.giphytest.proto.copy
import com.chililabs.giphytest.utils.ext.invokeAndWait
import com.chililabs.giphytest.utils.serializer.AppSettingsSerializer
import io.mockk.clearAllMocks
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
@OptIn(ExperimentalCoroutinesApi::class)
class AppSettingsRepositoryImplTest {

    private lateinit var testDataStore: DataStore<AppSettings>
    private lateinit var repository: AppSettingsRepositoryImpl
    private val testDispatcher = StandardTestDispatcher()
    private val testContext: Context = ApplicationProvider.getApplicationContext()

    @Before
    fun setup() {
        testDataStore = DataStoreFactory.create(
            produceFile = { testContext.dataStoreFile("test_app_settings.pb") },
            serializer = AppSettingsSerializer()
        )
        repository = AppSettingsRepositoryImpl(testDataStore)
    }

    @After
    fun tearDown() {
        File(testContext.filesDir, "datastore").deleteRecursively()
        clearAllMocks()
    }

    @Test
    fun `settings returns default AppSettings when no data exists`() = runTest(testDispatcher) {
        // When
        val result = repository.settings.first()

        // Then
        assertEquals(AppSettings.getDefaultInstance(), result)
    }

    @Test
    fun `theme returns System when themeMode is THEME_MODE_SYSTEM`() = runTest(testDispatcher) {
        // Given
        invokeAndWait {
            testDataStore.updateData { currentSettings ->
                currentSettings.copy {
                    this.themeMode = ThemeMode.THEME_MODE_SYSTEM
                }
            }
        }

        // When
        val result = repository.theme.first()

        // Then
        assertEquals(AppTheme.System, result)
    }

    @Test
    fun `theme returns Light when themeMode is THEME_MODE_LIGHT`() = runTest(testDispatcher) {
        // Given
        invokeAndWait {
            testDataStore.updateData { currentSettings ->
                currentSettings.copy {
                    this.themeMode = ThemeMode.THEME_MODE_LIGHT
                }
            }
        }

        // When
        val result = repository.theme.first()

        // Then
        assertEquals(AppTheme.Light, result)
    }

    @Test
    fun `theme returns Dark when themeMode is THEME_MODE_DARK`() = runTest(testDispatcher) {
        // Given
        invokeAndWait {
            testDataStore.updateData { currentSettings ->
                currentSettings.copy {
                    this.themeMode = ThemeMode.THEME_MODE_DARK
                }
            }
        }

        // When
        val result = repository.theme.first()

        // Then
        assertEquals(AppTheme.Dark, result)
    }

    @Test
    fun `theme returns System when themeMode is THEME_MODE_UNSPECIFIED`() = runTest(testDispatcher) {
        // Given
        invokeAndWait {
            testDataStore.updateData { currentSettings ->
                currentSettings.copy {
                    this.themeMode = ThemeMode.THEME_MODE_UNSPECIFIED
                }
            }
        }

        // When
        val result = repository.theme.first()

        // Then
        assertEquals(AppTheme.System, result)
    }

    @Test
    fun `setTheme updates theme to Light`() = runTest(testDispatcher) {
        // When
        invokeAndWait {
            repository.setTheme(AppTheme.Light)
        }

        // Then
        val settings = repository.settings.first()
        assertEquals(ThemeMode.THEME_MODE_LIGHT, settings.themeMode)
    }

    @Test
    fun `setTheme updates theme to Dark`() = runTest(testDispatcher) {
        // When
        invokeAndWait {
            repository.setTheme(AppTheme.Dark)
        }

        // Then
        val settings = repository.settings.first()
        assertEquals(ThemeMode.THEME_MODE_DARK, settings.themeMode)
    }

    @Test
    fun `setTheme updates theme to System`() = runTest(testDispatcher) {
        // Given - first set to Light
        invokeAndWait {
            repository.setTheme(AppTheme.Light)
        }

        // When - change to System
        invokeAndWait {
            repository.setTheme(AppTheme.System)
        }

        // Then
        val settings = repository.settings.first()
        assertEquals(ThemeMode.THEME_MODE_SYSTEM, settings.themeMode)
    }

    @Test
    fun `setTheme handles IOException gracefully`() = runTest(testDispatcher) {
        // Given - create a DataStore that throws IOException on update
        val failingDataStore = object : DataStore<AppSettings> {
            override val data = testDataStore.data
            
            override suspend fun updateData(transform: suspend (t: AppSettings) -> AppSettings): AppSettings {
                throw IOException("Disk full")
            }
        }
        val failingRepository = AppSettingsRepositoryImpl(failingDataStore)

        // When - should not throw exception
        invokeAndWait {
            failingRepository.setTheme(AppTheme.Light)
        }

        // Then - exception is caught and logged, no crash
        // The test passes if no exception is thrown
    }

    @Test
    fun `theme flow emits updated value when theme changes`() = runTest(testDispatcher) {
        // Given
        val themes = mutableListOf<AppTheme>()
        val job = launch(testDispatcher) {
            repository.theme.collect { themes.add(it) }
        }
        advanceUntilIdle()

        // When - update theme multiple times
        invokeAndWait {
            repository.setTheme(AppTheme.System)
        }
        invokeAndWait {
            repository.setTheme(AppTheme.Light)
        }
        invokeAndWait {
            repository.setTheme(AppTheme.Dark)
        }

        // Then
        job.cancel()
        // Should have at least the initial value and the three updates
        assert(themes.size >= 4)
        assertEquals(AppTheme.Dark, themes.last())
    }
}

