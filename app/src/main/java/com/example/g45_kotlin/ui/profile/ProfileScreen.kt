package com.example.g45_kotlin.ui.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.g45_kotlin.ui.profile.components.ListMenuProfile
import com.example.g45_kotlin.ui.profile.components.ProfileBanner
import com.example.g45_kotlin.ui.theme.AppTheme

@Composable
fun ProfileScreen(modifier: Modifier =Modifier,
                  viewModel: ProfileScreenViewModel = viewModel(),
                  onSignOut:()->Unit={},
                  onEditProfile:()->Unit={},
                  onCheckReservations:()->Unit={},
                  onCheckPQRs:()->Unit={},
                  onCheckFavorites:()->Unit={},
                  onChangePreference:()->Unit={},
                  onStartPqr:()->Unit={},
                  onCheckReviews:()->Unit={},
                  dynamicTheme:Boolean=false
){
    val uiState by viewModel.uiState.collectAsState()
    Column(modifier=modifier.fillMaxHeight().padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally){
        Spacer(modifier=Modifier.height(30.dp))
        LazyColumn(){
            item{
                Row(modifier=Modifier.fillMaxWidth().padding(top=30.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ){
                    Text(text="Mi Perfil",
                        textAlign = TextAlign.Center,
                        modifier=Modifier.fillMaxWidth(fraction = 0.75f),
                        style=MaterialTheme.typography.headlineLarge
                    )
                }
                /*TODO*/
                if (uiState.isLoading){
                    Box(contentAlignment = Alignment.Center){
                        CircularProgressIndicator()
                    }
                }else{
                    ProfileBanner(name= uiState.user?.name ?: "<<Usuario>>",
                        major=uiState.user?.major ?: "<<Major>>",
                        rating=uiState.user?.tutorRating,
                        code=uiState.user?.uniandesId.toString(),
                        imageUrl=uiState.user?.profileImageUrl ?: "https://media.licdn.com/dms/image/v2/D4E03AQErWMZGSWwcqg/profile-displayphoto-crop_800_800/B4EZxJq0pSLAAI-/0/1770762490962?e=1778716800&v=beta&t=khP8DFqey-djTQzSuecIN4GTT-JnPEjWwDdBxAPyrls" ,
                        onStartPqr=onStartPqr)
                }
                Spacer(modifier=Modifier.height(20.dp))
                ListMenuProfile(
                    modifier = Modifier.fillMaxWidth(),
                    onEditProfile = onEditProfile,
                    onCheckReservations = onCheckReservations,
                    onCheckPQRs = onCheckPQRs,
                    onCheckFavorites = onCheckFavorites,
                    onChangePreference = onChangePreference,
                    onCheckReviews = onCheckReviews,
                    dynamicTheme = dynamicTheme
                )
                Spacer(modifier=Modifier.height(30.dp))
                Surface(modifier=Modifier.fillMaxWidth(),
                    color = Color.Transparent,
                    border = BorderStroke(5.dp, MaterialTheme.colorScheme.error),
                    shape = RoundedCornerShape(50.dp)
                ){
                    Button(onClick = onSignOut,
                        colors= ButtonDefaults.buttonColors(MaterialTheme.colorScheme.surfaceVariant)){
                        Text(text="Cerrar Sesión", color = MaterialTheme.colorScheme.error, style=MaterialTheme.typography.titleMedium)
                    }
                }
                Spacer(modifier=Modifier.height(30.dp))

            }
        }

    }
}

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview(){
    AppTheme(useSensor = false, darkTheme = true){
        Surface(){
            ProfileScreen()
        }
    }
}