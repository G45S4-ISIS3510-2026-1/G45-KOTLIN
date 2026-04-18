package com.example.g45_kotlin

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.g45_kotlin.data.auth.AuthHolder
import com.example.g45_kotlin.data.local.SearchHistoryManager
import com.example.g45_kotlin.data.novelty.NoveltyDto
import com.example.g45_kotlin.data.novelty.NoveltyType
import com.example.g45_kotlin.data.user.TutorSummaryDto
import com.example.g45_kotlin.ui.auth.LoginScreen
import com.example.g45_kotlin.ui.home.HomeScreen
import com.example.g45_kotlin.ui.novelties.NoveltyScreen
import com.example.g45_kotlin.ui.provisional_profile.ProvisionalScreen
import com.example.g45_kotlin.ui.reservation.gateway.ReservationGateWay
import com.example.g45_kotlin.ui.reservation.summary.ReservationSummary
import com.example.g45_kotlin.ui.theme.AppTheme
import com.example.g45_kotlin.ui.tutor.catalog.CatalogoContent
import com.example.g45_kotlin.ui.tutor.catalog.TutorDetailScreen
import com.example.g45_kotlin.ui.tutor.catalog.TutorViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


enum class AppDestinations(
    val label: String,
    val icon: ImageVector,
) {
    HOME("Inicio", Icons.Default.Home),
    CATALOG("Catalogo", Icons.Default.Search),
    AGENDA("Agenda", Icons.Default.DateRange),
    MESSAGES("Mensajes", Icons.Default.Email),
    PROFILE("Perfil", Icons.Default.AccountCircle),
}


