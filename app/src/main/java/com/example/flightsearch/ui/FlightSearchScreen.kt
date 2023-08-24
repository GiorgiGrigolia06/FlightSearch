package com.example.flightsearch.ui

import android.annotation.SuppressLint
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.flightsearch.R
import com.example.flightsearch.data.Airport
import com.example.flightsearch.data.IataAndName
import com.example.flightsearch.ui.theme.FlightSearchTheme

@Composable
fun FlightSearchApp() {
    val viewModel: FlightSearchViewModel = viewModel(factory = AppViewModelProvider.Factory)
    val airportList by viewModel.retrieveAutocompleteSuggestions().collectAsState(emptyList())

    SearchBar(
        placeholder = R.string.search_bar_placeholder,
        value = viewModel.userInput,
        onValueChange = { viewModel.updateUserInput(it) }
    )

    if(viewModel.userInput != "") {
        AirportList(airportList = airportList)
    }
}


@Composable
fun AirportList(
    airportList: List<IataAndName>,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        items(
            items = airportList,
        ) {
            Row {
                Text(
                    text = it.iataCode
                )

                Text(
                    text = it.name
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
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.fillMaxSize()
    ){
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(stringResource(placeholder)) },
            singleLine = true,
            shape = MaterialTheme.shapes.medium,
            leadingIcon = {
                Icon(
                    painterResource(R.drawable.baseline_search_24),
                    contentDescription = null,
                )
            },
            trailingIcon =
            {
                Icon(
                    painterResource(R.drawable.baseline_keyboard_voice_24),
                    contentDescription = null
                )
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Go
            ),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                unfocusedBorderColor = Color.Transparent,
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ),
            modifier = modifier
        )
    }
}

@SuppressLint("ResourceType")
@Preview(showBackground = true)
@Composable
fun SearchBarPreviewLightTheme() {
    FlightSearchTheme(darkTheme = false) {
        SearchBar(
            placeholder = 1,
            value = "Giorgi",
            onValueChange = { },
        )
    }
}

@SuppressLint("ResourceType")
@Preview(showBackground = true)
@Composable
fun SearchBarPreviewDarkTheme() {
    FlightSearchTheme(darkTheme = true) {
        SearchBar(
            placeholder = 1,
            value = "Giorgi",
            onValueChange = { },
        )
    }
}