package com.example.flightsearch.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flightsearch.data.FlightSearchRepository
import com.example.flightsearch.data.IataAndName
import com.example.flightsearch.data.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class FlightSearchUIState(
    val userInput: String = "",
    val selectedAirport: IataAndName = IataAndName("", ""),
    val isAirportSelected: Boolean = false,
    val flightSavedStates: MutableMap<String, Boolean> = mutableMapOf()
)
class FlightSearchViewModel(
    private val flightSearchRepository: FlightSearchRepository,
    private val userPreferencesRepository: UserPreferencesRepository
): ViewModel() {

    private val _uiState = MutableStateFlow(
        FlightSearchUIState()
    )

    val uiState: StateFlow<FlightSearchUIState> = _uiState

    init {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    userInput = userPreferencesRepository.userInput.first()
                )
            }
        }
    }

    fun updateUserInput(input: String) {
        _uiState.update {
            it.copy(
                userInput = input,
                isAirportSelected = false
            )
        }

        viewModelScope.launch {
            userPreferencesRepository.saveUserInput(input)
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

    fun retrieveAutocompleteSuggestions(): Flow<List<IataAndName>> {
        return if (_uiState.value.userInput.isNotBlank())
            flightSearchRepository.getAutocompleteSuggestions(_uiState.value.userInput.trim())
        else
            emptyFlow()
    }

    fun retrievePossibleFlights(selectedAirport: IataAndName): Flow<List<IataAndName>> =
        flightSearchRepository.getPossibleFlights(selectedAirport.iataCode, selectedAirport.name)

    private fun updateFlightSavedState(airportCodes: String, newState: Boolean) {
        _uiState.update {
            it.copy(
                flightSavedStates = _uiState.value.flightSavedStates.toMutableMap().apply {
                    this[airportCodes] = newState
                }
            )
        }
    }

    fun saveFlight(airportCodes: String) {
        updateFlightSavedState(airportCodes, true)
    }

    fun deleteFlight(airportCodes: String) {
        if (_uiState.value.flightSavedStates[airportCodes] == true) {
            updateFlightSavedState(airportCodes, false)
        }
    }

    fun isFlightSaved(airportCodes: String): Boolean {
        return _uiState.value.flightSavedStates[airportCodes] == true
    }

    fun onClearClick() {
        _uiState.update {
            it.copy(
                userInput = ""
            )
        }

        viewModelScope.launch {
            userPreferencesRepository.saveUserInput(_uiState.value.userInput)
        }
    }
}


