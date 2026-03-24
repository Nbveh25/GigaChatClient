package ru.kazan.itis.bikmukhametov.gigachat.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector
import ru.kazan.itis.bikmukhametov.gigachat.R

object DrawerDestination {
    const val NewChatAction = "action_new_chat"
    const val ImagesChatAction = "action_images_chat"

    data class Item(
        val route: String,
        val labelRes: Int,
        val icon: ImageVector,
    )

    val search = Item(
        route = NavRoutes.ChatList,
        labelRes = R.string.drawer_search_chats,
        icon = Icons.Filled.Search,
    )
    val newChat = Item(
        route = NewChatAction,
        labelRes = R.string.drawer_new_chat,
        icon = Icons.Filled.Add,
    )
    val images = Item(
        route = ImagesChatAction,
        labelRes = R.string.drawer_images,
        icon = Icons.Filled.Image,
    )
    val chats = Item(
        route = NavRoutes.ChatList,
        labelRes = R.string.drawer_chats,
        icon = Icons.Filled.Home,
    )
    val profile = Item(
        route = NavRoutes.Profile,
        labelRes = R.string.drawer_profile,
        icon = Icons.Filled.Person,
    )

    val items = listOf(search, newChat, images, chats, profile)
}
