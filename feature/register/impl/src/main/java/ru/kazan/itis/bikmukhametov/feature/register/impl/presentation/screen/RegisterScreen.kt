package ru.kazan.itis.bikmukhametov.feature.register.impl.presentation.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import java.util.regex.Pattern
import ru.kazan.itis.bikmukhametov.designsystem.appuicomponent.AppOutlinedButton
import ru.kazan.itis.bikmukhametov.designsystem.appuicomponent.AppPasswordTextField
import ru.kazan.itis.bikmukhametov.designsystem.appuicomponent.AppPrimaryButton
import ru.kazan.itis.bikmukhametov.designsystem.appuicomponent.AppTextField
import ru.kazan.itis.bikmukhametov.designsystem.appuicomponent.AppTopBar
import ru.kazan.itis.bikmukhametov.feature.register.impl.R

@Composable
fun RegisterScreen(
    onNavigateToLogin: () -> Unit = {},
    onNavigateBack: () -> Unit = {},
    viewModel: RegisterViewModel = hiltViewModel(),
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val focusManager = LocalFocusManager.current

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is RegisterEffect.ShowSnackbar -> snackbarHostState.showSnackbar(effect.message)
                RegisterEffect.NavigateToLogin -> onNavigateToLogin()
            }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            AppTopBar(
                title = stringResource(R.string.register_title),
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.register_back),
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
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(64.dp))
            Text(
                text = stringResource(R.string.register_brand_title),
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 32.dp),
            )
            RegisterForm(
                state = uiState,
                onIntent = viewModel::onIntent,
                focusManager = focusManager,
            )
        }
    }
}

@Composable
private fun RegisterForm(
    state: RegisterUiState,
    onIntent: (RegisterIntent) -> Unit,
    focusManager: FocusManager,
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val emailPattern = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$",
        Pattern.CASE_INSENSITIVE,
    )
    val isEmailInvalid = state.emailInput.isNotBlank() &&
        !emailPattern.matcher(state.emailInput).matches()

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        AppTextField(
            value = state.emailInput,
            onValueChange = { onIntent(RegisterIntent.EmailChanged(it)) },
            label = stringResource(R.string.register_email_label),
            isError = isEmailInvalid || state.emailError != null,
            supportingText = when {
                isEmailInvalid -> {
                    {
                        Text(
                            text = stringResource(R.string.register_error_invalid_email),
                            color = MaterialTheme.colorScheme.error,
                        )
                    }
                }
                state.emailError != null -> {
                    {
                        Text(
                            text = state.emailError,
                            color = MaterialTheme.colorScheme.error,
                        )
                    }
                }
                else -> null
            },
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Next,
                keyboardType = KeyboardType.Email,
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) },
            ),
            enabled = !state.isLoading,
        )

        AppPasswordTextField(
            value = state.passwordInput,
            onValueChange = { onIntent(RegisterIntent.PasswordChanged(it)) },
            label = stringResource(R.string.register_password_label),
            isPasswordVisible = state.isPasswordVisible,
            onToggleVisibility = { onIntent(RegisterIntent.TogglePasswordVisibility) },
            isError = state.passwordError != null,
            supportingText = state.passwordError?.let { errorText ->
                {
                    Text(
                        text = errorText,
                        color = MaterialTheme.colorScheme.error,
                    )
                }
            },
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Next,
                keyboardType = KeyboardType.Password,
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) },
            ),
            enabled = !state.isLoading,
        )

        AppPasswordTextField(
            value = state.confirmPasswordInput,
            onValueChange = { onIntent(RegisterIntent.ConfirmPasswordChanged(it)) },
            label = stringResource(R.string.register_confirm_password_label),
            isPasswordVisible = state.isConfirmPasswordVisible,
            onToggleVisibility = { onIntent(RegisterIntent.ToggleConfirmPasswordVisibility) },
            isError = state.confirmPasswordError != null,
            supportingText = state.confirmPasswordError?.let { errorText ->
                {
                    Text(
                        text = errorText,
                        color = MaterialTheme.colorScheme.error,
                    )
                }
            },
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done,
                keyboardType = KeyboardType.Password,
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    focusManager.clearFocus()
                    keyboardController?.hide()
                    onIntent(RegisterIntent.RegisterButtonClicked)
                },
            ),
            enabled = !state.isLoading,
        )

        AppPrimaryButton(
            text = stringResource(R.string.register_primary_button),
            onClick = { onIntent(RegisterIntent.RegisterButtonClicked) },
            enabled = state.isButtonEnabled && !state.isLoading,
            isLoading = state.isLoading,
            modifier = Modifier.fillMaxWidth(),
        )

        AnimatedVisibility(
            visible = state.isNetworkError && !state.isLoading,
            enter = fadeIn() + slideInVertically(),
        ) {
            AppOutlinedButton(
                text = stringResource(R.string.register_retry_button),
                onClick = { onIntent(RegisterIntent.RetryButtonClicked) },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
