package com.example.flightsearch.ui

import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.flightsearch.R
import com.example.flightsearch.data.IataAndName

@Composable
fun FlightSearchApp(
    modifier: Modifier = Modifier,
    viewModel: FlightSearchViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val uiState by viewModel.uiState.collectAsState()
    val airportList by viewModel.retrieveAutocompleteSuggestions().collectAsState(emptyList())
    val destinationAirports by viewModel.retrievePossibleFlights(uiState.selectedAirport).collectAsState(emptyList())
    val focusManager = LocalFocusManager.current

    Box(
        contentAlignment = Alignment.TopCenter,
        modifier = modifier
            .padding(dimensionResource(R.dimen.main_box_padding))
            .clickable(
                interactionSource = MutableInteractionSource(),
                indication = null
            ) { focusManager.clearFocus() },
    ){
        Column {
            SearchBar(
                placeholder = R.string.search_bar_placeholder,
                value = uiState.userInput,
                onValueChange = { viewModel.updateUserInput(it) },
                onClearClick = { viewModel.onClearClick() },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.main_column_spacer)))

            if (uiState.userInput.isNotEmpty() && !uiState.isAirportSelected && uiState.userInput.isNotBlank()) {
                AutocompleteSuggestions(
                    airportList = airportList,
                    onItemSelected = { viewModel.retrievePossibleFlights(it) },
                    updateSelectedAirport = { viewModel.updateSelectedAirport(it) },
                )
            }

            if (uiState.userInput.isNotEmpty() && uiState.isAirportSelected) {
                PossibleFlights(
                    selectedAirport = uiState.selectedAirport,
                    destinationAirports = destinationAirports,
                    isFlightSaved = { viewModel.isFlightSaved(it) },
                    deleteFlight = { viewModel.deleteFlight(it) },
                    saveFlight = { viewModel.saveFlight(it) },
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(
    @StringRes placeholder: Int,
    value: String,
    onValueChange: (String) -> Unit,
    onClearClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = if (value.isBlank()) {
            { Text(text = stringResource(placeholder)) }
        } else null,
        singleLine = true,
        shape = MaterialTheme.shapes.medium,
        leadingIcon =
        {
            Icon(
                painterResource(R.drawable.baseline_search_24),
                contentDescription = null,
            )
        },
        trailingIcon = if (value.isNotBlank()) {
            {
                Icon(
                    painterResource(R.drawable.baseline_clear_24),
                    contentDescription = null,
                    modifier = Modifier.clickable { onClearClick() }
                )
            }
        } else null,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Go
        ),
        colors = TextFieldDefaults.outlinedTextFieldColors(
            unfocusedBorderColor = Color.Transparent,
            focusedBorderColor = Color.Transparent,
            containerColor = MaterialTheme.colorScheme.primaryContainer,
        ),
        modifier = modifier
    )
}

@Composable
fun AutocompleteSuggestions(
    airportList: List<IataAndName>,
    onItemSelected: (IataAndName) -> Unit,
    updateSelectedAirport: (IataAndName) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
    ) {
        items(
            items = airportList,
            key = { it.iataCode }
        ) {
            Row(
                modifier = Modifier
                    .padding(vertical = dimensionResource(R.dimen.lazy_column_row_vertical_padding))
                    .clickable {
                        onItemSelected(it)
                        updateSelectedAirport(it)
                    }
            ) {
                Text(
                    text = it.iataCode,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.widthIn(min = dimensionResource(R.dimen.iata_code_minimum_width))
                )

                Text(
                    text = it.name,
                    fontWeight = FontWeight.Light,
                )
            }
        }
    }
}

@Composable
fun PossibleFlights(
    selectedAirport: IataAndName,
    destinationAirports: List<IataAndName>,
    isFlightSaved: (String) -> Boolean,
    saveFlight: (String) -> Unit,
    deleteFlight: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column {
        Text(
            text = stringResource(R.string.flights_from, selectedAirport.iataCode),
            fontWeight = FontWeight.Bold,
            modifier = modifier.padding(bottom = dimensionResource(R.dimen.possible_flight_text_bottom_padding))
        )

        LazyColumn {
            items(
                items = destinationAirports,
                key = { it.iataCode }
            ) { destinationAirport ->
                PossibleFlightCard(
                    selectedAirport = selectedAirport,
                    destinationAirport = destinationAirport,
                    saveFlight = saveFlight,
                    isFlightSaved = isFlightSaved,
                    deleteFlight = deleteFlight,
                    modifier = Modifier.padding(vertical = dimensionResource(R.dimen.possible_flight_card_vertical_padding))
                )
            }
        }
    }
}

@Composable
fun PossibleFlightCard(
    selectedAirport: IataAndName,
    destinationAirport: IataAndName,
    saveFlight: (String) -> Unit,
    deleteFlight: (String) -> Unit,
    isFlightSaved: (String) -> Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraSmall,
        elevation = CardDefaults.cardElevation(defaultElevation = dimensionResource(R.dimen.card_default_elevation)),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.background),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(dimensionResource(R.dimen.possible_flight_card_column_padding))
            ) {
                Text(
                    text = stringResource(R.string.depart),
                    fontWeight = FontWeight.Light,
                    fontSize = dimensionResource(R.dimen.depart_font_size).value.sp
                )

                Row {
                    Text(
                        text = selectedAirport.iataCode,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.widthIn(min = dimensionResource(R.dimen.iata_code_minimum_width)),
                    )

                    Text(
                        text = selectedAirport.name
                    )
                }

                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.card_height_spacer)))

                Text(
                    text = stringResource(R.string.arrive),
                    fontWeight = FontWeight.Light,
                    fontSize = dimensionResource(R.dimen.arrive_font_size).value.sp
                )

                Row {
                    Text(
                        text = destinationAirport.iataCode,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.widthIn(min = dimensionResource(R.dimen.iata_code_minimum_width))
                    )

                    Text(
                        destinationAirport.name
                    )
                }
            }

            Image(
                painter = painterResource(R.drawable.baseline_star_24),
                contentDescription = null,
                modifier = Modifier
                    .size(dimensionResource(R.dimen.star_icon_size))
                    .padding(end = dimensionResource(R.dimen.star_icon_end_padding))
                    .clickable {
                        if (!isFlightSaved(destinationAirport.iataCode + selectedAirport.iataCode))
                            saveFlight(destinationAirport.iataCode + selectedAirport.iataCode)
                        else
                            deleteFlight(destinationAirport.iataCode + selectedAirport.iataCode)
                    },
                colorFilter = if (isFlightSaved(destinationAirport.iataCode + selectedAirport.iataCode))
                    ColorFilter.tint(MaterialTheme.colorScheme.primary)
                 else
                     ColorFilter.tint(MaterialTheme.colorScheme.outlineVariant)
            )
        }
    }
}