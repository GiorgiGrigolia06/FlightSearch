package com.example.flightsearch.data

import kotlinx.coroutines.flow.Flow

interface FlightSearchRepository {
    fun getAutocompleteSuggestions(input: String): Flow<List<IataAndName>>
}