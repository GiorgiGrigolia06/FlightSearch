package com.example.flightsearch.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flightsearch.data.Favorite
import com.example.flightsearch.data.FlightSearchRepository
import com.example.flightsearch.data.IataAndName
import com.example.flightsearch.data.UserPreferencesRepository
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class FlightSearchUIState(
    val userInput: String = "",
    val selectedAirport: IataAndName = IataAndName(iataCode = "", name =  ""),
    val isAirportSelected: Boolean = false,
    val flightSavedStates: MutableMap<Favorite, Boolean> = mutableMapOf(),
)

@OptIn(FlowPreview::class)
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

    // Updates user input and saves it in user preference repository
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

    // Updates selected airport so the function then can return possible flights list based on it
    fun updateSelectedAirport(updatedSelectedAirport: IataAndName) {
        _uiState.update {
            it.copy(
                selectedAirport = updatedSelectedAirport,
                isAirportSelected = true
            )
        }
    }

    // Retrieves autocomplete suggestions as the user fills the search bar
    fun retrieveAutocompleteSuggestions(): Flow<List<IataAndName>> {
        return if (_uiState.value.userInput.isNotBlank())
            flightSearchRepository.getAutocompleteSuggestions(_uiState.value.userInput.trim()).debounce(500L)
        else
            emptyFlow()
    }

    // Retrieves possible flights list after the airport is selected
    fun retrievePossibleFlights(selectedAirport: IataAndName): Flow<List<IataAndName>> =
        flightSearchRepository.getPossibleFlights(selectedAirport.iataCode, selectedAirport.name)

    // Marks flight as saved by switching boolean value to true
    private fun updateFlightSavedState(favorite: Favorite, newState: Boolean) {
        _uiState.update {
            it.copy(
                flightSavedStates = _uiState.value.flightSavedStates.toMutableMap().apply {
                    this[favorite] = newState
                }
            )
        }
    }

    // Saves item in the local database and marks it as saved
    fun insertItem(favorite: Favorite) {
        updateFlightSavedState(favorite, true)

        viewModelScope.launch {
            flightSearchRepository.insertFavoriteItem(favorite)
        }
    }

    // Deletes item from the local database and marks it as deleted
    fun deleteItem(favorite: Favorite) {
        if (_uiState.value.flightSavedStates[favorite] == true)
            updateFlightSavedState(favorite, false)

        viewModelScope.launch {
            flightSearchRepository.deleteFavorite(favorite.departureCode, favorite.destinationCode)
        }
    }

    // Checks if flight is saved or not
    fun isFlightSaved(favorite: Favorite): Boolean {
        return _uiState.value.flightSavedStates[favorite] == true
    }

    // Returns a list of favorite (saved) items from the database
    fun getAllFavorites(): Flow<List<Favorite>> =
        flightSearchRepository.getAllFavorites()

    // Clears user input from the search bar and saves it to the preference repository
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

    // Checks saved items and if the same item exists in possible flights list, marks it as saved
    fun syncFavoritesWithFlights(favorites: List<Favorite>, selectedAirport: IataAndName, destinationAirports: List<IataAndName>) {
        for (favorite in favorites)
            for (destinationAirport in destinationAirports) {
                if (favorite.departureCode == selectedAirport.iataCode && favorite.destinationCode == destinationAirport.iataCode)
                    updateFlightSavedState(Favorite(departureCode = selectedAirport.iataCode, destinationCode = destinationAirport.iataCode), true)
            }
    }
}