package ru.kazan.itis.bikmukhametov.feature.chatlist.impl.presentation.component

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.kazan.itis.bikmukhametov.feature.chatlist.impl.R

@Composable
fun ChatListFloatingActionButton(
    isCreating: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    FloatingActionButton(
        onClick = {
            if (!isCreating) {
                onClick()
            }
        },
        modifier = modifier,
    ) {
        if (isCreating) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                strokeWidth = 2.dp,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )
        } else {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = stringResource(R.string.chat_list_fab_new),
            )
        }
    }
}
