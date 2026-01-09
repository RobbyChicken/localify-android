package com.localify.android.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.PI
import coil.compose.AsyncImage
import com.localify.android.data.models.Artist
import com.localify.android.data.local.UserPreferences

@Composable
fun ArtistCard(
    artist: Artist,
    onArtistClick: () -> Unit,
    isFavoriteOverride: Boolean? = null,
    onFavoriteClick: ((Boolean) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val userPreferences = remember { UserPreferences(context) }
    val favoriteArtists by userPreferences.favoriteArtists.collectAsState()
    val isFavorite = isFavoriteOverride ?: favoriteArtists.contains(artist.id)
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(280.dp)
            .clickable { onArtistClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // Background Image
            AsyncImage(
                model = artist.imageUrl,
                contentDescription = "Artist image",
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
                                Color.Black.copy(alpha = 0.8f)
                            ),
                            startY = 150f
                        )
                    )
            )
            
            // Top right - Heart icon
            IconButton(
                onClick = { 
                    if (onFavoriteClick != null) {
                        onFavoriteClick(!isFavorite)
                    } else {
                        if (isFavorite) {
                            userPreferences.removeFavoriteArtist(artist.id)
                        } else {
                            userPreferences.addFavoriteArtist(artist.id)
                        }
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
                    if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = if (isFavorite) "Remove from favorites" else "Add to favorites",
                    tint = if (isFavorite) Color(0xFFE91E63) else Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
            
            // Top left - Simplified match indicator
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(16.dp)
                    .background(
                        Color.Black.copy(alpha = 0.6f),
                        RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "${artist.popularity}% Match",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
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
                        text = "Ithaca, NY",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                
                // Artist name
                Text(
                    text = artist.name,
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                
                // Genres with hashtags (only show if not unknown)
                val validGenres = artist.genres.filter { !it.equals("Unknown", ignoreCase = true) }
                if (validGenres.isNotEmpty()) {
                    Text(
                        text = validGenres.take(3).joinToString(" ") { "#$it" },
                        color = Color.Gray,
                        fontSize = 14.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                } else {
                    Spacer(modifier = Modifier.height(12.dp))
                }
                
                // Bottom row with similar artists and play button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Similar to section
                    Column {
                        Text(
                            text = "Similar to:",
                            color = Color.Gray,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        
                        // Similar artists with actual images
                        Row(
                            horizontalArrangement = Arrangement.spacedBy((-4).dp)
                        ) {
                            val similarArtistImages = listOf(
                                "https://images.unsplash.com/photo-1493225457124-a3eb161ffa5f?ixlib=rb-4.0.3&w=100&q=80",
                                "https://images.unsplash.com/photo-1516280440614-37939bbacd81?ixlib=rb-4.0.3&w=100&q=80",
                                "https://images.unsplash.com/photo-1506794778202-cad84cf45f1d?ixlib=rb-4.0.3&w=100&q=80"
                            )
                            
                            similarArtistImages.forEach { imageUrl ->
                                AsyncImage(
                                    model = imageUrl,
                                    contentDescription = "Similar artist",
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .border(2.dp, Color.Black, CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                    }
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Play button
                        IconButton(
                            onClick = { /* Handle play */ },
                            modifier = Modifier
                                .size(48.dp)
                                .background(
                                    Color(0xFF4A90E2),
                                    CircleShape
                                )
                        ) {
                            Icon(
                                Icons.Default.PlayArrow,
                                contentDescription = "Play",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        
                        // Circular Match Percentage (iOS style)
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "% Match",
                                color = Color.Gray,
                                fontSize = 10.sp,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                            
                            CircularProgressIndicator(
                                progress = artist.popularity / 100f,
                                modifier = Modifier.size(48.dp),
                                color = Color(0xFFE91E63),
                                strokeWidth = 4.dp,
                                trackColor = Color.Gray.copy(alpha = 0.3f),
                                strokeCap = StrokeCap.Round
                            )
                            
                            // Overlay the percentage text
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .offset(y = (-48).dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${artist.popularity}",
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
}

@Composable
fun ArtistCardSmall(
    artist: Artist,
    onArtistClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(80.dp)
            .clickable { onArtistClick() },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A))
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Artist image
            AsyncImage(
                model = artist.imageUrl ?: "https://images.unsplash.com/photo-1493225457124-a3eb161ffa5f?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=2340&q=80",
                contentDescription = "Artist image",
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Artist info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = artist.name,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (artist.genres.isNotEmpty()) {
                    Text(
                        text = artist.genres.take(2).joinToString(", "),
                        color = Color.Gray,
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            
            // Popularity
            Text(
                text = "${artist.popularity}%",
                color = Color(0xFFE91E63),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
