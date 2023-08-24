package com.example.flightsearch.ui

import android.annotation.SuppressLint
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
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
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.flightsearch.R
import com.example.flightsearch.data.IataAndName
import com.example.flightsearch.ui.theme.FlightSearchTheme

@Composable
fun FlightSearchApp(
    modifier: Modifier = Modifier
) {
    val viewModel: FlightSearchViewModel = viewModel(factory = AppViewModelProvider.Factory)
    val airportList by viewModel.retrieveAutocompleteSuggestions().collectAsState(emptyList())

    Box(
        contentAlignment = Alignment.TopCenter,
        modifier = modifier.padding(dimensionResource(R.dimen.main_box_padding))
    ){
        Column {
            SearchBar(
                placeholder = R.string.search_bar_placeholder,
                value = viewModel.userInput,
                onValueChange = { viewModel.updateUserInput(it) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.main_column_spacer)))

            if (viewModel.userInput != "") {
                AutocompleteSuggestions(
                    airportList = airportList
                )
            }
        }
    }
}

@Composable
fun AutocompleteSuggestions(
    airportList: List<IataAndName>,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
    ) {
        items(
            items = airportList,
        ) {
            Row(
                modifier = Modifier.padding(vertical = dimensionResource(R.dimen.lazy_column_row_vertical_padding))
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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(
    @StringRes placeholder: Int,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(stringResource(placeholder)) },
        singleLine = true,
        shape = MaterialTheme.shapes.medium,
        leadingIcon =
        {
            Icon(
                painterResource(R.drawable.baseline_search_24),
                contentDescription = null,)
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