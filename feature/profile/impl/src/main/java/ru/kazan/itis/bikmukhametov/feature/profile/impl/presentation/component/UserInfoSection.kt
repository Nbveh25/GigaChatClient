package ru.kazan.itis.bikmukhametov.feature.profile.impl.presentation.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.kazan.itis.bikmukhametov.designsystem.appuicomponent.AppPrimaryButton
import ru.kazan.itis.bikmukhametov.designsystem.appuicomponent.AppTextField
import ru.kazan.itis.bikmukhametov.feature.profile.impl.R

@Composable
fun UserInfoSection(
    userName: String,
    email: String,
    phone: String,
    tokens: String,
    emptyValue: String,
    isUpdatingUserName: Boolean,
    onUpdateUserName: (String) -> Unit,
) {
    var editableUserName by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(userName, emptyValue) {
        editableUserName = if (userName == emptyValue) "" else userName
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
        ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            AppTextField(
                value = editableUserName,
                onValueChange = { editableUserName = it },
                label = stringResource(R.string.profile_user_name),
                enabled = !isUpdatingUserName,
            )
            AppPrimaryButton(
                text = stringResource(R.string.profile_save_user_name),
                onClick = { onUpdateUserName(editableUserName) },
                isLoading = isUpdatingUserName,
                enabled = editableUserName.isNotBlank(),
            )
            InfoRow(
                label = stringResource(R.string.profile_email),
                value = email,
            )
            InfoRow(
                label = stringResource(R.string.profile_tokens),
                value = tokens,
            )
        }
    }
}


@Composable
private fun InfoRow(
    label: String,
    value: String,
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}
