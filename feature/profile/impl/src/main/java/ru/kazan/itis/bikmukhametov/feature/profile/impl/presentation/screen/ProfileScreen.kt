package ru.kazan.itis.bikmukhametov.feature.profile.impl.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.kazan.itis.bikmukhametov.designsystem.appuicomponent.AppTopBar
import ru.kazan.itis.bikmukhametov.feature.profile.impl.R
import ru.kazan.itis.bikmukhametov.feature.profile.impl.presentation.component.PhotoSection
import ru.kazan.itis.bikmukhametov.feature.profile.impl.presentation.component.ThemeSection
import ru.kazan.itis.bikmukhametov.feature.profile.impl.presentation.component.UserInfoSection

@Composable
fun ProfileScreen(
    onOpenDrawer: () -> Unit,
    onPhotoClick: () -> Unit = {},
    onThemeChange: (Boolean) -> Unit = {},
    onSignOutClick: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            AppTopBar(
                title = stringResource(R.string.profile_title),
                navigationIcon = {
                    IconButton(onClick = onOpenDrawer) {
                        Icon(
                            imageVector = Icons.Filled.Menu,
                            contentDescription = null,
                        )
                    }
                },
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            PhotoSection(
                onPhotoClick = onPhotoClick,
            )
            UserInfoSection()
            ThemeSection(
                onThemeChange = onThemeChange,
            )
            Spacer(modifier = Modifier.weight(1f))
            Button(
                onClick = onSignOutClick,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(text = stringResource(R.string.profile_logout))
            }
        }
    }
}


