package com.example.flightsearch.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AirportDao {
    @Query("SELECT iata_code, name FROM airport WHERE iata_code LIKE '%' || :input || '%' OR name LIKE '%' || :input || '%' ORDER BY passengers DESC")
    fun retrieveAutocompleteSuggestions(input: String): Flow<List<IataAndName>>

    @Query("SELECT iata_code, name FROM airport WHERE iata_code NOT LIKE '%' || :iataCode || '%' AND name NOT LIKE '%' || :name || '%' ORDER BY passengers DESC")
    fun retrievePossibleFlights(iataCode: String, name: String): Flow<List<IataAndName>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertFavorite(favorite: Favorite)

    @Query("DELETE FROM favorite WHERE departure_code = :departureCode AND destination_code = :destinationCode")
    suspend fun deleteFavorite(departureCode: String, destinationCode: String)

    @Query("DELETE FROM favorite")
    suspend fun deleteAllFavorites()

    @Query("SELECT * from favorite")
    fun retrieveAllFavorites(): Flow<List<Favorite>>
}