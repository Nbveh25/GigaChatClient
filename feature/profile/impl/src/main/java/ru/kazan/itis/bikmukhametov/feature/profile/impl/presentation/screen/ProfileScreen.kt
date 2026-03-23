package ru.kazan.itis.bikmukhametov.feature.profile.impl.presentation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
    viewModel: ProfileViewModel = hiltViewModel(),
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.state.collectAsStateWithLifecycle()
    val emptyValue = stringResource(R.string.profile_empty_value)
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                ProfileEffect.OpenPhotoPicker -> onPhotoClick()
                ProfileEffect.SignedOut -> onSignOutClick()
                is ProfileEffect.ShowError -> snackbarHostState.showSnackbar(effect.message)
            }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
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
                onPhotoClick = { viewModel.onIntent(ProfileIntent.PhotoClicked) },
            )
            UserInfoSection(
                userName = uiState.userName ?: emptyValue,
                email = uiState.email ?: emptyValue,
                phone = uiState.phone ?: emptyValue,
                tokens = uiState.tokens ?: emptyValue,
            )
            ThemeSection(
                checked = uiState.isDarkTheme,
                onThemeChange = {
                    viewModel.onIntent(ProfileIntent.ThemeChanged(it))
                    onThemeChange(it)
                },
            )
            Spacer(modifier = Modifier.weight(1f))
            Button(
                onClick = { viewModel.onIntent(ProfileIntent.SignOutClicked) },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(text = stringResource(R.string.profile_logout))
            }
        }
    }
}


