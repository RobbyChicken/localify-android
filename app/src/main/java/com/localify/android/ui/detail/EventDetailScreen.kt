package com.localify.android.ui.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.localify.android.data.models.Event
import com.localify.android.data.models.Venue
import com.localify.android.data.models.City
import com.localify.android.data.models.Artist

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDetailScreen(
    eventId: String,
    onNavigateBack: () -> Unit,
    onNavigateToArtistDetail: (String) -> Unit = {}
) {
    // Mock event data for now
    val mockEvent = Event(
        id = eventId,
        name = "Ray LaMontagne Live in Concert",
        imageUrl = "https://images.unsplash.com/photo-1493225457124-a3eb161ffa5f?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=2340&q=80",
        date = "March 15, 2024 • 8:00 PM",
        venue = Venue(
            id = "venue1",
            name = "The Capitol Theatre",
            address = "149 Westchester Ave",
            city = City(
                id = "city1",
                name = "Port Chester",
                state = "NY",
                country = "USA",
                latitude = 41.0018,
                longitude = -73.6651
            )
        ),
        artists = listOf(
            Artist(
                id = "artist1",
                name = "Ray LaMontagne",
                imageUrl = "https://via.placeholder.com/300x300/666666/ffffff?text=RL",
                genres = listOf("Folk", "Indie Rock"),
                bio = "Ray LaMontagne is an American singer-songwriter known for his raspy voice.",
                spotifyId = "2UazAtjfzqBF0Nho2awK4z",
                popularity = 75
            )
        ),
        ticketUrl = "https://example.com/tickets",
        description = "An intimate evening with Ray LaMontagne featuring songs from his latest album and classic hits."
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .verticalScroll(rememberScrollState())
    ) {
        // Header with Back Button and Title
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onNavigateBack,
                modifier = Modifier
                    .background(
                        Color.Black.copy(alpha = 0.5f),
                        CircleShape
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Text(
                text = mockEvent.artists.firstOrNull()?.name ?: "Artist",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = Color.White
            )
        }
        
        // Event Image Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .padding(horizontal = 24.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                AsyncImage(
                    model = mockEvent.imageUrl,
                    contentDescription = "${mockEvent.name} image",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                
                // Bookmark icon
                Icon(
                    imageVector = Icons.Default.LocationOn, // Using LocationOn as bookmark placeholder
                    contentDescription = "Bookmark",
                    tint = Color(0xFF007AFF),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp)
                        .size(24.dp)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Event Details
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = mockEvent.name,
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp
                ),
                color = Color.White,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = mockEvent.venue.name,
                style = MaterialTheme.typography.titleLarge,
                color = Color(0xFF007AFF),
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = "${mockEvent.venue.city.name}, ${mockEvent.venue.city.state}",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = mockEvent.date,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // View Event Tickets Button
        Button(
            onClick = { /* TODO: Open ticket URL */ },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFE91E63) // Localify pink
            ),
            shape = RoundedCornerShape(25.dp)
        ) {
            Icon(
                imageVector = Icons.Default.OpenInNew,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("View Event Tickets")
        }
        
        // Description Section
        if (mockEvent.description.isNotEmpty()) {
            Column(
                modifier = Modifier.padding(horizontal = 24.dp)
            ) {
                Text(
                    text = "About This Event",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = Color.White
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Text(
                    text = mockEvent.description,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White.copy(alpha = 0.8f),
                    lineHeight = 24.sp
                )
                
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Performing Artists Section
        if (mockEvent.artists.isNotEmpty()) {
            Column(
                modifier = Modifier.padding(horizontal = 24.dp)
            ) {
                Text(
                    text = "Performing Artists",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = Color.White
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // First artist from the event
                    mockEvent.artists.firstOrNull()?.let { artist ->
                        PerformingArtistCard(
                            name = artist.name,
                            subtitle = "North Smithfield,...", // Mock subtitle
                            imageUrl = artist.imageUrl,
                            onClick = { onNavigateToArtistDetail(artist.id) }
                        )
                    }
                    
                    // Add the second artist as shown in the design
                    PerformingArtistCard(
                        name = "David Rawlings",
                        subtitle = "North Smithfield,...",
                        imageUrl = "https://via.placeholder.com/80x80/666666/ffffff?text=DR",
                        onClick = { onNavigateToArtistDetail("david_rawlings") }
                    )
                }
                
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun PerformingArtistCard(
    name: String,
    subtitle: String,
    imageUrl: String,
    onClick: () -> Unit = {}
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(100.dp)
            .clickable { onClick() }
    ) {
        AsyncImage(
            model = imageUrl,
            contentDescription = name,
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = name,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Bold
            ),
            color = Color.White,
            textAlign = TextAlign.Center,
            maxLines = 1
        )
        
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodySmall,
            color = Color.White.copy(alpha = 0.7f),
            textAlign = TextAlign.Center,
            maxLines = 1
        )
    }
}
