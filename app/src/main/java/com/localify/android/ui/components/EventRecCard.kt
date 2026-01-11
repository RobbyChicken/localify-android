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
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.localify.android.data.network.EventRecResponse
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun EventRecCard(
    event: EventRecResponse,
    onFavoriteClick: () -> Unit,
    isFavorited: Boolean,
    onEventClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onEventClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Image Section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
            ) {
            // Event Image - try multiple possible image fields
            val imageUrl = event.imageUrl ?: event.artists.firstOrNull()?.image
            imageUrl?.let { url ->
                AsyncImage(
                    model = url,
                    contentDescription = "Event image",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(16.dp)),
                    contentScale = ContentScale.Crop
                )
            } ?: run {
                // Fallback background when no image
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFF2A2A2A),
                                    Color(0xFF1A1A1A)
                                )
                            )
                        )
                )
            }
            
            // Date/Time overlay (top-left)
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(16.dp)
                    .background(
                        Color.Black.copy(alpha = 0.8f),
                        RoundedCornerShape(12.dp)
                    )
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = formatDayOfWeek(event.startTime),
                        fontSize = 12.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = formatDate(event.startTime),
                        fontSize = 16.sp,
                        color = Color(0xFFE91E63),
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = formatTime(event.startTime),
                        fontSize = 12.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            
            // Favorite button (top right)
            IconButton(
                onClick = onFavoriteClick,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(12.dp)
                    .background(
                        Color.Black.copy(alpha = 0.5f),
                        CircleShape
                    )
                    .size(40.dp)
            ) {
                Icon(
                    imageVector = if (isFavorited) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                    contentDescription = "Bookmark",
                    tint = if (isFavorited) Color(0xFF4A90E2) else Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
        
        // Info section with gray background
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF2A2A2A))
                .padding(12.dp)
        ) {
                // Location overlay
                run {
                    val venue = event.venue
                    Row(
                        modifier = Modifier
                            .background(
                                Color(0xFFE91E63),
                                RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Location",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = venue.name,
                            fontSize = 12.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Medium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                }
                // Event Name
                Text(
                    text = event.name,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                
                // Artists
                if (event.artists.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Artists: ${event.artists.map { it.name }.joinToString(", ")}",
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.8f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Bottom section with play button and match percentage
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Play button
                    IconButton(
                        onClick = { /* TODO: Play event */ },
                        modifier = Modifier
                            .background(
                                Color(0xFF007AFF),
                                CircleShape
                            )
                            .size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Play",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    
                    // Match percentage
                    Box(
                        modifier = Modifier
                            .background(
                                Color(0xFFE91E63),
                                CircleShape
                            )
                            .size(48.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (event.percentMatch > 0) "${(event.percentMatch * 100).toInt()}" else "95",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

private fun formatDayOfWeek(timestamp: Long): String {
    return try {
        val date = Date(timestamp)
        val format = SimpleDateFormat("EEE", Locale.getDefault())
        format.format(date)
    } catch (e: Exception) {
        "Day"
    }
}

private fun formatDate(timestamp: Long): String {
    return try {
        val date = Date(timestamp)
        val format = SimpleDateFormat("MMM d", Locale.getDefault())
        format.format(date)
    } catch (e: Exception) {
        "Date"
    }
}

private fun formatTime(timestamp: Long): String {
    return try {
        val date = Date(timestamp)
        val format = SimpleDateFormat("h:mm a", Locale.getDefault())
        format.format(date)
    } catch (e: Exception) {
        "Time"
    }
}
