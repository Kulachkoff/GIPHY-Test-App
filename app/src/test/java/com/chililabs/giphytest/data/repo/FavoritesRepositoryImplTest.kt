package com.chililabs.giphytest.data.repo

import com.chililabs.giphytest.data.local.dao.FavoriteGifDao
import com.chililabs.giphytest.data.local.entity.FavoriteGifEntity
import com.chililabs.giphytest.data.mapper.toDomainList
import com.chililabs.giphytest.domain.model.Gif
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class FavoritesRepositoryImplTest {

    private lateinit var favoriteGifDao: FavoriteGifDao
    private lateinit var repository: FavoritesRepositoryImpl

    @Before
    fun setup() {
        favoriteGifDao = mockk()
        repository = FavoritesRepositoryImpl(favoriteGifDao)
    }

    @Test
    fun `getFavorites returns list of favorites`() = runTest {
        // Given
        val entities = listOf(
            FavoriteGifEntity(id = "1", title = "GIF 1", username = "user1"),
            FavoriteGifEntity(id = "2", title = "GIF 2", username = "user2")
        )
        coEvery { favoriteGifDao.getAll() } returns flowOf(entities)

        // When
        val result = repository.getFavorites().first()

        // Then
        assertEquals(entities.toDomainList(), result)
    }

    @Test
    fun `getFavorites returns empty list when no favorites`() = runTest {
        // Given
        coEvery { favoriteGifDao.getAll() } returns flowOf(emptyList())

        // When
        val result = repository.getFavorites().first()

        // Then
        assertTrue(result.isEmpty())
    }

    @Test
    fun `toggleFavorite adds favorite when not exists`() = runTest {
        // Given
        val gif = Gif(id = "new-gif", title = "New GIF", username = "user")
        coEvery { favoriteGifDao.exists(gif.id) } returns false
        coEvery { favoriteGifDao.upsert(any()) } returns Unit

        // When
        val result = repository.toggleFavorite(gif)

        // Then
        assertTrue(result)
        coVerify {
            favoriteGifDao.upsert(
                match {
                    it.id == gif.id &&
                    it.title == gif.title &&
                    it.username == gif.username
                }
            )
        }
        coVerify(exactly = 0) { favoriteGifDao.deleteById(any()) }
    }

    @Test
    fun `toggleFavorite removes favorite when exists`() = runTest {
        // Given
        val gif = Gif(id = "existing-gif", title = "Existing GIF", username = "user")
        coEvery { favoriteGifDao.exists(gif.id) } returns true
        coEvery { favoriteGifDao.deleteById(gif.id) } returns Unit

        // When
        val result = repository.toggleFavorite(gif)

        // Then
        assertFalse(result)
        coVerify { favoriteGifDao.deleteById(gif.id) }
        coVerify(exactly = 0) { favoriteGifDao.upsert(any()) }
    }

    @Test
    fun `isFavorite returns true when gif exists`() = runTest {
        // Given
        val gifId = "favorite-id"
        coEvery { favoriteGifDao.exists(gifId) } returns true

        // When
        val result = repository.isFavorite(gifId)

        // Then
        assertTrue(result)
    }

    @Test
    fun `isFavorite returns false when gif does not exist`() = runTest {
        // Given
        val gifId = "not-favorite-id"
        coEvery { favoriteGifDao.exists(gifId) } returns false

        // When
        val result = repository.isFavorite(gifId)

        // Then
        assertFalse(result)
    }
}

