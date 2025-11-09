package com.chililabs.giphytest.data.repo

import com.chililabs.giphytest.data.mapper.toDomain
import com.chililabs.giphytest.data.remote.api.GiphyApi
import com.chililabs.giphytest.data.remote.model.GifDto
import com.chililabs.giphytest.data.remote.model.SingleGifResponse
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class GifsRepositoryImplTest {

    private lateinit var giphyApi: GiphyApi
    private lateinit var repository: GifsRepositoryImpl

    @Before
    fun setup() {
        giphyApi = mockk()
        repository = GifsRepositoryImpl(giphyApi)
    }

    @Test
    fun `getById returns gif when API call succeeds`() = runTest {
        // Given
        val gifId = "test-gif-id"
        val gifDto = GifDto(
            id = gifId,
            title = "Test GIF",
            username = "testuser",
            url = "https://test.com/gif.gif"
        )
        val response = SingleGifResponse(gifDto)
        coEvery { giphyApi.getById(gifId) } returns response

        // When
        val result = repository.getById(gifId).first()

        // Then
        assertEquals(gifDto.toDomain(), result)
    }

    @Test
    fun `getById returns null when API returns null`() = runTest {
        // Given
        val gifId = "non-existent-id"
        coEvery { giphyApi.getById(gifId) } returns null

        // When
        val result = repository.getById(gifId).first()

        // Then
        assertNull(result)
    }

    @Test
    fun `getById propagates exception when API throws`() = runTest {
        // Given
        val gifId = "test-id"
        val exception = RuntimeException("Network error")
        coEvery { giphyApi.getById(gifId) } throws exception

        // When/Then
        try {
            repository.getById(gifId).first()
            assert(false) { "Expected exception to be thrown" }
        } catch (e: RuntimeException) {
            assertEquals(exception, e)
        }
    }
}

