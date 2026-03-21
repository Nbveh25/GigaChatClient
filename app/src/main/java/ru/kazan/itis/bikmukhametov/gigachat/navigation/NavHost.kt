package ru.kazan.itis.bikmukhametov.gigachat.navigation

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import kotlinx.coroutines.launch
import ru.kazan.itis.bikmukhametov.feature.auth.impl.presentation.screen.AuthScreen
import ru.kazan.itis.bikmukhametov.feature.register.impl.presentation.screen.RegisterScreen
import ru.kazan.itis.bikmukhametov.gigachat.R
import ru.kazan.itis.bikmukhametov.feature.chatlist.impl.presentation.screen.ChatListScreen
import ru.kazan.itis.bikmukhametov.feature.chatdetail.impl.presentation.screen.ChatDetailScreen
import ru.kazan.itis.bikmukhametov.gigachat.ui.placeholder.DrawerDestination
import ru.kazan.itis.bikmukhametov.gigachat.ui.placeholder.ImagesPlaceholder
import ru.kazan.itis.bikmukhametov.gigachat.ui.placeholder.ProfilePlaceholder

private fun isOnChatDetail(entry: NavBackStackEntry?): Boolean =
    entry?.arguments?.containsKey("chatId") == true

private fun isDrawerItemSelected(
    item: DrawerDestination.Item,
    entry: NavBackStackEntry?,
): Boolean {
    val route = entry?.destination?.route
    return when {
        item === DrawerDestination.search ->
            route == NavRoutes.ChatList
        item === DrawerDestination.chats ->
            route == NavRoutes.ChatList || isOnChatDetail(entry)
        item === DrawerDestination.profile ->
            route == NavRoutes.Profile
        item === DrawerDestination.images ->
            route == NavRoutes.Images
        else -> false
    }
}

@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val activity = LocalContext.current as ComponentActivity
    val appNavViewModel: AppNavViewModel = hiltViewModel(activity)

    LaunchedEffect(currentRoute) {
        drawerState.close()
    }

    ModalNavigationDrawer(
        modifier = modifier,
        drawerState = drawerState,
        gesturesEnabled = currentRoute != NavRoutes.Auth && currentRoute != NavRoutes.Register,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(Modifier.height(16.dp))
                Text(
                    text = stringResource(R.string.drawer_header),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(horizontal = 28.dp, vertical = 8.dp),
                )
                Spacer(Modifier.height(8.dp))
                DrawerDestination.items.forEach { item ->
                    NavigationDrawerItem(
                        icon = {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = null,
                            )
                        },
                        label = { Text(stringResource(item.labelRes)) },
                        selected = isDrawerItemSelected(item, navBackStackEntry),
                        onClick = {
                            when (item.route) {
                                DrawerDestination.NewChatAction -> {
                                    scope.launch {
                                        val id = appNavViewModel.createNewChat()
                                        navController.navigate(NavRoutes.chat(id))
                                    }
                                }
                                else -> {
                                    navController.navigate(item.route) {
                                        launchSingleTop = true
                                    }
                                }
                            }
                            scope.launch { drawerState.close() }
                        },
                    )
                }
            }
        },
    ) {
        NavHost(
            navController = navController,
            startDestination = NavRoutes.Auth,
            modifier = Modifier.fillMaxSize(),
        ) {
            composable(NavRoutes.Auth) {
                AuthScreen(
                    onNavigateToChats = {
                        navController.navigate(NavRoutes.ChatList) {
                            popUpTo(NavRoutes.Auth) { inclusive = true }
                        }
                    },
                    onNavigateToRegistration = {
                        navController.navigate(NavRoutes.Register)
                    },
                )
            }
            composable(NavRoutes.Register) {
                RegisterScreen(
                    onNavigateToLogin = {
                        navController.navigate(NavRoutes.Auth) {
                            popUpTo(NavRoutes.Auth) { inclusive = true }
                        }
                    },
                    onNavigateBack = { navController.popBackStack() },
                )
            }
            composable(NavRoutes.ChatList) {
                ChatListScreen(
                    onOpenDrawer = { scope.launch { drawerState.open() } },
                    onNavigateToChat = { chatId ->
                        navController.navigate(NavRoutes.chat(chatId))
                    },
                )
            }
            composable(
                route = NavRoutes.Chat,
                arguments = listOf(
                    navArgument("chatId") { type = NavType.StringType },
                ),
            ) { entry ->
                val chatId = entry.arguments?.getString("chatId").orEmpty()
                ChatDetailScreen(
                    chatId = chatId,
                    onBack = { navController.popBackStack() },
                )
            }
            composable(NavRoutes.Profile) {
                ProfilePlaceholder(
                    onOpenDrawer = { scope.launch { drawerState.open() } },
                )
            }
            composable(NavRoutes.Images) {
                ImagesPlaceholder(
                    onOpenDrawer = { scope.launch { drawerState.open() } },
                )
            }
        }
    }
}
