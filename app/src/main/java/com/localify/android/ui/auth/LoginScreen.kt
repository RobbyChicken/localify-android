package com.localify.android.ui.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.localify.android.R
import androidx.compose.foundation.Canvas
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.Path
import kotlin.random.Random

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Map-style background
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            // Draw a dark map-like background
            drawRect(
                color = Color(0xFF1a1a1a),
                size = size
            )
            
            // Draw map-like grid lines
            val gridSpacing = 100f
            for (i in 0..size.width.toInt() step gridSpacing.toInt()) {
                drawLine(
                    color = Color(0xFF333333),
                    start = androidx.compose.ui.geometry.Offset(i.toFloat(), 0f),
                    end = androidx.compose.ui.geometry.Offset(i.toFloat(), size.height),
                    strokeWidth = 1f
                )
            }
            for (i in 0..size.height.toInt() step gridSpacing.toInt()) {
                drawLine(
                    color = Color(0xFF333333),
                    start = androidx.compose.ui.geometry.Offset(0f, i.toFloat()),
                    end = androidx.compose.ui.geometry.Offset(size.width, i.toFloat()),
                    strokeWidth = 1f
                )
            }
            
            // Draw some map-like shapes (roads/areas)
            val path = Path()
            path.moveTo(size.width * 0.2f, size.height * 0.3f)
            path.quadraticBezierTo(
                size.width * 0.5f, size.height * 0.1f,
                size.width * 0.8f, size.height * 0.4f
            )
            path.lineTo(size.width * 0.9f, size.height * 0.6f)
            path.quadraticBezierTo(
                size.width * 0.7f, size.height * 0.8f,
                size.width * 0.3f, size.height * 0.7f
            )
            path.close()
            
            drawPath(
                path = path,
                color = Color(0xFF2a2a2a)
            )
        }
        
        // Background Image overlay (if you still want the original image)
        Image(
            painter = painterResource(id = R.drawable.background_localify),
            contentDescription = "Localify background",
            modifier = Modifier.fillMaxSize().alpha(0.3f),
            contentScale = ContentScale.Crop
        )
        
        // Dark overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.3f),
                            Color.Black.copy(alpha = 0.8f)
                        )
                    )
                )
        )
        
        // Localify banner
        Image(
            painter = painterResource(id = R.drawable.localify_banner),
            contentDescription = "Localify Banner",
            modifier = Modifier
                .padding(start = 16.dp, top = 80.dp)
                .fillMaxWidth(0.8f),
            contentScale = ContentScale.Fit
        )
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            
            Spacer(modifier = Modifier.weight(1f))
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Login Buttons
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Spotify Login
                Button(
                    onClick = onLoginSuccess,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF1DB954)
                    ),
                    shape = RoundedCornerShape(28.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "♫",
                            fontSize = 18.sp,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Login with Spotify",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = Color.White
                        )
                    }
                }
                
                // Apple Login
                Button(
                    onClick = onLoginSuccess,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White
                    ),
                    shape = RoundedCornerShape(28.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "",
                            fontSize = 18.sp,
                            color = Color.Black
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Sign in with Apple",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = Color.Black
                        )
                    }
                }
                
                // Email Login
                Button(
                    onClick = onLoginSuccess,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFE91E63)
                    ),
                    shape = RoundedCornerShape(28.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "📧",
                            fontSize = 18.sp,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Log in with Email",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = Color.White
                        )
                    }
                }
                
                // Guest Mode
                Button(
                    onClick = onLoginSuccess,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2196F3)
                    ),
                    shape = RoundedCornerShape(28.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "👤",
                            fontSize = 18.sp,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Continue as Guest",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = Color.White
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
