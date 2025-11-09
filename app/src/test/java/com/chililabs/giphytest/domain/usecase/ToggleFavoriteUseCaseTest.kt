package com.chililabs.giphytest.domain.usecase

import com.chililabs.giphytest.domain.model.Gif
import com.chililabs.giphytest.domain.repo.FavoritesRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ToggleFavoriteUseCaseTest {

    private lateinit var repository: FavoritesRepository
    private lateinit var useCase: ToggleFavoriteUseCase

    @Before
    fun setup() {
        repository = mockk()
        useCase = ToggleFavoriteUseCase(repository)
    }

    @Test
    fun `invoke returns true when adding favorite`() = runTest {
        // Given
        val gif = Gif(id = "test-id", title = "Test")
        coEvery { repository.toggleFavorite(gif) } returns true

        // When
        val result = useCase(gif)

        // Then
        assertTrue(result)
    }

    @Test
    fun `invoke returns false when removing favorite`() = runTest {
        // Given
        val gif = Gif(id = "test-id", title = "Test")
        coEvery { repository.toggleFavorite(gif) } returns false

        // When
        val result = useCase(gif)

        // Then
        assertFalse(result)
    }
}

