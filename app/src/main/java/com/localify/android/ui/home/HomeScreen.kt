package com.localify.android.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.localify.android.data.models.Event
import com.localify.android.data.models.Artist
import com.localify.android.ui.components.EventCard
import com.localify.android.ui.components.ArtistCard
import com.localify.android.ui.components.CitySelectionModal
import com.localify.android.data.local.UserPreferences

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToEventDetail: (String) -> Unit,
    onNavigateToArtistDetail: (String) -> Unit,
    onNavigateToFavorites: () -> Unit = {},
    onNavigateToSearch: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    viewModel: HomeViewModel = viewModel()
) {
    // Debug state
    var showDebugDialog by remember { mutableStateOf(false) }
    var debugInfo by remember { mutableStateOf("") }
    
    // UI state
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val userPreferences = remember { UserPreferences(context) }
    
    // Update debug info when UI state changes
    LaunchedEffect(uiState) {
        debugInfo = """
            === DEBUG INFO ===
            Selected Tab: ${uiState.selectedTab}
            Is Loading: ${uiState.isLoading}
            Error: ${uiState.error ?: "None"}
            Artists Count: ${uiState.artists.size}
            Events Count: ${uiState.events.size}
            Current City: ${uiState.currentCity?.name ?: "None"}
            ==================
        """.trimIndent()
        
        // Log to console as well
        println(debugInfo)
    }
    
    val savedCity by userPreferences.selectedCity.collectAsState()
    var showCityModal by remember { mutableStateOf(false) }
    var showFilterMenu by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showDebugInfo by remember { mutableStateOf(false) }
    var selectedTimeFrame by remember { mutableStateOf("Custom") }
    var startDate by remember { mutableStateOf("Sep 7, 2025") }
    var endDate by remember { mutableStateOf("Sep 8, 2025") }
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { showCityModal = true }
                    ) {
                        Text(
                            text = uiState.currentCity?.name ?: "Loading...",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "▼",
                            color = Color.White,
                            fontSize = 12.sp
                        )
                    }
                },
                actions = {
                    // Only show filter buttons when Events tab is selected
                    if (uiState.selectedTab == HomeTab.EVENTS) {
                        // Filter menu button
                        Box {
                            IconButton(onClick = { showFilterMenu = !showFilterMenu }) {
                                Icon(
                                    Icons.Default.Menu,
                                    contentDescription = "Filter Menu",
                                    tint = Color.White
                                )
                            }
                            
                            DropdownMenu(
                                expanded = showFilterMenu,
                                onDismissRequest = { showFilterMenu = false },
                                modifier = Modifier.background(Color(0xFF2A2A2A))
                            ) {
                                DropdownMenuItem(
                                    text = { 
                                        Text(
                                            "Score",
                                            color = Color.White
                                        )
                                    },
                                    onClick = { 
                                        viewModel.applyDateFilter("All upcoming")
                                        showFilterMenu = false
                                    }
                                )
                                DropdownMenuItem(
                                    text = { 
                                        Text(
                                            "Date",
                                            color = Color.White
                                        )
                                    },
                                    onClick = { 
                                        showDatePicker = true
                                        showFilterMenu = false
                                    }
                                )
                            }
                        }
                        
                        // Calendar/Date picker button
                        IconButton(onClick = { showDatePicker = true }) {
                            Icon(
                                Icons.Default.DateRange,
                                contentDescription = "Date Filter",
                                tint = Color.White
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Black
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = Color.Black
            ) {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = { Text("Home") },
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
                    icon = { Icon(Icons.Default.Favorite, contentDescription = "Favorites") },
                    label = { Text("Favorites") },
                    selected = false,
                    onClick = onNavigateToFavorites,
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
        },
        containerColor = Color.Black
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            // Category Tabs - iOS style rounded buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Row(
                    modifier = Modifier
                        .background(
                            Color(0xFF2A2A2A),
                            RoundedCornerShape(25.dp)
                        )
                        .padding(4.dp)
                ) {
                    TabButton(
                        text = "Artists",
                        isSelected = uiState.selectedTab == HomeTab.ARTISTS,
                        onClick = { viewModel.selectTab(HomeTab.ARTISTS) },
                        modifier = Modifier.weight(1f)
                    )
                    TabButton(
                        text = "Events",
                        isSelected = uiState.selectedTab == HomeTab.EVENTS,
                        onClick = { viewModel.selectTab(HomeTab.EVENTS) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            
            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Loading...",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color(0xFFE91E63)
                        )
                    }
                }
                uiState.error != null -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = when (uiState.selectedTab) {
                                HomeTab.ARTISTS -> "Failed to load artists"
                                HomeTab.EVENTS -> "Failed to load events"
                            },
                            color = Color.White,
                            fontSize = 18.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = uiState.error ?: "Unknown error",
                            color = Color.Gray,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 32.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { viewModel.retry() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFE91E63)
                            )
                        ) {
                            Text("Retry", color = Color.White)
                        }
                    }
                }
                else -> {
                    when (uiState.selectedTab) {
                        HomeTab.EVENTS -> {
                            if (uiState.events.isEmpty()) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "No events found",
                                        color = Color.Gray,
                                        fontSize = 16.sp
                                    )
                                }
                            } else {
                                LazyColumn {
                                    items(uiState.events) { event ->
                                        val context = LocalContext.current
                                        val userPreferences = remember { UserPreferences(context) }
                                        val favoriteEvents by userPreferences.favoriteEvents.collectAsState()
                                        
                                        com.localify.android.ui.components.EventRecCard(
                                            event = event,
                                            onFavoriteClick = {
                                                if (favoriteEvents.contains(event.id)) {
                                                    userPreferences.removeFavoriteEvent(event.id)
                                                } else {
                                                    userPreferences.addFavoriteEvent(event.id)
                                                }
                                            },
                                            isFavorited = favoriteEvents.contains(event.id),
                                            onEventClick = { onNavigateToEventDetail(event.id) }
                                        )
                                    }
                                }
                            }
                        }
                        HomeTab.ARTISTS -> {
                            if (uiState.artists.isEmpty()) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = "No artists found",
                                        color = Color.Gray,
                                        fontSize = 16.sp,
                                        modifier = Modifier.padding(bottom = 16.dp)
                                    )
                                    
                                    // Debug information
                                    if (uiState.error != null) {
                                        Text(
                                            text = "Error: ${uiState.error}",
                                            color = Color.Red,
                                            fontSize = 14.sp,
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.padding(bottom = 8.dp)
                                        )
                                    }
                                    
                                    Button(
                                        onClick = { 
                                            // Force reload
                                            viewModel.loadRecommendations() 
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color(0xFFE91E63)
                                        )
                                    ) {
                                        Text("Retry Loading")
                                    }
                                    
                                    // Show raw data for debugging
                                    if (uiState.artists.isEmpty() && uiState.events.isNotEmpty()) {
                                        Spacer(modifier = Modifier.height(16.dp))
                                        Text(
                                            "Debug: Found ${uiState.events.size} events but no artists. This might indicate an issue with the artist data.",
                                            color = Color.Yellow,
                                            fontSize = 12.sp,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            } else {
                                LazyColumn(
                                    modifier = Modifier.fillMaxSize(),
                                    contentPadding = PaddingValues(vertical = 8.dp)
                                ) {
                                    items(uiState.artists) { artist ->
                                        val context = LocalContext.current
                                        val userPreferences = remember { UserPreferences(context) }
                                        val favoriteArtists by userPreferences.favoriteArtists.collectAsState()
                                        
                                        com.localify.android.ui.components.ArtistRecCard(
                                            artist = artist,
                                            onFavoriteClick = {
                                                if (favoriteArtists.contains(artist.id)) {
                                                    userPreferences.removeFavoriteArtist(artist.id)
                                                } else {
                                                    userPreferences.addFavoriteArtist(artist.id)
                                                }
                                            },
                                            isFavorited = favoriteArtists.contains(artist.id),
                                            onArtistClick = { onNavigateToArtistDetail(artist.id) },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 16.dp, vertical = 8.dp)
                                        )
                                    }
                                    
                                    // Add some bottom padding to account for the navigation bar
                                    item {
                                        Spacer(modifier = Modifier.height(80.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    if (showCityModal) {
        CitySelectionModal(
            isVisible = showCityModal,
            onDismiss = { showCityModal = false },
            onCityChange = { city ->
                // TODO: Handle city change
                showCityModal = false
            }
        )
    }
    
    // Date Picker Dialog
    if (showDatePicker) {
        AlertDialog(
            onDismissRequest = { showDatePicker = false },
            title = {
                Text(
                    "Select Dates",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    // Time frame options
                    listOf(
                        "Custom",
                        "One week", 
                        "One month",
                        "Three months",
                        "All upcoming"
                    ).forEach { option ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { 
                                    selectedTimeFrame = option
                                }
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = option,
                                color = if (option == selectedTimeFrame) Color.White else Color.Gray,
                                fontSize = 16.sp,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Custom date inputs - only show if Custom is selected
                    if (selectedTimeFrame == "Custom") {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Start Date", color = Color.White, fontSize = 14.sp)
                                Text(
                                    text = startDate,
                                    color = Color.Gray,
                                    fontSize = 16.sp,
                                    modifier = Modifier.clickable { showStartDatePicker = true }
                                )
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text("End Date", color = Color.White, fontSize = 14.sp)
                                Text(
                                    text = endDate,
                                    color = Color.Gray,
                                    fontSize = 16.sp,
                                    modifier = Modifier.clickable { showEndDatePicker = true }
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { 
                        viewModel.applyDateFilter(selectedTimeFrame, startDate, endDate)
                        showDatePicker = false 
                    }
                ) {
                    Text("Done", color = Color(0xFFE91E63))
                }
            },
            containerColor = Color(0xFF2A2A2A)
        )
    }
    
    // Start Date Picker
    if (showStartDatePicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showStartDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val formatter = java.text.SimpleDateFormat("MMM d, yyyy", java.util.Locale.getDefault())
                            startDate = formatter.format(java.util.Date(millis))
                        }
                        showStartDatePicker = false
                    }
                ) {
                    Text("OK", color = Color(0xFFE91E63))
                }
            },
            dismissButton = {
                TextButton(onClick = { showStartDatePicker = false }) {
                    Text("Cancel", color = Color.Gray)
                }
            }
        ) {
            DatePicker(
                state = datePickerState,
                colors = DatePickerDefaults.colors(
                    containerColor = Color(0xFF2A2A2A)
                )
            )
        }
    }
    
    // End Date Picker
    if (showEndDatePicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showEndDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val formatter = java.text.SimpleDateFormat("MMM d, yyyy", java.util.Locale.getDefault())
                            endDate = formatter.format(java.util.Date(millis))
                        }
                        showEndDatePicker = false
                    }
                ) {
                    Text("OK", color = Color(0xFFE91E63))
                }
            },
            dismissButton = {
                TextButton(onClick = { showEndDatePicker = false }) {
                    Text("Cancel", color = Color.Gray)
                }
            }
        ) {
            DatePicker(
                state = datePickerState,
                colors = DatePickerDefaults.colors(
                    containerColor = Color(0xFF2A2A2A)
                )
            )
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
            containerColor = if (isSelected) Color(0xFFE91E63) else Color.Transparent,
            contentColor = if (isSelected) Color.White else Color.Gray
        ),
        modifier = modifier.height(36.dp),
        shape = RoundedCornerShape(18.dp)
    ) {
        Text(
            text = text,
            fontSize = 14.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
fun LargeArtistCard(
    artist: Artist,
    onArtistClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val userPreferences = remember { UserPreferences(context) }
    val favoriteArtists by userPreferences.favoriteArtists.collectAsState()
    val isFavorite = favoriteArtists.contains(artist.id)
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(400.dp)
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
                            startY = 200f
                        )
                    )
            )
            
            // Top right - Heart icon
            IconButton(
                onClick = { 
                    if (isFavorite) {
                        userPreferences.removeFavoriteArtist(artist.id)
                    } else {
                        userPreferences.addFavoriteArtist(artist.id)
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
            
            // Bottom content
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomStart)
                    .padding(20.dp)
            ) {
                // Location with pink pin
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
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                // Genres with hashtags (only show if not unknown)
                val validGenres = artist.genres.filter { !it.equals("Unknown", ignoreCase = true) }
                if (validGenres.isNotEmpty()) {
                    Text(
                        text = validGenres.take(3).joinToString(" ") { "#$it" },
                        color = Color.Gray,
                        fontSize = 16.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                } else {
                    Spacer(modifier = Modifier.height(16.dp))
                }
                
                // Bottom row with similar artists and match percentage
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
                    
                    // Match percentage and play button
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
                        
                        // Match percentage
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "% Match",
                                color = Color.Gray,
                                fontSize = 10.sp
                            )
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .background(
                                        Color(0xFFE91E63),
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${artist.popularity}",
                                    color = Color.White,
                                    fontSize = 16.sp,
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
fun LargeEventCard(
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
            .height(400.dp)
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
                                Color.Black.copy(alpha = 0.8f)
                            ),
                            startY = 200f
                        )
                    )
            )
            
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
                    .padding(20.dp)
            ) {
                // Location with pink pin
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
                
                // Artist/Event name
                Text(
                    text = event.artists.firstOrNull()?.name ?: "Unknown Artist",
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                        .clickable { onArtistClick() }
                )
                
                // Event description or genres
                Text(
                    text = event.description.ifEmpty { 
                        event.artists.firstOrNull()?.genres?.take(3)?.joinToString(" ") { "#$it" } ?: "#Music #Live"
                    },
                    color = Color.Gray,
                    fontSize = 16.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                // Bottom row with similar artists and match percentage
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
                    
                    // Match percentage and play button
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
                        
                        // Match percentage
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "% Match",
                                color = Color.Gray,
                                fontSize = 10.sp
                            )
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .background(
                                        Color(0xFFE91E63),
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${event.artists.firstOrNull()?.popularity ?: 85}",
                                    color = Color.White,
                                    fontSize = 16.sp,
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

