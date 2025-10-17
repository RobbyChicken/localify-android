package com.localify.android.ui.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import com.localify.android.data.local.UserPreferences

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScreen(
    onComplete: () -> Unit,
    onCancel: () -> Unit
) {
    val context = LocalContext.current
    val userPreferences = remember { UserPreferences(context) }
    
    var currentStep by remember { mutableStateOf(0) }
    val totalSteps = 4
    
    // State for each step
    var selectedCity by remember { mutableStateOf("") }
    var radius by remember { mutableStateOf(10.0) }
    var selectedGenres by remember { mutableStateOf(setOf<String>()) }
    var selectedArtists by remember { mutableStateOf(setOf<String>()) }
    
    // Loading state for completion
    var isCompletingOnboarding by remember { mutableStateOf(false) }
    var completionError by remember { mutableStateOf<String?>(null) }
    
    // Determine if next/done button should be enabled
    val isNextEnabled = when (currentStep) {
        0 -> selectedCity.isNotEmpty()
        1 -> true // Radius is always valid
        2 -> selectedGenres.size >= 3
        3 -> selectedArtists.size >= 5
        else -> false
    }
    
    // Navigation title based on current step
    val navTitle = when (currentStep) {
        0 -> "Search for City"
        1 -> "Select Radius"
        2 -> "Select Genres"
        3 -> "Select Artists"
        else -> "Onboarding"
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Top App Bar
        TopAppBar(
            title = {
                Text(
                    text = navTitle,
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            navigationIcon = {
                if (currentStep == 0) {
                    TextButton(onClick = onCancel) {
                        Text(
                            text = "Cancel",
                            color = Color(0xFF4A90E2),
                            fontSize = 16.sp
                        )
                    }
                } else {
                    IconButton(onClick = {
                        if (currentStep > 0) {
                            currentStep--
                        }
                    }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color(0xFF4A90E2)
                        )
                    }
                }
            },
            actions = {
                if (currentStep < totalSteps - 1) {
                    TextButton(
                        onClick = {
                            if (currentStep < totalSteps - 1) {
                                currentStep++
                            }
                        },
                        enabled = isNextEnabled
                    ) {
                        Text(
                            text = "Next",
                            color = if (isNextEnabled) Color(0xFF4A90E2) else Color.Gray,
                            fontSize = 16.sp
                        )
                    }
                } else {
                    TextButton(
                        onClick = {
                            // Complete onboarding and save all data
                            isCompletingOnboarding = true
                            
                            // Save all onboarding data to UserPreferences
                            userPreferences.setSelectedCity(selectedCity)
                            userPreferences.setSelectedRadius(radius.toFloat())
                            userPreferences.setSelectedGenres(selectedGenres)
                            userPreferences.setSelectedArtists(selectedArtists)
                            
                            onComplete()
                        },
                        enabled = isNextEnabled && !isCompletingOnboarding
                    ) {
                        if (isCompletingOnboarding) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = Color(0xFF4A90E2),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = "Done",
                                color = if (isNextEnabled) Color(0xFF4A90E2) else Color.Gray,
                                fontSize = 16.sp
                            )
                        }
                    }
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Black
            )
        )
        
        // Main content
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            when (currentStep) {
                0 -> CitySearchContent(
                    selectedCity = selectedCity,
                    onCitySelected = { city ->
                        selectedCity = city
                        // Auto-advance to next step when city is selected
                        currentStep++
                    }
                )
                1 -> RadiusSelectionContent(
                    selectedCity = selectedCity,
                    radius = radius,
                    onRadiusChanged = { newRadius ->
                        radius = newRadius
                    }
                )
                2 -> GenreSelectionContent(
                    selectedGenres = selectedGenres,
                    onGenresChanged = { genres ->
                        selectedGenres = genres
                    }
                )
                3 -> ArtistSelectionContent(
                    selectedArtists = selectedArtists,
                    onArtistsChanged = { artists ->
                        selectedArtists = artists
                    }
                )
            }
            
            // Loading overlay
            if (isCompletingOnboarding) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.7f)),
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        modifier = Modifier.padding(32.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF1A1A1A)
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator(
                                color = Color(0xFFE91E63),
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Completing Onboarding...",
                                color = Color.White,
                                fontSize = 16.sp
                            )
                        }
                    }
                }
            }
        }
        
        // Page indicators
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(totalSteps) { index ->
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .background(
                            if (index == currentStep) Color(0xFF4A90E2) else Color.Gray.copy(alpha = 0.5f),
                            CircleShape
                        )
                )
                if (index < totalSteps - 1) {
                    Spacer(modifier = Modifier.width(8.dp))
                }
            }
        }
    }
    
    // Error dialog
    completionError?.let { error ->
        AlertDialog(
            onDismissRequest = { completionError = null },
            title = { Text("Error") },
            text = { Text(error) },
            confirmButton = {
                TextButton(onClick = { completionError = null }) {
                    Text("OK")
                }
            }
        )
    }
}

@Composable
private fun CitySearchContent(
    selectedCity: String,
    onCitySelected: (String) -> Unit
) {
    var searchText by remember { mutableStateOf("") }
    
    // Mock city data that would normally come from an API
    val allCities = listOf(
        "Ithan, PA",
        "Ithaca, NY", 
        "Ithaca, MI",
        "Ithaca, NE",
        "Smith, NV",
        "Faith, NC",
        "Faith, SD",
        "Erith, GB",
        "Edith, OK",
        "East Ithaca, NY",
        "Munith, MI",
        "St. Vith, BE",
        "Nesmith, SC",
        "Minnith, MO",
        "Denith, CR"
    )
    
    // Filter cities based on search text
    val filteredCities = remember(searchText) {
        if (searchText.length >= 2) {
            allCities.filter { city ->
                city.lowercase().contains(searchText.lowercase())
            }
        } else {
            emptyList()
        }
    }
    
    CitySearchScreen(
        searchText = searchText,
        onSearchTextChanged = { searchText = it },
        filteredCities = filteredCities,
        selectedCity = selectedCity,
        onCitySelected = onCitySelected
    )
}

@Composable
private fun RadiusSelectionContent(
    selectedCity: String,
    radius: Double,
    onRadiusChanged: (Double) -> Unit
) {
    RadiusSelectionScreen(
        selectedCity = selectedCity,
        radius = radius,
        onRadiusChanged = onRadiusChanged
    )
}

@Composable
private fun GenreSelectionContent(
    selectedGenres: Set<String>,
    onGenresChanged: (Set<String>) -> Unit
) {
    GenreSelectionScreen(
        selectedGenres = selectedGenres,
        onGenresChanged = onGenresChanged
    )
}

@Composable
private fun ArtistSelectionContent(
    selectedArtists: Set<String>,
    onArtistsChanged: (Set<String>) -> Unit
) {
    ArtistSelectionScreen(
        selectedArtists = selectedArtists,
        onArtistsChanged = onArtistsChanged
    )
}
