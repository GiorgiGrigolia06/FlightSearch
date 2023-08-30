package com.example.flightsearch.data

import kotlinx.coroutines.flow.Flow

interface FlightSearchRepository {
    fun getAutocompleteSuggestions(input: String): Flow<List<IataAndName>>
    fun getPossibleFlights(name: String, iataCode: String): Flow<List<IataAndName>>
    suspend fun insertFavoriteItem(favorite: Favorite)
    fun getAllFavorites(): Flow<List<Favorite>>

    suspend fun deleteFavorite(departureCode: String, destinationCode: String)
}