package com.localify.android.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.localify.android.data.network.ArtistRecResponse

@Composable
fun ArtistRecCard(
    artist: ArtistRecResponse,
    onFavoriteClick: () -> Unit,
    isFavorited: Boolean,
    modifier: Modifier = Modifier
) {
    // Show error state if artist data is invalid
    if (artist.name.isBlank()) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp)
                .background(Color.Red.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Invalid artist data",
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
        return
    }
    // Debug logging with more details
    SideEffect {
        println("=== ARTIST CARD DEBUG ===")
        println("Artist: ${artist.name} (ID: ${artist.id})")
        println("Image URL: ${artist.image ?: "N/A"}")
        println("Spotify URL: ${artist.spotifyUrl ?: "N/A"}")
        println("Genres: ${artist.genres?.joinToString { it.name } ?: "None"}")
        println("Similar Artists: ${artist.similar?.take(2)?.joinToString { it.name } ?: "None"}")
        println("=========================")
    }
    // Debug logging
    SideEffect {
        println("DEBUG: Rendering ArtistRecCard for artist: ${artist.name}")
        println("DEBUG: Artist ID: ${artist.id}")
        println("DEBUG: Image URL: ${artist.image}")
        println("DEBUG: Spotify URL: ${artist.spotifyUrl}")
        println("DEBUG: Genres: ${artist.genres?.joinToString { it.name } ?: "None"}")
    }
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { /* TODO: Navigate to artist detail */ },
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2A2A2A)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp)
        ) {
            // Artist Image with fallback to a colored background
            if (!artist.image.isNullOrEmpty()) {
                val painter = rememberAsyncImagePainter(
                    model = artist.image,
                    error = null
                )
                
                androidx.compose.foundation.Image(
                    painter = painter,
                    contentDescription = "${artist.name} image",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(16.dp)),
                    contentScale = ContentScale.Crop
                )
                
                // Fallback if image fails to load
                if (painter.state is coil.compose.AsyncImagePainter.State.Error) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0xFF1E1E1E)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = artist.name.take(2).uppercase(),
                            color = Color.White,
                            fontSize = 48.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            } else {
                // Fallback when no image URL is provided
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFF1E1E1E)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = artist.name.take(2).uppercase(),
                        color = Color.White,
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            // Gradient overlay for text readability
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.7f)
                            ),
                            startY = 200f
                        )
                    )
            )
            
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
                    imageVector = if (isFavorited) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Favorite",
                    tint = if (isFavorited) Color(0xFFE91E63) else Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
            
            // Content overlay at bottom
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Artist Name
                Text(
                    text = artist.name,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                
                // Genres
                artist.genres?.let { genres ->
                    if (genres.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "# ${genres.map { it.name }.joinToString(", ")}",
                            fontSize = 14.sp,
                            color = Color.White.copy(alpha = 0.8f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
                
                // Similar artists
                artist.similar?.let { similarArtists ->
                    if (similarArtists.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Similar to:",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.6f)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            similarArtists.take(3).forEach { similarArtist ->
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF3A3A3A)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    similarArtist.image?.let { imageUrl ->
                                        AsyncImage(
                                            model = imageUrl,
                                            contentDescription = "Similar artist: ${similarArtist.name}",
                                            modifier = Modifier.fillMaxSize(),
                                            contentScale = ContentScale.Crop
                                        )
                                    } ?: Text(
                                        text = similarArtist.name.take(2).uppercase(),
                                        color = Color.White,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Bottom section with play button and match percentage
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Play button - using spotifyUrl if available
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF4A90E2))
                            .clickable { 
                                artist.spotifyUrl?.let { url ->
                                    // TODO: Handle Spotify URL (open in app or browser)
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Play on Spotify",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    
                    // Circular Match Percentage (iOS style)
                    val matchPercentage = artist.similar?.firstOrNull()?.let { 85 } ?: 85
                    
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "% Match",
                            color = Color.Gray,
                            fontSize = 10.sp,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        
                        Box(
                            modifier = Modifier.size(48.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                progress = matchPercentage / 100f,
                                modifier = Modifier.fillMaxSize(),
                                color = Color(0xFFE91E63),
                                strokeWidth = 4.dp,
                                trackColor = Color.Gray.copy(alpha = 0.3f),
                                strokeCap = StrokeCap.Round
                            )
                            
                            Text(
                                text = "$matchPercentage",
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
