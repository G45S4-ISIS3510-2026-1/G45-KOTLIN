package com.uniandes.tutorias_g45k.ui.reservation.gateway.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.uniandes.tutorias_g45k.data.reservation.SkillSummaryDto

@Composable
fun SkillSelector(modifier: Modifier =Modifier, skillsData:List<SkillSummaryDto>, onSkillSelection:(String)->Unit={}, selectedSkill:String=""){
    LazyRow(modifier=modifier,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ){
        items(skillsData.size){
            SkillOption(modifier=modifier, skill = skillsData[it], onSelection = {onSkillSelection(skillsData[it].id ?: "")}, selected =skillsData[it].id == selectedSkill)
        }
    }
}

@Composable
fun SkillOption(modifier: Modifier =Modifier, skill:SkillSummaryDto, onSelection:()->Unit={}, selected:Boolean=false){
    val backgroundColor = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
    val borderWidth = if (selected) 4.dp else 0.dp
    Surface(modifier=modifier
        .padding(10.dp)
        .requiredHeight(40.dp)
        .clickable { onSelection() },
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(width = borderWidth, color = MaterialTheme.colorScheme.onPrimary),
        color =backgroundColor,
    ){
        Row(verticalAlignment = Alignment.CenterVertically,
            modifier = modifier.fillMaxWidth().padding(horizontal=5.dp),
            horizontalArrangement = Arrangement.Center){
            Text(text=skill.label,
                modifier=modifier.fillMaxWidth(),
                style=MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )
        }
    }
}


