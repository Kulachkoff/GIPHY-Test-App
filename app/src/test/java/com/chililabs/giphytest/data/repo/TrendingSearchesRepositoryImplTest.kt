package com.chililabs.giphytest.data.repo

import com.chililabs.giphytest.data.remote.api.GiphyApi
import com.chililabs.giphytest.data.remote.model.AutocompleteItem
import com.chililabs.giphytest.data.remote.model.AutocompleteResponse
import com.chililabs.giphytest.data.remote.model.TrendingSearchesResponse
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class TrendingSearchesRepositoryImplTest {

    private lateinit var giphyApi: GiphyApi
    private lateinit var repository: TrendingSearchesRepositoryImpl

    @Before
    fun setup() {
        giphyApi = mockk()
        repository = TrendingSearchesRepositoryImpl(giphyApi)
    }

    @Test
    fun `getTrendingSearches returns list when API call succeeds`() = runTest {
        // Given
        val trendingSearches = listOf("cats", "dogs", "funny", "memes", "reactions")
        val response = TrendingSearchesResponse(trendingSearches)
        coEvery { giphyApi.getTrendingSearches() } returns response

        // When
        val result = repository.getTrendingSearches().first()

        // Then
        assertEquals(trendingSearches, result)
    }

    @Test
    fun `getTrendingSearches returns empty list when API returns null`() = runTest {
        // Given
        coEvery { giphyApi.getTrendingSearches() } returns null

        // When
        val result = repository.getTrendingSearches().first()

        // Then
        assertTrue(result.isEmpty())
    }

    @Test
    fun `getTrendingSearches returns empty list when API throws exception`() = runTest {
        // Given
        val exception = RuntimeException("Network error")
        coEvery { giphyApi.getTrendingSearches() } throws exception

        // When
        val result = repository.getTrendingSearches().first()

        // Then
        assertTrue(result.isEmpty())
    }

    @Test
    fun `getAutocompleteSuggestions returns mapped names when API call succeeds`() = runTest {
        // Given
        val query = "cat"
        val autocompleteItems = listOf(
            AutocompleteItem("cats"),
            AutocompleteItem("cat memes"),
            AutocompleteItem("cat gifs")
        )
        val response = AutocompleteResponse(autocompleteItems)
        coEvery { giphyApi.getAutocompleteSuggestions(query) } returns response

        // When
        val result = repository.getAutocompleteSuggestions(query).first()

        // Then
        assertEquals(listOf("cats", "cat memes", "cat gifs"), result)
    }

    @Test
    fun `getAutocompleteSuggestions returns empty list when query is blank`() = runTest {
        // Given
        val query = ""

        // When
        val result = repository.getAutocompleteSuggestions(query).first()

        // Then
        assertTrue(result.isEmpty())
    }

    @Test
    fun `getAutocompleteSuggestions returns empty list when API returns null`() = runTest {
        // Given
        val query = "test"
        coEvery { giphyApi.getAutocompleteSuggestions(query) } returns null

        // When
        val result = repository.getAutocompleteSuggestions(query).first()

        // Then
        assertTrue(result.isEmpty())
    }

    @Test
    fun `getAutocompleteSuggestions returns empty list when API throws exception`() = runTest {
        // Given
        val query = "test"
        val exception = RuntimeException("Network error")
        coEvery { giphyApi.getAutocompleteSuggestions(query) } throws exception

        // When
        val result = repository.getAutocompleteSuggestions(query).first()

        // Then
        assertTrue(result.isEmpty())
    }

    @Test
    fun `getAutocompleteSuggestions returns empty list when response data is empty`() = runTest {
        // Given
        val query = "test"
        val response = AutocompleteResponse(emptyList())
        coEvery { giphyApi.getAutocompleteSuggestions(query) } returns response

        // When
        val result = repository.getAutocompleteSuggestions(query).first()

        // Then
        assertTrue(result.isEmpty())
    }
}

