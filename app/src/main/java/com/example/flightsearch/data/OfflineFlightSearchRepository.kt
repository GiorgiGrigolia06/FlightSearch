package com.example.flightsearch.data

import kotlinx.coroutines.flow.Flow

class OfflineFlightSearchRepository(private val airportDao: AirportDao): FlightSearchRepository {
    override fun getAutocompleteSuggestions(input: String): Flow<List<IataAndName>> =
        airportDao.retrieveAutocompleteSuggestions(input)

    override fun getPossibleFlights(name: String, iataCode: String): Flow<List<IataAndName>> =
        airportDao.retrievePossibleFlights(name, iataCode)

    override suspend fun insertFavoriteItem(favorite: Favorite) =
        airportDao.insertFavorite(favorite)

    override suspend fun deleteFavorite(departureCode: String, destinationCode: String) =
        airportDao.deleteFavorite(departureCode, destinationCode)

    override suspend fun deleteAllFavorites() = airportDao.deleteAllFavorites()

    override fun getAllFavorites(): Flow<List<Favorite>> =
        airportDao.retrieveAllFavorites()
}