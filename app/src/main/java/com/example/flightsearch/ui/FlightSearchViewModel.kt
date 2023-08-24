package com.example.flightsearch.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.flightsearch.data.Airport
import com.example.flightsearch.data.FlightSearchRepository
import com.example.flightsearch.data.IataAndName
import kotlinx.coroutines.flow.Flow

class FlightSearchViewModel(private val flightSearchRepository: FlightSearchRepository): ViewModel() {
    var userInput: String by mutableStateOf("")
        private set

    fun updateUserInput(input: String) {
        userInput = input
    }

    fun retrieveAutocompleteSuggestions(): Flow<List<IataAndName>> =
        flightSearchRepository.getAutocompleteSuggestions(userInput)
}

