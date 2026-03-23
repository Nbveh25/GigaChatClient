package ru.kazan.itis.bikmukhametov.gigachat.ui.placeholder

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.kazan.itis.bikmukhametov.gigachat.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImagesPlaceholder(
    onOpenDrawer: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.nav_images_title)) },
                navigationIcon = {
                    IconButton(onClick = onOpenDrawer) {
                        Icon(
                            imageVector = Icons.Filled.Menu,
                            contentDescription = stringResource(R.string.cd_open_menu),
                        )
                    }
                },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
        ) {
            Text(
                text = stringResource(R.string.placeholder_images),
                style = MaterialTheme.typography.bodyLarge,
            )
        }
    }
}

object DrawerDestination {
    const val NewChatAction = "action_new_chat"

    data class Item(
        val route: String,
        val labelRes: Int,
        val icon: androidx.compose.ui.graphics.vector.ImageVector,
    )

    val search = Item(
        route = ru.kazan.itis.bikmukhametov.gigachat.navigation.NavRoutes.ChatList,
        labelRes = R.string.drawer_search_chats,
        icon = Icons.Filled.Search,
    )
    val newChat = Item(
        route = NewChatAction,
        labelRes = R.string.drawer_new_chat,
        icon = Icons.Filled.Add,
    )
    val images = Item(
        route = ru.kazan.itis.bikmukhametov.gigachat.navigation.NavRoutes.Images,
        labelRes = R.string.drawer_images,
        icon = Icons.Filled.Image,
    )
    val chats = Item(
        route = ru.kazan.itis.bikmukhametov.gigachat.navigation.NavRoutes.ChatList,
        labelRes = R.string.drawer_chats,
        icon = Icons.Filled.Home,
    )
    val profile = Item(
        route = ru.kazan.itis.bikmukhametov.gigachat.navigation.NavRoutes.Profile,
        labelRes = R.string.drawer_profile,
        icon = Icons.Filled.Person,
    )

    val items = listOf(search, newChat, images, chats, profile)
}
