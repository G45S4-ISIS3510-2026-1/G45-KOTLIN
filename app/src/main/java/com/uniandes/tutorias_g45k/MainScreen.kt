package com.uniandes.tutorias_g45k

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
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
import androidx.compose.runtime.LaunchedEffect
import com.uniandes.tutorias_g45k.utilities.AnalyticsManager
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.uniandes.tutorias_g45k.data.auth.AuthHolder
import com.uniandes.tutorias_g45k.data.local.SearchHistoryManager
import com.uniandes.tutorias_g45k.data.novelty.NoveltyDto
import com.uniandes.tutorias_g45k.data.novelty.NoveltyType
import com.uniandes.tutorias_g45k.data.recommendation.TutorSummaryDto
import com.uniandes.tutorias_g45k.ui.MainNoveltyViewModel
import com.uniandes.tutorias_g45k.ui.agenda.AgendaScreen
import com.uniandes.tutorias_g45k.ui.auth.LoginScreen
import com.uniandes.tutorias_g45k.ui.home.HomeScreen
import com.uniandes.tutorias_g45k.ui.novelties.NoveltyScreen
import com.uniandes.tutorias_g45k.ui.profile.ProfileScreen
import com.uniandes.tutorias_g45k.ui.profile.pages.edit.TutorPriceEditScreen
import com.uniandes.tutorias_g45k.ui.profile.pages.edit.TutorScheduleEditScreen
import com.uniandes.tutorias_g45k.ui.profile.pages.pqrs.PqrListScreen
import com.uniandes.tutorias_g45k.ui.profile.pages.pqrs.PqrScreen
import com.uniandes.tutorias_g45k.ui.profile.pages.pqrs.PqrViewModel
import com.uniandes.tutorias_g45k.ui.profile.pages.pqrs.PqrViewModelFactory
import com.uniandes.tutorias_g45k.ui.profile.pages.reviews.ReviewListScreen
import com.uniandes.tutorias_g45k.ui.profile.pages.reservations.ReservationListScreen
import com.uniandes.tutorias_g45k.ui.reservation.gateway.ReservationGateWay
import com.uniandes.tutorias_g45k.ui.reservation.summary.ReservationSummary
import com.uniandes.tutorias_g45k.ui.theme.AppTheme
import com.uniandes.tutorias_g45k.ui.tutor.become.BecomeTutorPriceScreen
import com.uniandes.tutorias_g45k.ui.tutor.become.BecomeTutorScheduleScreen
import com.uniandes.tutorias_g45k.ui.tutor.become.BecomeTutorSkillsScreen
import com.uniandes.tutorias_g45k.ui.tutor.become.BecomeTutorViewModel
import com.uniandes.tutorias_g45k.ui.tutor.become.BecomeTutorViewModelFactory
import com.uniandes.tutorias_g45k.ui.tutor.catalog.CatalogoContent
import com.uniandes.tutorias_g45k.ui.tutor.catalog.TutorDetailScreen
import com.uniandes.tutorias_g45k.ui.tutor.catalog.TutorViewModel
import com.uniandes.tutorias_g45k.utilities.NetworkMonitor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


enum class AppDestinations(
    val label: String,
    val icon: ImageVector,
) {
    HOME("Inicio", Icons.Default.Home),
    CATALOG("Catalogo", Icons.Default.Search),
    AGENDA("Agenda", Icons.Default.DateRange),
    NOVELTIES("Novedades", Icons.Default.Notifications),
    PROFILE("Perfil", Icons.Default.AccountCircle),
}
private fun processNotificationIntent(intent: Intent?, navController: NavHostController, shared: TutorViewModel, isLogged:Boolean=true) {
    //Retrive intent data
    val type = intent?.getStringExtra("type")
    val entityId = intent?.getStringExtra("entity_id")

    if (type != null && entityId != null && isLogged) {
        when (type) {
            "tutor" -> {
                shared.onTutorSelected(TutorSummaryDto(
                    id = entityId,
                    name ="Tutor",
                    major = "Carrera",
                    rating = 0.0,
                    sessionPrice = 0
                ))
                navController.navigate(Routes.tutorDetail){
                    popUpTo(Routes.home){
                        inclusive=false
                    }
                    launchSingleTop=true
                }
            }
            "session" -> {
                navController.navigate(Routes.reservationSummary+"/$entityId"){
                    popUpTo(Routes.home){
                        inclusive=false
                    }
                    launchSingleTop=true
                }
            }
            else -> {}
        }
        intent.replaceExtras(Bundle())
        intent.data = null
    }else{
        navController.navigate(Routes.home)
    }
}