@Composable
fun MainScreen(modifier: Modifier = Modifier,
               sharedViewModel: TutorViewModel = viewModel(),
               navController: NavHostController= rememberNavController()){
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = currentBackStackEntry?.destination
    val context=LocalContext.current
    val authRepository=AuthHolder.authRepo
    //Verify logged in with firebase auth instance
    var isLogged by rememberSaveable { mutableStateOf(authRepository.isUserLoggedIn()) }
    //Check if logged in, then show navigation bar
    val mostrarBottomBar = currentDestination?.route in setOf(AppDestinations.HOME.label, AppDestinations.CATALOG.label, AppDestinations.AGENDA.label, AppDestinations.MESSAGES.label, AppDestinations.PROFILE.label)
    //Define shared viewmodel for tutor catalog and detail
    val scope = rememberCoroutineScope()
    val tutorsState by sharedViewModel.uiState.collectAsState()
    val searchManager= SearchHistoryManager.getInstance()

    fun onNoveltyClick (novelty: NoveltyDto){
        val entityId=novelty.entityId
        if (entityId.isNotBlank()){
            if (novelty.type==NoveltyType.SESSION.label || novelty.type==NoveltyType.INCOMING_SESION.label){
                navController.navigate(Routes.reservationSummary+"/$entityId")
            }else{
                sharedViewModel.onTutorSelected(TutorSummaryDto(
                    id =entityId,
                    name = "Desconocido",
                    major = "Desconocido",
                    rating = 0.0,
                    sessionPrice = 0
                ))
                navController.navigate(Routes.tutorDetail)
            }
        }
    }

    Scaffold(modifier=modifier.fillMaxSize(),
        bottomBar = {
            AnimatedVisibility(visible = mostrarBottomBar && isLogged ){
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                ){
                    AppDestinations.entries.forEach{
                        NavigationBarItem(
                            selected = currentDestination?.route == it.label,
                            onClick = { navController.navigate(it.label) },
                            icon = {Icon(imageVector = it.icon, contentDescription = it.label)},
                            label = {Text(text = it.label)}
                        )
                    }
                }
            }
        }
    ){paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Routes.home,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(Routes.home) {
                if(isLogged){
                    HomeScreen(
                        onTutorClick = {tutor->sharedViewModel.onTutorSelected(tutor);
                            searchManager.saveQuery(tutor.id);
                            navController.navigate(Routes.tutorDetail)}
                    , onMoreClick = {navController.navigate(Routes.catalog)}
                    , onSessionClick = {id->navController.navigate(Routes.reservationSummary+"/$id")}
                    )
                }else{
                    LoginScreen(onLoginSuccess = {isLogged=true})
                }
            }
            composable(Routes.catalog) {
                CatalogoContent(
                    uiState = tutorsState,
                    onSearchTextChange = sharedViewModel::onSearchTextChange,
                    onOrderChange = sharedViewModel::onOrderChange,
                    onFacultadChange = sharedViewModel::onFacultadChange,
                    onTutorClick = {tutor->
                        searchManager.saveQuery(tutor.id)
                        sharedViewModel.onTutorSelected(tutor)
                        navController.navigate(Routes.tutorDetail)
                    },
                    onRetry = {
                        sharedViewModel.cargarTutores()
                    }
                )
            }
            composable(Routes.agenda) { PendingPage(modifier=Modifier.fillMaxSize())}
            composable(Routes.messages) { NoveltyScreen(modifier=Modifier.fillMaxSize(), onNoveltyClick = {novelty->onNoveltyClick(novelty)})}
            composable(Routes.profile) {
                Surface(modifier=Modifier.fillMaxSize()){
                    Column(verticalArrangement = Arrangement.SpaceBetween,
                        horizontalAlignment = Alignment.CenterHorizontally){
                        ProvisionalScreen(onReservationClick = {id->navController.navigate(Routes.reservationSummary+"/${id}")})
                        Surface(){
                            Button(onClick = {
                                scope.launch(Dispatchers.IO) {
                                    authRepository.signOut();
                                }
                                navController.navigate(route= Routes.home){
                                    popUpTo(Routes.home) {
                                        inclusive = false
                                    }
                                    launchSingleTop = true
                                }
                                isLogged=false
                            }) {
                                Text(text = "Sign Out")
                            }
                        }
                    }
                }
            }
            composable(Routes.tutorDetail) {
                val tutor = tutorsState.selectedTutor
                val reseñas=tutorsState.selectedTutorReviews
                val skills=tutorsState.selectedTutorSkills
                if (tutor != null) {
                    TutorDetailScreen(tutor = tutor, reseñas=reseñas, skills=skills,
                        onBack = { navController.popBackStack()},
                        onBook = { navController.navigate(Routes.reservationGateway+"/${tutor.id}")})
                }
            }
            composable(Routes.reservationSummary+"/{session_id}") {
                backStackEntry ->
                val sessionId = backStackEntry.arguments?.getString("session_id")
                if (sessionId != null) {
                    Surface(modifier=modifier.padding(10.dp)){
                        ReservationSummary(onBack = {navController.popBackStack()})
                    }
                }
            }
            composable(Routes.reservationGateway+"/{tutor_id}") {
                backStackEntry ->
                val tutorId = backStackEntry.arguments?.getString("tutor_id")
                if (tutorId != null) {
                    Surface(modifier=modifier.padding(10.dp)){
                        ReservationGateWay(onBack = {navController.popBackStack()}, onConfirm = {sessionId->navController.navigate(Routes.reservationSummary+"/$sessionId"){
                            popUpTo(Routes.home){
                                inclusive=false
                            }
                            launchSingleTop=true
                        } })
                    }
                }

            }
        }
    }
}

@Composable
fun PendingPage(modifier: Modifier=Modifier){
    Card(
        modifier = modifier
            .fillMaxWidth(0.8f)
            .aspectRatio(1f)
            .clip(RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiary)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(48.dp),
                color = MaterialTheme.colorScheme.primary,
                strokeWidth = 4.dp
            )

            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "In Progress...",
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onTertiary
            )
        }
    }
}

@Composable
fun ProvisionalSessionCard(modifier: Modifier=Modifier, onClick:()->Unit, id:String){
    Card(modifier=modifier, colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiary),
        onClick = onClick) {
        Text(text = id)
    }
}


@Preview(showBackground = true)
@Composable
fun MainScreenPreview(){
    AppTheme(darkTheme = false){
        MainScreen()
    }
}

