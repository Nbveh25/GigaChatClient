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
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.kazan.itis.bikmukhametov.feature.profile.impl.R

@Composable
fun UserInfoSection() {
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
            InfoRow(
                label = stringResource(R.string.profile_user_name),
                value = stringResource(R.string.profile_empty_value),
            )
            InfoRow(
                label = stringResource(R.string.profile_email),
                value = stringResource(R.string.profile_empty_value),
            )
            InfoRow(
                label = stringResource(R.string.profile_phone),
                value = stringResource(R.string.profile_empty_value),
            )
            InfoRow(
                label = stringResource(R.string.profile_tokens),
                value = stringResource(R.string.profile_empty_value),
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
