package com.localify.android.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.localify.android.data.models.Event
import com.localify.android.data.local.UserPreferences
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun EventCard(
    event: Event,
    onEventClick: () -> Unit,
    onArtistClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val userPreferences = remember { UserPreferences(context) }
    val favoriteEvents by userPreferences.favoriteEvents.collectAsState()
    val isBookmarked = favoriteEvents.contains(event.id)
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(280.dp)
            .clickable { onEventClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // Background Image
            AsyncImage(
                model = event.imageUrl,
                contentDescription = "Event image",
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Crop
            )
            
            // Dark gradient overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.9f)
                            ),
                            startY = 100f
                        )
                    )
            )
            
            // Top left - Date block
            Card(
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.TopStart),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.Black.copy(alpha = 0.7f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = formatEventDay(event.date),
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = formatEventDate(event.date),
                        color = Color(0xFFE91E63),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = formatEventTime(event.date),
                        color = Color.White,
                        fontSize = 12.sp
                    )
                }
            }
            
            // Top right - Bookmark icon
            IconButton(
                onClick = { 
                    if (isBookmarked) {
                        userPreferences.removeFavoriteEvent(event.id)
                    } else {
                        userPreferences.addFavoriteEvent(event.id)
                    }
                },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
                    .size(40.dp)
                    .background(
                        Color.Black.copy(alpha = 0.5f),
                        CircleShape
                    )
            ) {
                Icon(
                    if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                    contentDescription = if (isBookmarked) "Remove bookmark" else "Add bookmark",
                    tint = if (isBookmarked) Color(0xFF4A90E2) else Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
            
            // Bottom content
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
            ) {
                // Location
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = "Location",
                        tint = Color(0xFFE91E63),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${event.venue.city.name}, ${event.venue.city.state}",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                
                // Artist name
                Text(
                    text = event.artists.firstOrNull()?.name ?: "Unknown Artist",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .padding(bottom = 4.dp)
                        .clickable { onArtistClick() }
                )
                
                // Event description
                Text(
                    text = event.description,
                    color = Color.Gray,
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                // Similar to section
                Text(
                    text = "Similar to:",
                    color = Color.Gray,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Similar artists with actual content
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val similarArtists = listOf("TS", "BE", "AM") // Taylor Swift, Billie Eilish, Arctic Monkeys
                        val artistColors = listOf(
                            Color(0xFFE91E63), // Pink
                            Color(0xFF4A90E2), // Blue
                            Color(0xFF9C27B0)  // Purple
                        )
                        
                        similarArtists.forEachIndexed { index, artist ->
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(artistColors[index % artistColors.size]),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = artist,
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Ticket button
                        IconButton(
                            onClick = { /* Handle tickets */ },
                            modifier = Modifier
                                .size(40.dp)
                                .background(
                                    Color(0xFF4A90E2),
                                    CircleShape
                                )
                        ) {
                            Icon(
                                Icons.Outlined.CreditCard,
                                contentDescription = "Tickets",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        
                        // Play button
                        IconButton(
                            onClick = { /* Handle play */ },
                            modifier = Modifier
                                .size(40.dp)
                                .background(
                                    Color(0xFF4A90E2),
                                    CircleShape
                                )
                        ) {
                            Icon(
                                Icons.Default.PlayArrow,
                                contentDescription = "Play",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        
                        // Match percentage
                        Text(
                            text = "% Match",
                            color = Color.Gray,
                            fontSize = 10.sp
                        )
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(
                                    Color.Transparent,
                                    CircleShape
                                )
                                .padding(2.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            // Circular progress indicator background
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Color.Gray.copy(alpha = 0.3f),
                                        CircleShape
                                    )
                            )
                            Text(
                                text = "${event.artists.firstOrNull()?.popularity ?: 83}",
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun formatEventDay(dateString: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
        val outputFormat = SimpleDateFormat("EEE", Locale.getDefault())
        val date = inputFormat.parse(dateString)
        outputFormat.format(date ?: Date())
    } catch (e: Exception) {
        "Tue"
    }
}

private fun formatEventDate(dateString: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
        val outputFormat = SimpleDateFormat("MMM d", Locale.getDefault())
        val date = inputFormat.parse(dateString)
        outputFormat.format(date ?: Date())
    } catch (e: Exception) {
        "Sep 9"
    }
}

private fun formatEventTime(dateString: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
        val outputFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
        val date = inputFormat.parse(dateString)
        outputFormat.format(date ?: Date())
    } catch (e: Exception) {
        "7:00 PM"
    }
}
