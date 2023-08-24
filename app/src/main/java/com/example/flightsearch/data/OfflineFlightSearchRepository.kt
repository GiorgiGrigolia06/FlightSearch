package com.example.flightsearch.data

import kotlinx.coroutines.flow.Flow

class OfflineFlightSearchRepository(private val airportDao: AirportDao): FlightSearchRepository {
    override fun getAutocompleteSuggestions(input: String): Flow<List<IataAndName>> =
        airportDao.retrieveAutocompleteSuggestions(input)
}