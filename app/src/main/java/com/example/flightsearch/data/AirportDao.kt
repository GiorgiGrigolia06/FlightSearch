package com.example.flightsearch.data

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AirportDao {
    @Query("SELECT iata_code, name FROM airport WHERE iata_code LIKE '%' || :input || '%' OR name LIKE '%' || :input || '%' ORDER BY passengers DESC")
    fun retrieveAutocompleteSuggestions(input: String): Flow<List<IataAndName>>
}


/**
 * Assume that every airport has flights to every other airport in the database (except for itself).
 * When no text is in the search box, display a list of favorite flights, showing the
 * departure and destination. As the favorite table only includes columns for the airport codes,
 * you're not expected to show the airport names in this list.
 * Perform all database querying with SQL and Room APIs. The whole point is to NOT load your entire database into memory at once, only to retrieve the required data as needed.
 */