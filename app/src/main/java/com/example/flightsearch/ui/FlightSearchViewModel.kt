package com.example.flightsearch.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flightsearch.data.FlightSearchRepository
import com.example.flightsearch.data.IataAndName
import com.example.flightsearch.data.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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
            userPreferencesRepository.userInput.collect { savedUserInput ->
                _uiState.update {
                    it.copy(
                        userInput = savedUserInput
                    )
                }
            }
        }
    }

    fun onClearClick() {
        _uiState.update {
            it.copy(
                userInput = ""
            )
        }
    }


    fun updateUserInput(input: String) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    userInput = input,
                    isAirportSelected = false
                )
            }
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

    fun retrieveAutocompleteSuggestions(): Flow<List<IataAndName>> =
        flightSearchRepository.getAutocompleteSuggestions(_uiState.value.userInput)


    fun retrievePossibleFlights(selectedAirport: IataAndName): Flow<List<IataAndName>> {
        return flightSearchRepository.getPossibleFlights(selectedAirport.iataCode, selectedAirport.name)
    }

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


