package com.localify.android.ui.favorites

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.localify.android.data.models.Artist
import com.localify.android.data.models.Event
import com.localify.android.ui.components.ArtistCard
import com.localify.android.ui.components.EventCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    onNavigateToArtistDetail: (String) -> Unit = {},
    onNavigateToEventDetail: (String) -> Unit = {},
    onNavigateToHome: () -> Unit = {},
    onNavigateToSearch: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {}
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val viewModel: FavoritesViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
        factory = androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.getInstance(context.applicationContext as android.app.Application)
    )
    val uiState by viewModel.uiState.collectAsState()
    
    LaunchedEffect(Unit) {
        println("DEBUG: FavoritesScreen - Initial load")
        viewModel.loadFavorites()
    }
    
    // Debug current state
    LaunchedEffect(uiState.favoriteArtists, uiState.upcomingEvents) {
        println("DEBUG: FavoritesScreen - UI State changed:")
        println("DEBUG: - Artists: ${uiState.favoriteArtists.map { it.name }}")
        println("DEBUG: - Events: ${uiState.upcomingEvents.map { it.name }}")
        println("DEBUG: - Selected tab: ${uiState.selectedTab}")
        println("DEBUG: - Is loading: ${uiState.isLoading}")
    }
    
    Scaffold(
        containerColor = Color.Black,
        bottomBar = {
            NavigationBar(
                containerColor = Color.Black,
                contentColor = Color.White
            ) {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = { Text("Home") },
                    selected = false,
                    onClick = onNavigateToHome,
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFFE91E63),
                        selectedTextColor = Color(0xFFE91E63),
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray,
                        indicatorColor = Color.Transparent
                    )
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Favorite, contentDescription = "Favorites") },
                    label = { Text("Favorites") },
                    selected = true,
                    onClick = { },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFFE91E63),
                        selectedTextColor = Color(0xFFE91E63),
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray,
                        indicatorColor = Color.Transparent
                    )
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                    label = { Text("Search") },
                    selected = false,
                    onClick = onNavigateToSearch,
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFFE91E63),
                        selectedTextColor = Color(0xFFE91E63),
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray,
                        indicatorColor = Color.Transparent
                    )
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                    label = { Text("Profile") },
                    selected = false,
                    onClick = onNavigateToProfile,
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFFE91E63),
                        selectedTextColor = Color(0xFFE91E63),
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray,
                        indicatorColor = Color.Transparent
                    )
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Title
            Text(
                text = "My Artists",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 24.dp)
            )
            
            // Tab Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TabButton(
                    text = "Artists",
                    isSelected = uiState.selectedTab == FavoritesTab.ARTISTS,
                    onClick = { viewModel.selectTab(FavoritesTab.ARTISTS) },
                    modifier = Modifier.weight(1f)
                )
                TabButton(
                    text = "Upcoming Events",
                    isSelected = uiState.selectedTab == FavoritesTab.UPCOMING_EVENTS,
                    onClick = { viewModel.selectTab(FavoritesTab.UPCOMING_EVENTS) },
                    modifier = Modifier.weight(1f)
                )
                TabButton(
                    text = "Past Events",
                    isSelected = uiState.selectedTab == FavoritesTab.PAST_EVENTS,
                    onClick = { viewModel.selectTab(FavoritesTab.PAST_EVENTS) },
                    modifier = Modifier.weight(1f)
                )
            }
            
            // Content
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = Color(0xFFE91E63),
                        modifier = Modifier.size(48.dp)
                    )
                }
            } else {
                when (uiState.selectedTab) {
                    FavoritesTab.ARTISTS -> {
                        if (uiState.favoriteArtists.isEmpty()) {
                            EmptyState(
                                message = "No favorite artists. Try discovering some!",
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            LazyColumn(
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                items(uiState.favoriteArtists) { artist ->
                                    ArtistCard(
                                        artist = artist,
                                        onArtistClick = { onNavigateToArtistDetail(artist.id) },
                                        isFavoriteOverride = true,
                                        onFavoriteClick = { shouldFavorite ->
                                            viewModel.setFavoriteArtist(artist.id, shouldFavorite)
                                        }
                                    )
                                }
                            }
                        }
                    }
                    FavoritesTab.UPCOMING_EVENTS -> {
                        if (uiState.upcomingEvents.isEmpty()) {
                            EmptyState(
                                message = "No upcoming events. Check back later!",
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            LazyColumn(
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                items(uiState.upcomingEvents) { event ->
                                    EventCard(
                                        event = event,
                                        onEventClick = { onNavigateToEventDetail(event.id) },
                                        onArtistClick = { onNavigateToArtistDetail(event.artists.firstOrNull()?.id ?: "") },
                                        isBookmarkedOverride = true,
                                        onBookmarkClick = { shouldFavorite ->
                                            viewModel.setFavoriteEvent(event.id, shouldFavorite)
                                        }
                                    )
                                }
                            }
                        }
                    }
                    FavoritesTab.PAST_EVENTS -> {
                        if (uiState.pastEvents.isEmpty()) {
                            EmptyState(
                                message = "No past events yet.",
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            LazyColumn(
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                items(uiState.pastEvents) { event ->
                                    EventCard(
                                        event = event,
                                        onEventClick = { onNavigateToEventDetail(event.id) },
                                        onArtistClick = { onNavigateToArtistDetail(event.artists.firstOrNull()?.id ?: "") },
                                        isBookmarkedOverride = true,
                                        onBookmarkClick = { shouldFavorite ->
                                            viewModel.setFavoriteEvent(event.id, shouldFavorite)
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TabButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) Color(0xFF4A4A4A) else Color.Transparent,
            contentColor = if (isSelected) Color.White else Color.Gray
        ),
        shape = RoundedCornerShape(20.dp),
        modifier = modifier.height(40.dp)
    ) {
        Text(
            text = text,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
private fun EmptyState(
    message: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            color = Color.Gray,
            fontSize = 16.sp,
            textAlign = TextAlign.Center
        )
    }
}

enum class FavoritesTab {
    ARTISTS, UPCOMING_EVENTS, PAST_EVENTS
}
