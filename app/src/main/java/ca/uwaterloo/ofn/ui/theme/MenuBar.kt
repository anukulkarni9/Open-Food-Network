package ca.uwaterloo.ofn.ui.theme

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

@Composable
fun MenuBar(
    onNavigationIconClick: () -> Unit
) {
    TopAppBar(
        title = {
                null
        },
        backgroundColor = Color.Transparent,
        contentColor = Color(63, 115, 36),
        modifier = Modifier.fillMaxWidth(),
        elevation = 0.dp,
        navigationIcon = {
            IconButton(onClick = onNavigationIconClick) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Toggle drawer"
                )
            }
        }
    )
}