package com.chililabs.giphytest.domain.usecase

import com.chililabs.giphytest.domain.repo.FavoritesRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class IsFavoriteUseCaseTest {

    private lateinit var repository: FavoritesRepository
    private lateinit var useCase: IsFavoriteUseCase
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        repository = mockk()
        useCase = IsFavoriteUseCase(repository, testDispatcher)
    }

    @Test
    fun `invoke returns true when gif is favorite`() = runTest(testDispatcher) {
        // Given
        val gifId = "favorite-id"
        coEvery { repository.isFavorite(gifId) } returns true

        // When
        val result = useCase(gifId)

        // Then
        assertTrue(result)
    }

    @Test
    fun `invoke returns false when gif is not favorite`() = runTest(testDispatcher) {
        // Given
        val gifId = "not-favorite-id"
        coEvery { repository.isFavorite(gifId) } returns false

        // When
        val result = useCase(gifId)

        // Then
        assertFalse(result)
    }
}

