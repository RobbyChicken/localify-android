package com.localify.android.ui.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

data class SelectableArtist(
    val id: String,
    val name: String,
    val imageUrl: String
)

@Composable
fun ArtistSelectionScreen(
    selectedArtists: Set<String>,
    onArtistsChanged: (Set<String>) -> Unit
) {
    val artists = listOf(
        SelectableArtist("1", "Ray Charles", "https://images.unsplash.com/photo-1493225457124-a3eb161ffa5f"),
        SelectableArtist("2", "Weezer", "https://images.unsplash.com/photo-1516280440614-37939bbacd81"),
        SelectableArtist("3", "Johnny Cash", "https://images.unsplash.com/photo-1493225457124-a3eb161ffa5f"),
        SelectableArtist("4", "The Killers", "https://images.unsplash.com/photo-1516280440614-37939bbacd81"),
        SelectableArtist("5", "Slightly Stoopid", "https://images.unsplash.com/photo-1493225457124-a3eb161ffa5f"),
        SelectableArtist("6", "Luke Combs", "https://images.unsplash.com/photo-1516280440614-37939bbacd81"),
        SelectableArtist("7", "Bruce Springsteen", "https://images.unsplash.com/photo-1493225457124-a3eb161ffa5f"),
        SelectableArtist("8", "Taylor Swift", "https://images.unsplash.com/photo-1516280440614-37939bbacd81"),
        SelectableArtist("9", "Ed Sheeran", "https://images.unsplash.com/photo-1493225457124-a3eb161ffa5f"),
        SelectableArtist("10", "Billie Eilish", "https://images.unsplash.com/photo-1516280440614-37939bbacd81"),
        SelectableArtist("11", "Drake", "https://images.unsplash.com/photo-1493225457124-a3eb161ffa5f"),
        SelectableArtist("12", "Ariana Grande", "https://images.unsplash.com/photo-1516280440614-37939bbacd81"),
        SelectableArtist("13", "Post Malone", "https://images.unsplash.com/photo-1493225457124-a3eb161ffa5f"),
        SelectableArtist("14", "Dua Lipa", "https://images.unsplash.com/photo-1516280440614-37939bbacd81"),
        SelectableArtist("15", "The Weeknd", "https://images.unsplash.com/photo-1493225457124-a3eb161ffa5f")
    )
    
    var searchText by remember { mutableStateOf("") }
    var showSearch by remember { mutableStateOf(false) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(24.dp)
    ) {
        
        // Title
        Text(
            text = "Select Artists",
            color = Color.White,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Subtitle
        Text(
            text = "Select At Least 5 Artists",
            color = Color.Gray,
            fontSize = 16.sp
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Progress indicators
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            repeat(5) { index ->
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(
                            if (index < selectedArtists.size) Color(0xFFE91E63) else Color.Gray,
                            CircleShape
                        )
                )
            }
            Text(
                text = "...",
                color = Color.Gray,
                fontSize = 16.sp,
                modifier = Modifier.padding(start = 4.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // All artists list
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.weight(1f)
        ) {
            val filteredArtists = if (searchText.length >= 2) {
                artists.filter { it.name.contains(searchText, ignoreCase = true) }
            } else {
                artists
            }
            
            items(filteredArtists) { artist ->
                ArtistItem(
                    artist = artist,
                    isSelected = selectedArtists.contains(artist.id),
                    onToggle = {
                        val newArtists = if (selectedArtists.contains(artist.id)) {
                            selectedArtists - artist.id
                        } else {
                            selectedArtists + artist.id
                        }
                        onArtistsChanged(newArtists)
                    }
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Search for artists manually button
        TextButton(
            onClick = { showSearch = !showSearch },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = if (showSearch) "Hide artist search" else "Search for artists manually",
                color = Color(0xFF4A90E2),
                fontSize = 16.sp
            )
        }
        
        if (showSearch) {
            Spacer(modifier = Modifier.height(16.dp))
            
            // Search field
            OutlinedTextField(
                value = searchText,
                onValueChange = { searchText = it },
                placeholder = {
                    Text(
                        text = "Search artists",
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
            
            Text(
                text = "Type at least 2 characters to search artists.",
                color = Color.Gray,
                fontSize = 14.sp
            )
        }
        
    }
}

@Composable
fun ArtistItem(
    artist: SelectableArtist,
    isSelected: Boolean,
    onToggle: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Color(0xFF1A1A1A),
                RoundedCornerShape(12.dp)
            )
            .clickable { onToggle() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Artist image
        Box {
            AsyncImage(
                model = artist.imageUrl,
                contentDescription = "Artist image",
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(
                            Color.Black.copy(alpha = 0.6f),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = "Selected",
                        tint = Color(0xFF4A90E2),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Text(
            text = artist.name,
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f)
        )
    }
}
