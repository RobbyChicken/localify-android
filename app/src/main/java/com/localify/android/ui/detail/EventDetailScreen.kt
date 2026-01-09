package com.localify.android.ui.detail

import android.content.Intent
import android.net.Uri
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
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
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
    val context = LocalContext.current
    val viewModel: EventDetailViewModel = viewModel(
        factory = ViewModelProvider.AndroidViewModelFactory.getInstance(context.applicationContext as android.app.Application)
    )
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(eventId) {
        viewModel.loadEvent(eventId)
    }

    val event = uiState.event

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .verticalScroll(rememberScrollState())
    ) {
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color(0xFFE91E63))
            }
        }

        uiState.error?.let { error ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Failed to load event",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = error,
                    color = Color.Gray,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { viewModel.loadEvent(eventId) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE91E63))
                ) {
                    Text("Retry", color = Color.White)
                }
            }
        }

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
                text = event?.artists?.firstOrNull()?.name ?: "Event",
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
                    model = event?.imageUrl,
                    contentDescription = "${event?.name ?: "Event"} image",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                
                // Bookmark icon
                IconButton(
                    onClick = { viewModel.toggleFavorite() },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp)
                        .background(
                            Color.Black.copy(alpha = 0.5f),
                            CircleShape
                        )
                ) {
                    Icon(
                        imageVector = if (uiState.isFavorite) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        contentDescription = if (uiState.isFavorite) "Remove bookmark" else "Add bookmark",
                        tint = if (uiState.isFavorite) Color(0xFF007AFF) else Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
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
                text = event?.name ?: "",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp
                ),
                color = Color.White,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = event?.venue?.name ?: "",
                style = MaterialTheme.typography.titleLarge,
                color = Color(0xFF007AFF),
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = "${event?.venue?.city?.name ?: ""}, ${event?.venue?.city?.state ?: ""}".trimEnd(',', ' '),
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = event?.date ?: "",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // View Event Tickets Button
        Button(
            onClick = {
                val url = event?.ticketUrl
                if (!url.isNullOrBlank()) {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    context.startActivity(intent)
                }
            },
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
        if (!event?.description.isNullOrEmpty()) {
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
                    text = event?.description ?: "",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White.copy(alpha = 0.8f),
                    lineHeight = 24.sp
                )
                
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Performing Artists Section
        if (!event?.artists.isNullOrEmpty()) {
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
                    event?.artists?.firstOrNull()?.let { artist ->
                        PerformingArtistCard(
                            name = artist.name,
                            subtitle = "",
                            imageUrl = artist.imageUrl,
                            onClick = { onNavigateToArtistDetail(artist.id) }
                        )
                    }

                    event?.artists?.drop(1)?.firstOrNull()?.let { artist ->
                        PerformingArtistCard(
                            name = artist.name,
                            subtitle = "",
                            imageUrl = artist.imageUrl,
                            onClick = { onNavigateToArtistDetail(artist.id) }
                        )
                    }
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
                .clip(CircleShape)
                .background(Color(0xFF2A2A2A)),
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
