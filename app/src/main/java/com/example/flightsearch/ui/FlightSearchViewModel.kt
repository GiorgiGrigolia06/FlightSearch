package com.example.flightsearch.ui

import androidx.lifecycle.ViewModel
import com.example.flightsearch.data.FlightSearchRepository
import com.example.flightsearch.data.IataAndName
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

data class FlightSearchUIState(
    val userInput: String = "",
    val selectedAirport: IataAndName = IataAndName("", ""),
    val isAirportSelected: Boolean = false,
    val flightSavedStates: MutableMap<String, Boolean> = mutableMapOf()
)

class FlightSearchViewModel(private val flightSearchRepository: FlightSearchRepository): ViewModel() {

    private val _uiState = MutableStateFlow(
        FlightSearchUIState()
    )

    val uiState: StateFlow<FlightSearchUIState> = _uiState

    fun updateUserInput(input: String) {
        _uiState.update {
            it.copy(
                userInput = input,
                isAirportSelected = false
            )
        }
    }

    fun updateSelectedAirport(updatedSelectedAirport: IataAndName) {
        _uiState.update {
            it.copy(
                selectedAirport = updatedSelectedAirport,
                isAirportSelected = true
            )
        }
    }

    fun retrieveAutocompleteSuggestions(): Flow<List<IataAndName>> =
        flightSearchRepository.getAutocompleteSuggestions(_uiState.value.userInput)

    fun retrievePossibleFlights(selectedAirport: IataAndName): Flow<List<IataAndName>> =
        flightSearchRepository.getPossibleFlights(selectedAirport.iataCode, selectedAirport.name)

    fun saveFlight(airportCodes: String) {
        _uiState.update {
            it.copy(
                flightSavedStates = _uiState.value.flightSavedStates.toMutableMap().apply {
                    this[airportCodes] = true
                }
            )
        }
    }

    fun deleteFlight(airportCodes: String) {
        if (_uiState.value.flightSavedStates[airportCodes] == true) {
            _uiState.update {
                it.copy(
                    flightSavedStates = _uiState.value.flightSavedStates.toMutableMap().apply {
                        this[airportCodes] = false
                    }
                )
            }
        }
    }

    fun isFlightSaved(airportCodes: String): Boolean {
        return _uiState.value.flightSavedStates[airportCodes] == true
    }
}


