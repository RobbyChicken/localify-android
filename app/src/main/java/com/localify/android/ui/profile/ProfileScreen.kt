package com.localify.android.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavigateToHome: () -> Unit = {},
    onNavigateToFavorites: () -> Unit = {},
    onNavigateToSearch: () -> Unit = {},
    onNavigateToLogin: () -> Unit = {},
    viewModel: ProfileViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.error) {
        uiState.error?.let { snackbarHostState.showSnackbar(it) }
    }
    
    Scaffold(
        containerColor = Color.Black,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
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
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Title
            Text(
                text = "Profile",
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 24.dp)
            )
            
            // Profile Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF2A2A2A)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (uiState.isLoading) {
                        LinearProgressIndicator(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp),
                            color = Color(0xFFE91E63)
                        )
                    }
                    // Profile Avatar
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF4A4A4A)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (uiState.profileImageUrl.isNotEmpty()) {
                            AsyncImage(
                                model = uiState.profileImageUrl,
                                contentDescription = "Profile Image",
                                modifier = Modifier
                                    .size(100.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = "Profile",
                                tint = Color.White,
                                modifier = Modifier.size(50.dp)
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // User Name
                    Text(
                        text = uiState.userName,
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    
                    // Email (if connected)
                    if (uiState.email.isNotEmpty()) {
                        Text(
                            text = uiState.email,
                            color = Color(0xFF999999),
                            fontSize = 16.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                    
                    // Member Since
                    Text(
                        text = "Member since ${uiState.memberSince}",
                        color = Color.Gray,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
            
            // Edit Profile Button
            Button(
                onClick = { /* TODO: Handle edit profile */ },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4A90E2)
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .padding(bottom = 32.dp)
            ) {
                Text(
                    "Edit Profile",
                    color = Color.White,
                    fontWeight = FontWeight.Medium
                )
            }
            
            // My Favorites Section
            SectionHeader("My Favorites")
            
            ProfileMenuItem(
                icon = Icons.Default.LocationOn,
                title = "My Cities",
                onClick = { /* TODO: Handle my cities */ }
            )
            
            ProfileMenuItem(
                icon = Icons.Default.Favorite,
                title = "My Familiar Artists",
                onClick = { /* TODO: Handle familiar artists */ }
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // My Preferences Section
            SectionHeader("My Preferences")
            
            // Email Opt-in Toggle (only show if email is connected)
            if (uiState.isEmailConnected) {
                PreferenceToggleItem(
                    icon = Icons.Default.Email,
                    title = "Receive Personalized Weekly Emails",
                    subtitle = "Subscribe to receive weekly emails about upcoming events curated to your cities and discovered artists.",
                    isChecked = uiState.emailOptIn,
                    onToggle = { viewModel.setEmailOptIn(it) }
                )
            }
            
            // Spotify Playlist Generation (only show if Spotify is connected)
            if (uiState.isSpotifyConnected) {
                PreferenceToggleItem(
                    icon = Icons.Default.MusicNote,
                    title = "Generate Local Spotify Playlists",
                    subtitle = "Generate Spotify playlists using your discovered artists. You can choose to include your familiar artists.",
                    isChecked = uiState.generateSpotifyPlaylists,
                    onToggle = { viewModel.setGenerateSpotifyPlaylists(it) }
                )
                
                // Nested toggle for using familiar artists (only show if playlist generation is enabled)
                if (uiState.generateSpotifyPlaylists) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF2A2A2A)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, bottom = 8.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Use my Familiar Artists in Playlists",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.weight(1f)
                            )
                            
                            Switch(
                                checked = uiState.playlistsIncludeLocalOnly,
                                onCheckedChange = { viewModel.setPlaylistsIncludeLocalOnly(it) },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = Color(0xFFE91E63),
                                    uncheckedThumbColor = Color.Gray,
                                    uncheckedTrackColor = Color(0xFF4A4A4A)
                                )
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Login & Security Section
            SectionHeader("Login & Security")
            
            // Connect Email Button (only show if not connected)
            if (!uiState.isEmailConnected) {
                Button(
                    onClick = { viewModel.connectEmail(context) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4A90E2)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .padding(bottom = 8.dp)
                ) {
                    Text(
                        "Connect Email",
                        color = Color.White,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            
            // Connect Spotify Button (only show if not connected)
            if (!uiState.isSpotifyConnected) {
                Button(
                    onClick = { viewModel.connectSpotify(context) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF1DB954)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .padding(bottom = 16.dp)
                ) {
                    Text(
                        "Connect Spotify",
                        color = Color.White,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            
            // Logout Button
            Button(
                onClick = { 
                    viewModel.logout(context)
                    onNavigateToLogin()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFF3B30)
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .padding(bottom = 8.dp)
            ) {
                Text(
                    "Logout",
                    color = Color.White,
                    fontWeight = FontWeight.Medium
                )
            }
            
            // Delete Account Button
            Button(
                onClick = { viewModel.deleteAccount(context, onNavigateToLogin) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF8E8E93)
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .padding(bottom = 16.dp)
            ) {
                Text(
                    "Delete Account",
                    color = Color.White,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        color = Color.White,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 16.dp)
    )
}

@Composable
private fun ProfileMenuItem(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF2A2A2A)
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = Color(0xFFE91E63),
                modifier = Modifier.size(24.dp)
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Text(
                text = title,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f)
            )
            
            Text(
                text = ">",
                color = Color.Gray,
                fontSize = 18.sp
            )
        }
    }
}

@Composable
private fun PreferenceToggleItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    isChecked: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF2A2A2A)
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = Color(0xFFE91E63),
                    modifier = Modifier.size(24.dp)
                )
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Text(
                    text = title,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f)
                )
                
                Switch(
                    checked = isChecked,
                    onCheckedChange = onToggle,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = Color(0xFFE91E63),
                        uncheckedThumbColor = Color.Gray,
                        uncheckedTrackColor = Color(0xFF4A4A4A)
                    )
                )
            }
            
            if (subtitle.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = subtitle,
                    color = Color.Gray,
                    fontSize = 14.sp,
                    lineHeight = 18.sp
                )
            }
        }
    }
}
