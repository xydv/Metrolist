package com.metrolist.music.ui.screens.settings.integrations

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import com.metrolist.music.LocalPlayerAwareWindowInsets
import com.metrolist.music.R
import com.metrolist.music.constants.WebhookUrlKey
import com.metrolist.music.ui.component.IconButton
import com.metrolist.music.ui.component.PreferenceEntry
import com.metrolist.music.ui.component.PreferenceGroupTitle
import com.metrolist.music.ui.component.TextFieldDialog
import com.metrolist.music.ui.utils.backToMain
import com.metrolist.music.utils.rememberPreference

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WebhookSettings(
    navController: NavController,
    scrollBehavior: TopAppBarScrollBehavior,
) {
    var webhookUrl by rememberPreference(WebhookUrlKey, "")
    var showEditDialog by rememberSaveable { mutableStateOf(false) }

    if (showEditDialog) {
        var tempUrl by rememberSaveable { mutableStateOf(webhookUrl) }

        TextFieldDialog(
            onDismiss = { showEditDialog = false },
            icon = { Icon(painterResource(R.drawable.language), null) },
            onDone = {
                webhookUrl = it
                showEditDialog = false
            },
            singleLine = true,
            isInputValid = { it.isNotEmpty() },
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Webhook Integration") },
                scrollBehavior = scrollBehavior,
                navigationIcon = {
                    IconButton(
                        onClick = navController::navigateUp,
                        onLongClick = navController::backToMain,
                    ) {
                        Icon(
                            painterResource(R.drawable.arrow_back),
                            contentDescription = null,
                        )
                    }
                }
            )
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { paddingValues ->
        Column(
            Modifier
                .padding(paddingValues)
                .windowInsetsPadding(LocalPlayerAwareWindowInsets.current.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Bottom))
                .verticalScroll(rememberScrollState())
        ) {
            PreferenceGroupTitle(title = "Webhook")

            PreferenceEntry(
                title = { Text("Webhook URL") },
                description = if (webhookUrl.isBlank()) "No URL configured" else webhookUrl,
                icon = { Icon(painterResource(R.drawable.link), null) },
                onClick = { showEditDialog = true }
            )
        }
    }
}