@Composable
fun MainScreen(modifier: Modifier = Modifier,
               isDynamic:Boolean=false,
               sharedViewModel: TutorViewModel = viewModel(),
               noveltyViewModel: MainNoveltyViewModel = viewModel(),
               navController: NavHostController= rememberNavController(),
               onChangePreference:()->Unit={},
               intent: Intent?=null
){

    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = currentBackStackEntry?.destination
    val context=LocalContext.current
    val authRepository=AuthHolder.authRepo
    //Verify logged in with firebase auth instance
    var isLogged by rememberSaveable { mutableStateOf(authRepository.isUserLoggedIn()) }
    //Check if logged in, then show navigation bar
    val showBottomBar = currentDestination?.route in setOf(AppDestinations.HOME.label, AppDestinations.CATALOG.label, AppDestinations.AGENDA.label, AppDestinations.NOVELTIES.label, AppDestinations.PROFILE.label)
    //Define shared viewmodel for tutor catalog and detail
    val scope = rememberCoroutineScope()
    val tutorsState by sharedViewModel.uiState.collectAsState()
    val searchManager= SearchHistoryManager.getInstance()
    val unReadCount by noveltyViewModel.unreadCount.collectAsState()
    val becomeTutorViewModel: BecomeTutorViewModel = viewModel(
        factory = BecomeTutorViewModelFactory(context)
    )
    val isOnline by NetworkMonitor.isOnline.collectAsState()

    // Rastreo automático de pantallas para Analytics y Crashlytics
    LaunchedEffect(currentDestination) {
        currentDestination?.route?.let { route ->
            AnalyticsManager.logScreenView(route)
        }
    }
    LaunchedEffect(intent) {
        intent?.let {
            processNotificationIntent(it, navController, sharedViewModel, isLogged)
        }
    }

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
        topBar = {
            AnimatedVisibility(visible = !isOnline) {
                Surface(
                    color = Color.Black.copy(alpha = 0.7f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .padding(vertical = 20.dp, horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CloudOff,
                            contentDescription = "Offline",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Modo Offline - Funciones y Datos limitados",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        },
        bottomBar = {
            AnimatedVisibility(visible = showBottomBar && isLogged ){
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                ){
                    AppDestinations.entries.forEach{
                        NavigationBarItem(
                            selected = currentDestination?.route == it.label,
                            onClick = { navController.navigate(it.label) },
                            icon = {
                                if (it.label==AppDestinations.NOVELTIES.label){
                                    BadgedBox(badge={
                                        if (unReadCount>0){
                                            Badge(containerColor = MaterialTheme.colorScheme.error) {
                                                Text(text = unReadCount.toString())
                                            }
                                        }
                                    }){
                                        Icon(imageVector = it.icon, contentDescription = it.label)
                                    }
                                }else{
                                    Icon(imageVector = it.icon, contentDescription = it.label)
                                }
                           },
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
                    ,onBecomeTutor = {navController.navigate(Routes.becomeTutorSkills)}
                    )
                }else{
                    LoginScreen(onLoginSuccess = {isLogged=true;sharedViewModel.cargarTutores()}, onFail = {isLogged=false})
                }
            }
            composable(Routes.catalog) {
                CatalogoContent(
                    uiState = tutorsState,
                    onSearchTextChange = sharedViewModel::onSearchTextChange,
                    onOrderChange = sharedViewModel::onOrderChange,
                    onFacultadChange = sharedViewModel::onFacultadChange,
                    onOnlyFavoritesChange = sharedViewModel::onOnlyFavoritesChange,
                    onTutorClick = {tutor->
                        searchManager.saveQuery(tutor.id)
                        sharedViewModel.onTutorSelected(tutor)
                        navController.navigate(Routes.tutorDetail)
                    },
                    onLoadNextPage = sharedViewModel::loadNextPage,
                    onRetry = {
                        sharedViewModel.cargarTutores()
                    }
                )
            }
            composable(Routes.editPrice){TutorPriceEditScreen( onSubmit = {navController.navigate(Routes.profile)}, onBack = {navController.popBackStack()})}
            composable(Routes.agenda) {
                AgendaScreen(Modifier.fillMaxSize(), onSessionClick = {id->navController.navigate(Routes.reservationSummary+"/${id}")})
            }
            composable(Routes.novelties) { NoveltyScreen(modifier=Modifier.fillMaxSize(), onNoveltyClick = {novelty->onNoveltyClick(novelty)})}
            composable(Routes.profile) {
                ProfileScreen(modifier=Modifier.fillMaxSize(), dynamicTheme = isDynamic, onChangePreference = onChangePreference,
                    onCheckReservations = {navController.navigate(Routes.reservationList)},
                    onCheckPQRs = {navController.navigate(Routes.pqrList)},
                    onCheckReviews = {navController.navigate(Routes.reviewList)},
                    onStartPqr = { navController.navigate(Routes.pqrs) },
                    onSignOut = {
                    scope.launch(Dispatchers.IO) {
                        authRepository.signOut();
                        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                        notificationManager.cancelAll()
                    }
                    navController.navigate(route= Routes.home){
                        popUpTo(Routes.home) {
                            inclusive = false
                        }
                        launchSingleTop = true
                    }
                    isLogged=false
                    },
                    onEditProfile = {
                        navController.navigate(Routes.editPrice)
                    },
                    onEditAvailability = {
                        navController.navigate(Routes.editAvailability)
                    }
                )


            }
            composable(Routes.editAvailability){
                TutorScheduleEditScreen(onBack = {navController.popBackStack()}, onSuccess = {navController.navigate(Routes.profile)})
            }
            composable(Routes.pqrList){
                Surface(modifier=Modifier.fillMaxSize()){
                    com.uniandes.tutorias_g45k.ui.profile.pages.pqrs.PqrListScreen(
                        onBack = {navController.popBackStack()}
                    )
                }
            }
            composable(Routes.pqrs) {
                val pqrViewModel: PqrViewModel = viewModel(factory = PqrViewModelFactory(context))
                PqrScreen(onBack = { navController.popBackStack() }, viewModel = pqrViewModel)
            }
            composable(Routes.reservationList){
                Surface(modifier=Modifier.fillMaxSize()){
                    Column(verticalArrangement = Arrangement.SpaceBetween,
                        horizontalAlignment = Alignment.CenterHorizontally){
                        ReservationListScreen (onReservationClick = {id->navController.navigate(Routes.reservationSummary+"/${id}")}, onBack = {navController.popBackStack()})
                    }
                }
            }
            composable(Routes.reviewList){
                Surface(modifier=Modifier.fillMaxSize()){
                    Column(verticalArrangement = Arrangement.SpaceBetween,
                        horizontalAlignment = Alignment.CenterHorizontally){
                        ReviewListScreen (
                            onReviewClick = {review->navController.navigate(Routes.reviewDetail+"/${review.id}")}, 
                            onBack = {navController.popBackStack()}
                        )
                    }
                }
            }
            composable(Routes.reviewDetail+"/{reviewId}") { backStackEntry ->
                val reviewId = backStackEntry.arguments?.getString("reviewId")
                if (reviewId != null) {
                    Surface(modifier=Modifier.fillMaxSize()){
                        com.uniandes.tutorias_g45k.ui.profile.pages.reviews.ReviewDetailScreen(
                            reviewId = reviewId, 
                            onBack = {navController.popBackStack()}
                        )
                    }
                }
            }
            composable(Routes.tutorDetail) {
                val tutor = tutorsState.selectedTutor
                val reseñas=tutorsState.selectedTutorReviews
                val skills=tutorsState.selectedTutorSkills
                if (tutor != null) {
                    val tutorId = tutor.id ?: ""
                    val isFavorite = tutorsState.favoriteTutorIds.contains(tutorId)
                    TutorDetailScreen(tutor = tutor, reseñas=reseñas, skills=skills,
                        isFavorite = isFavorite,
                        isLoadingReviews = tutorsState.isLoadingReviews,
                        viewModel = sharedViewModel, // PASAR EL VIEWMODEL COMPARTIDO CORRECTO
                        onBack = { navController.popBackStack()},
                        onBook = { navController.navigate(Routes.reservationGateway+"/${tutorId}")},
                        onToggleFavorite = { sharedViewModel.toggleFavorite(tutorId) },
                        onCreateReview = sharedViewModel::createReview
                    )
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
            composable(Routes.becomeTutorSkills) {
                BecomeTutorSkillsScreen(
                    viewModel = becomeTutorViewModel,
                    onNext = { navController.navigate(Routes.becomeTutorPrice) },
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Routes.becomeTutorPrice) {
                BecomeTutorPriceScreen(
                    viewModel = becomeTutorViewModel,
                    onNext = { navController.navigate(Routes.becomeTutorSchedule) },
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Routes.becomeTutorSchedule) {
                BecomeTutorScheduleScreen(
                    viewModel = becomeTutorViewModel,
                    onPublish = {
                        becomeTutorViewModel.publishProfile()
                    },
                    onSuccess = {
                        navController.navigate(Routes.home) {
                            popUpTo(Routes.home) { inclusive = true }
                        }
                    },
                    onBack = { navController.popBackStack() }
                )
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
