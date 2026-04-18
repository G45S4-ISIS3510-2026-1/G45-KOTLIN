package com.example.g45_kotlin.ui.tutor.become

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.g45_kotlin.data.reservation.SkillSummaryDto

@Composable
fun BecomeTutorSkillsScreen(
    viewModel: BecomeTutorViewModel,
    onNext: () -> Unit,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val darkBlue = Color(0xFF0D1117)
    val accentYellow = Color(0xFFFFD600)

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = darkBlue
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(56.dp))
            
            Text(
                text = "¿Qué áreas vas a enseñar?",
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 38.sp
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Selecciona las facultades y materias en las que eres experto. Esto ayudará a los estudiantes de Uniandes a encontrarte más rápido.",
                color = Color.Gray,
                fontSize = 16.sp,
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            if (uiState.isLoading && uiState.majors.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = accentYellow)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.majors) { major ->
                        val icon = when {
                            major.contains("Ingeniería", ignoreCase = true) -> Icons.Default.Engineering
                            major.contains("Ciencias", ignoreCase = true) -> Icons.Default.Science
                            major.contains("Economía", ignoreCase = true) -> Icons.Default.AccountBalance
                            else -> Icons.Default.Palette
                        }
                        
                        MajorExpandableItem(
                            major = major,
                            icon = icon,
                            isExpanded = uiState.expandedMajors.contains(major),
                            skills = uiState.skillsByMajor[major] ?: emptyList(),
                            selectedSkills = uiState.selectedSkills,
                            onToggleExpand = { viewModel.toggleMajor(major) },
                            onToggleSkill = { viewModel.toggleSkill(it) }
                        )
                    }
                    item { Spacer(modifier = Modifier.height(16.dp)) }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = onNext,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = accentYellow,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(28.dp),
                    enabled = uiState.selectedSkills.isNotEmpty()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            "Guardar y Continuar",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, null)
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                TextButton(onClick = onBack) {
                    Text(
                        "OMITIR",
                        color = Color(0xFF339AF0),
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }
            }
        }
    }
}

@Composable
fun MajorExpandableItem(
    major: String,
    icon: ImageVector,
    isExpanded: Boolean,
    skills: List<SkillSummaryDto>,
    selectedSkills: Set<String>,
    onToggleExpand: () -> Unit,
    onToggleSkill: (String) -> Unit
) {
    val cardBackground = if (isExpanded) Color(0xFF161B22) else Color(0xFF161B22).copy(alpha = 0.5f)
    val borderColor = if (isExpanded) Color(0xFF30363D) else Color.Transparent

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(cardBackground)
            .border(1.dp, borderColor, RoundedCornerShape(16.dp))
            .clickable { onToggleExpand() }
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF1F6FEB).copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color(0xFF58A6FF),
                    modifier = Modifier.size(20.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Text(
                text = major,
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(1f)
            )
            
            Icon(
                imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                contentDescription = null,
                tint = Color.Gray
            )
        }

        AnimatedVisibility(
            visible = isExpanded,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Column(
                modifier = Modifier.padding(top = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                skills.forEach { skill ->
                    val isSelected = selectedSkills.contains(skill.id)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF0D1117))
                            .border(
                                1.dp,
                                if (isSelected) Color(0xFF58A6FF) else Color(0xFF30363D),
                                RoundedCornerShape(12.dp)
                            )
                            .clickable { onToggleSkill(skill.id ?: "") }
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = skill.label,
                            color = Color.White,
                            modifier = Modifier.weight(1f)
                        )
                        
                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = Color(0xFF58A6FF),
                                modifier = Modifier.size(20.dp)
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .border(2.dp, Color.Gray, CircleShape)
                            )
                        }
                    }
                }
            }
        }
    }
}
