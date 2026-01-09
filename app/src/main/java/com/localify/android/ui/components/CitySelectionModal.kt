package com.localify.android.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.localify.android.data.network.CityResponse
import com.localify.android.data.network.NetworkModule
import com.localify.android.data.network.UserCity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun CitySelectionModal(
    isVisible: Boolean,
    currentCity: String = "Ithaca, NY",
    currentRadius: Int = 50,
    onDismiss: () -> Unit,
    onCitySelected: (CityResponse, Int) -> Unit
) {
    var radius by remember { mutableStateOf(currentRadius) }
    var showCitySearch by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var isSearching by remember { mutableStateOf(false) }
    var searchError by remember { mutableStateOf<String?>(null) }
    var searchResults by remember { mutableStateOf<List<CityResponse>>(emptyList()) }
    var suggestedCities by remember { mutableStateOf<List<CityResponse>>(emptyList()) }
    var isLoadingSuggestions by remember { mutableStateOf(false) }
    var suggestionError by remember { mutableStateOf<String?>(null) }
    var userCities by remember { mutableStateOf<List<UserCityRow>>(emptyList()) }
    var currentUserCityId by remember { mutableStateOf<String?>(null) }
    var isLoadingUserCities by remember { mutableStateOf(false) }
    var userCitiesError by remember { mutableStateOf<String?>(null) }
    var refreshUserCitiesKey by remember { mutableStateOf(0) }
    val apiService = remember { NetworkModule.apiService }

    val selectedRow = remember(userCities, currentUserCityId) {
        userCities.firstOrNull { it.id == currentUserCityId }
    }

    LaunchedEffect(currentRadius) {
        radius = currentRadius
    }

    LaunchedEffect(selectedRow?.id, selectedRow?.radiusMiles) {
        val r = selectedRow?.radiusMiles
        if (r != null) radius = r
    }

    LaunchedEffect(isVisible, refreshUserCitiesKey) {
        if (!isVisible) return@LaunchedEffect
        isLoadingUserCities = true
        userCitiesError = null
        try {
            val response = withContext(Dispatchers.IO) { apiService.getUserCities() }
            if (response.isSuccessful) {
                val body = response.body()
                val current = body?.current
                val others = body?.others.orEmpty()
                currentUserCityId = current?.id

                val rows = buildList {
                    if (current != null) add(current.toRow())
                    others.forEach { add(it.toRow()) }
                }
                userCities = rows
            } else {
                userCitiesError = "Failed to load your cities (${response.code()})"
            }
        } catch (e: Exception) {
            userCitiesError = e.message ?: "Failed to load your cities"
        } finally {
            isLoadingUserCities = false
        }
    }

    LaunchedEffect(showCitySearch) {
        if (!showCitySearch) return@LaunchedEffect
        if (suggestedCities.isNotEmpty()) return@LaunchedEffect

        isLoadingSuggestions = true
        suggestionError = null
        try {
            val response = withContext(Dispatchers.IO) {
                apiService.getUserNearestCities()
            }
            if (response.isSuccessful) {
                val cities = response.body()?.cities.orEmpty()
                suggestedCities = cities.map {
                    val parts = it.name.split(",")
                    val cityName = parts.firstOrNull()?.trim().orEmpty().ifBlank { it.name }
                    val state = parts.getOrNull(1)?.trim()
                    CityResponse(
                        id = it.id,
                        name = cityName,
                        state = state,
                        country = it.countryCode ?: "",
                        latitude = it.latitude,
                        longitude = it.longitude
                    )
                }
            } else {
                suggestionError = "Failed to load nearby cities (${response.code()})"
            }
        } catch (e: Exception) {
            suggestionError = e.message ?: "Failed to load nearby cities"
        } finally {
            isLoadingSuggestions = false
        }
    }

    LaunchedEffect(showCitySearch, searchQuery) {
        if (!showCitySearch) return@LaunchedEffect
        if (searchQuery.length < 2) {
            searchResults = emptyList()
            searchError = null
            return@LaunchedEffect
        }

        isSearching = true
        searchError = null
        try {
            val response = withContext(Dispatchers.IO) {
                apiService.searchCities(searchQuery, limit = 20)
            }
            if (response.isSuccessful) {
                searchResults = response.body().orEmpty()
            } else {
                val errorBody = response.errorBody()?.string()
                searchError = "Failed to search cities (${response.code()})"
                searchResults = emptyList()
            }
        } catch (e: Exception) {
            searchError = e.message ?: "Failed to search cities"
            searchResults = emptyList()
        } finally {
            isSearching = false
        }
    }
    
    if (isVisible) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.8f))
                .clickable(enabled = !showCitySearch) { onDismiss() },
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .background(Color.Black, RoundedCornerShape(16.dp))
                    .padding(24.dp)
                    .clickable(enabled = false) { /* Prevent dismiss when clicking content */ }
            ) {
                    // Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Choose City",
                            color = Color.White,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                        
                        TextButton(onClick = onDismiss) {
                            Text(
                                text = "Done",
                                color = Color(0xFF4A90E2),
                                fontSize = 16.sp
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Current city with radius
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Color(0xFFE91E63),
                                RoundedCornerShape(12.dp)
                            )
                            .padding(16.dp)
                    ) {
                        Column {
                            Text(
                                text = selectedRow?.displayName ?: currentCity,
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Radius: $radius miles",
                                    color = Color.White,
                                    fontSize = 14.sp
                                )
                                
                                Spacer(modifier = Modifier.weight(1f))
                                
                                // Radius slider
                                Slider(
                                    value = radius.toFloat(),
                                    onValueChange = { radius = it.toInt() },
                                    valueRange = 5f..100f,
                                    modifier = Modifier.width(120.dp),
                                    colors = SliderDefaults.colors(
                                        thumbColor = Color.White,
                                        activeTrackColor = Color.White,
                                        inactiveTrackColor = Color.White.copy(alpha = 0.3f)
                                    )
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                // Add City button
                Button(
                    onClick = { 
                        showCitySearch = true
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4A90E2)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Add",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Add City",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Your Cities",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))

                when {
                    isLoadingUserCities -> {
                        CircularProgressIndicator(color = Color(0xFFE91E63))
                    }
                    userCitiesError != null -> {
                        Text(
                            text = userCitiesError ?: "",
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                    }
                    userCities.isEmpty() -> {
                        Text(
                            text = "No saved cities yet.",
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                    }
                    else -> {
                        LazyColumn(
                            modifier = Modifier.heightIn(max = 240.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(userCities) { row ->
                                val isCurrent = row.id == currentUserCityId
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            radius = row.radiusMiles
                                            onCitySelected(
                                                CityResponse(
                                                    id = row.id,
                                                    name = row.cityName,
                                                    state = row.state,
                                                    country = "",
                                                    latitude = 0.0,
                                                    longitude = 0.0
                                                ),
                                                row.radiusMiles
                                            )
                                            refreshUserCitiesKey++
                                        },
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (isCurrent) Color(0xFF2A2A2A) else Color(0xFF333333)
                                    )
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Text(
                                            text = row.displayName,
                                            color = Color.White,
                                            fontSize = 16.sp,
                                            fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "Radius: ${row.radiusMiles} miles",
                                            color = Color.White.copy(alpha = 0.7f),
                                            fontSize = 12.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    // City Search Dialog
    if (showCitySearch) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.9f)),
            contentAlignment = Alignment.Center
        ) {
            // Background click area to close modal
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable { showCitySearch = false }
            )
            
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .background(Color.Black, RoundedCornerShape(16.dp))
                    .padding(24.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Search Cities",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    
                    TextButton(onClick = { showCitySearch = false }) {
                        Text(
                            text = "Cancel",
                            color = Color(0xFF4A90E2),
                            fontSize = 16.sp
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Search field
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { newValue -> 
                        searchQuery = newValue
                    },
                    label = { Text("Enter city name", color = Color.Gray) },
                    placeholder = { Text("Type to search...", color = Color.Gray) },
                    modifier = Modifier
                        .fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFE91E63),
                        unfocusedBorderColor = Color.Gray,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        cursorColor = Color.White,
                        focusedLabelColor = Color(0xFFE91E63),
                        unfocusedLabelColor = Color.Gray
                    )
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = if (searchQuery.length < 2) "Nearby Cities" else "Search Results (${searchResults.size})",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(12.dp))

                if (searchQuery.length < 2) {
                    when {
                        isLoadingSuggestions -> {
                            CircularProgressIndicator(color = Color(0xFFE91E63))
                        }
                        suggestionError != null -> {
                            Text(
                                text = suggestionError ?: "",
                                color = Color.Gray,
                                modifier = Modifier.padding(16.dp),
                                fontSize = 14.sp
                            )
                        }
                        suggestedCities.isEmpty() -> {
                            Text(
                                text = "No nearby cities available",
                                color = Color.Gray,
                                modifier = Modifier.padding(16.dp),
                                fontSize = 14.sp
                            )
                        }
                        else -> {
                            suggestedCities.take(20).forEach { city ->
                                val cityLabel = listOfNotNull(city.name, city.state).joinToString(", ")
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                        .clickable {
                                            onCitySelected(city, radius)
                                            showCitySearch = false
                                            searchQuery = ""
                                            refreshUserCitiesKey++
                                        },
                                    colors = CardDefaults.cardColors(
                                        containerColor = Color(0xFF333333)
                                    )
                                ) {
                                    Text(
                                        text = cityLabel,
                                        color = Color.White,
                                        modifier = Modifier.padding(16.dp),
                                        fontSize = 16.sp
                                    )
                                }
                            }
                        }
                    }
                } else if (isSearching) {
                    CircularProgressIndicator(color = Color(0xFFE91E63))
                } else if (searchError != null) {
                    Text(
                        text = searchError ?: "",
                        color = Color.Gray,
                        modifier = Modifier.padding(16.dp),
                        fontSize = 14.sp
                    )
                } else if (searchResults.isEmpty() && searchQuery.length >= 2) {
                    Text(
                        text = "No cities found matching \"$searchQuery\"",
                        color = Color.Gray,
                        modifier = Modifier.padding(16.dp),
                        fontSize = 14.sp
                    )
                } else {
                    searchResults.forEach { city ->
                        val cityLabel = listOfNotNull(city.name, city.state).joinToString(", ")
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable {
                                    onCitySelected(city, radius)
                                    showCitySearch = false
                                    searchQuery = "" // Reset search
                                    refreshUserCitiesKey++
                                },
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFF333333)
                            )
                        ) {
                            Text(
                                text = cityLabel,
                                color = Color.White,
                                modifier = Modifier.padding(16.dp),
                                fontSize = 16.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

private data class UserCityRow(
    val id: String,
    val cityName: String,
    val state: String?,
    val displayName: String,
    val radiusMiles: Int
)

private fun UserCity.toRow(): UserCityRow {
    val parts = name.split(",")
    val cityName = parts.firstOrNull()?.trim().orEmpty().ifBlank { name }
    val state = parts.getOrNull(1)?.trim()
    val displayName = listOfNotNull(cityName, state).joinToString(", ")
    return UserCityRow(
        id = id,
        cityName = cityName,
        state = state,
        displayName = displayName,
        radiusMiles = radius.toInt()
    )
}
