package com.example.flightsearch.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.flightsearch.data.FlightSearchRepository
import com.example.flightsearch.data.IataAndName
import kotlinx.coroutines.flow.Flow

class FlightSearchViewModel(private val flightSearchRepository: FlightSearchRepository): ViewModel() {
    var userInput: String by mutableStateOf("")
        private set

    var selectedAirport: IataAndName by mutableStateOf(IataAndName("", ""))
        private set

    var isAirportSelected: Boolean by mutableStateOf(false)
        private set

    fun updateUserInput(input: String) {
        userInput = input
        isAirportSelected = false
    }

    fun updateSelectedAirport(updatedSelectedAirport: IataAndName) {
        selectedAirport = updatedSelectedAirport
        isAirportSelected = true
    }

    fun retrieveAutocompleteSuggestions(): Flow<List<IataAndName>> =
        flightSearchRepository.getAutocompleteSuggestions(userInput)

    fun retrievePossibleFlights(selectedAirport: IataAndName): Flow<List<IataAndName>> =
        flightSearchRepository.getPossibleFlights(selectedAirport.iataCode, selectedAirport.name)
}

