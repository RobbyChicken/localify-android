package com.localify.android.ui.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.localify.android.data.network.CityResponse

@Composable
fun CitySearchScreen(
    searchText: String,
    onSearchTextChanged: (String) -> Unit,
    filteredCities: List<CityResponse>,
    selectedCityId: String,
    onCitySelected: (cityId: String, cityLabel: String) -> Unit
) {
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(24.dp)
    ) {
        // Title
        Text(
            text = "Search for City",
            color = Color.White,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Search field
        OutlinedTextField(
            value = searchText,
            onValueChange = onSearchTextChanged,
            placeholder = {
                Text(
                    text = "Search for cities...",
                    color = Color.Gray
                )
            },
            leadingIcon = {
                Icon(
                    Icons.Default.Search,
                    contentDescription = "Search",
                    tint = Color.Gray
                )
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Gray,
                unfocusedBorderColor = Color.Gray,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                cursorColor = Color.White
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // City results
        if (filteredCities.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredCities) { city ->
                    val label = buildString {
                        append(city.name)
                        if (!city.state.isNullOrBlank()) {
                            append(", ")
                            append(city.state)
                        }
                    }
                    CityItem(
                        city = label,
                        isSelected = selectedCityId == city.id,
                        onClick = {
                            onCitySelected(city.id, label)
                        }
                    )
                }
            }
        } else {
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
fun CityItem(
    city: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                if (isSelected) Color(0xFF2A2A2A) else Color.Transparent,
                RoundedCornerShape(8.dp)
            )
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = city,
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}
