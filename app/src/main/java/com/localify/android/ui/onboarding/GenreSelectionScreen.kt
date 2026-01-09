package com.localify.android.ui.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.localify.android.data.network.GenreV1Response

data class Genre(
    val name: String,
    val artists: String
)

@Composable
fun GenreSelectionScreen(
    selectedGenreIds: Set<String>,
    genres: List<GenreV1Response>,
    onGenresChanged: (selectedIds: Set<String>, selectedNames: Set<String>) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(24.dp)
    ) {
        
        // Title
        Text(
            text = "Select Genres",
            color = Color.White,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Subtitle
        Text(
            text = "Select Between 3-5 Genres",
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
                            if (index < selectedGenreIds.size) Color(0xFFE91E63) else Color.Gray,
                            RoundedCornerShape(6.dp)
                        )
                )
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Genre list
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(genres) { genre ->
                GenreItem(
                    genre = Genre(genre.name, ""),
                    isSelected = selectedGenreIds.contains(genre.id),
                    onToggle = {
                        val newIds = if (selectedGenreIds.contains(genre.id)) {
                            selectedGenreIds - genre.id
                        } else {
                            if (selectedGenreIds.size < 5) {
                                selectedGenreIds + genre.id
                            } else {
                                selectedGenreIds
                            }
                        }
                        val newNames = genres.filter { newIds.contains(it.id) }.map { it.name }.toSet()
                        onGenresChanged(newIds, newNames)
                    }
                )
            }
        }
        
    }
}

@Composable
fun GenreItem(
    genre: Genre,
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
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = isSelected,
            onCheckedChange = { onToggle() },
            colors = CheckboxDefaults.colors(
                checkedColor = Color(0xFF4A90E2),
                uncheckedColor = Color.Gray,
                checkmarkColor = Color.White
            )
        )
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = genre.name,
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = genre.artists,
                color = Color.Gray,
                fontSize = 14.sp
            )
        }
    }
}
