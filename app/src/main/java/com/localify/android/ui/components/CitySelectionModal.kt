package com.localify.android.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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

@Composable
fun CitySelectionModal(
    isVisible: Boolean,
    currentCity: String = "Ithaca, NY",
    currentRadius: Int = 50,
    onDismiss: () -> Unit,
    onCityChange: (String) -> Unit
) {
    var radius by remember { mutableStateOf(currentRadius) }
    var showCitySearch by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    
    if (isVisible) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.8f))
                .clickable { onDismiss() },
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
                                text = currentCity,
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
                
                // All available cities
                val allCities = listOf(
                    "New York, NY", "Los Angeles, CA", "Chicago, IL", "Miami, FL",
                    "Houston, TX", "Phoenix, AZ", "Philadelphia, PA", "San Antonio, TX",
                    "San Diego, CA", "Dallas, TX", "San Jose, CA", "Austin, TX",
                    "Jacksonville, FL", "Fort Worth, TX", "Columbus, OH", "Charlotte, NC",
                    "San Francisco, CA", "Indianapolis, IN", "Seattle, WA", "Denver, CO",
                    "Washington, DC", "Boston, MA", "El Paso, TX", "Nashville, TN",
                    "Detroit, MI", "Oklahoma City, OK", "Portland, OR", "Las Vegas, NV",
                    "Memphis, TN", "Louisville, KY", "Baltimore, MD", "Milwaukee, WI",
                    "Albuquerque, NM", "Tucson, AZ", "Fresno, CA", "Sacramento, CA",
                    "Atlanta, GA", "Kansas City, MO", "Colorado Springs, CO", "Omaha, NE",
                    "Raleigh, NC", "Miami Beach, FL", "Virginia Beach, VA", "Oakland, CA",
                    "Minneapolis, MN", "Tulsa, OK", "Arlington, TX", "New Orleans, LA",
                    "Wichita, KS", "Cleveland, OH", "Tampa, FL", "Bakersfield, CA",
                    "Aurora, CO", "Honolulu, HI", "Anaheim, CA", "Santa Ana, CA",
                    "Corpus Christi, TX", "Riverside, CA", "Lexington, KY", "Stockton, CA",
                    "Henderson, NV", "Saint Paul, MN", "St. Louis, MO", "Cincinnati, OH",
                    "Pittsburgh, PA", "Greensboro, NC", "Anchorage, AK", "Plano, TX",
                    "Lincoln, NE", "Orlando, FL", "Irvine, CA", "Newark, NJ",
                    "Durham, NC", "Chula Vista, CA", "Toledo, OH", "Fort Wayne, IN",
                    "St. Petersburg, FL", "Laredo, TX", "Jersey City, NJ", "Chandler, AZ",
                    "Madison, WI", "Lubbock, TX", "Scottsdale, AZ", "Reno, NV",
                    "Buffalo, NY", "Gilbert, AZ", "Glendale, AZ", "North Las Vegas, NV",
                    "Winston-Salem, NC", "Chesapeake, VA", "Norfolk, VA", "Fremont, CA",
                    "Garland, TX", "Irving, TX", "Hialeah, FL", "Richmond, VA",
                    "Boise, ID", "Spokane, WA", "Baton Rouge, LA"
                )
                
                // Filter cities based on search query
                val filteredCities = if (searchQuery.isBlank()) {
                    allCities.take(10) // Show first 10 cities when no search
                } else {
                    allCities.filter { 
                        it.contains(searchQuery, ignoreCase = true) 
                    }.take(20) // Show up to 20 matching cities
                }
                
                Text(
                    text = if (searchQuery.isBlank()) "Popular Cities" else "Search Results (${filteredCities.size})",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                if (filteredCities.isEmpty() && searchQuery.isNotBlank()) {
                    Text(
                        text = "No cities found matching \"$searchQuery\"",
                        color = Color.Gray,
                        modifier = Modifier.padding(16.dp),
                        fontSize = 14.sp
                    )
                } else {
                    filteredCities.forEach { city ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable {
                                    onCityChange(city)
                                    showCitySearch = false
                                    searchQuery = "" // Reset search
                                },
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFF333333)
                            )
                        ) {
                            Text(
                                text = city,
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
