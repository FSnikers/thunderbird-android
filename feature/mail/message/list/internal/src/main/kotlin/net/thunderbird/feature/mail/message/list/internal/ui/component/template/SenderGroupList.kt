package net.thunderbird.feature.mail.message.list.internal.ui.component.template

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import app.k9mail.core.ui.compose.designsystem.atom.card.CardDefaults
import app.k9mail.core.ui.compose.designsystem.atom.card.CardFilled
import app.k9mail.core.ui.compose.designsystem.atom.text.TextBodyMedium
import app.k9mail.core.ui.compose.designsystem.atom.text.TextBodySmall
import app.k9mail.core.ui.compose.designsystem.atom.text.TextLabelSmall
import app.k9mail.core.ui.compose.designsystem.atom.text.TextTitleMedium
import app.k9mail.core.ui.compose.designsystem.atom.text.TextTitleSmall
import app.k9mail.core.ui.compose.designsystem.atom.textfield.TextFieldOutlined
import net.thunderbird.core.ui.compose.theme2.MainTheme
import net.thunderbird.feature.mail.message.list.R
import net.thunderbird.feature.mail.message.list.internal.ui.component.MessageItemAvatar
import net.thunderbird.feature.mail.message.list.internal.ui.component.SenderMessageGroup
import net.thunderbird.feature.mail.message.list.internal.ui.component.filterSenderGroups
import net.thunderbird.feature.mail.message.list.internal.ui.component.groupMessagesBySender
import net.thunderbird.feature.mail.message.list.ui.state.Avatar
import net.thunderbird.feature.mail.message.list.ui.state.MessageItemUi

@Composable
internal fun SenderGroupList(
    messages: List<MessageItemUi>,
    selectedSenderKey: String?,
    onSenderClick: (String) -> Unit,
    onBackToContacts: () -> Unit,
    senderMessagesContent: @Composable (List<MessageItemUi>) -> Unit,
    modifier: Modifier = Modifier,
) {
    val senderGroups = groupMessagesBySender(
        messages = messages,
        unknownSenderName = stringResource(id = R.string.message_list_contacts_unknown_sender),
    )
    val selectedSenderGroup = selectedSenderKey?.let { senderKey ->
        senderGroups.firstOrNull { it.senderKey == senderKey }
    }

    if (selectedSenderGroup != null) {
        Column(modifier = modifier.fillMaxSize()) {
            SelectedSenderHeader(
                senderGroup = selectedSenderGroup,
                onBackToContacts = onBackToContacts,
            )
            senderMessagesContent(selectedSenderGroup.messages)
        }
    } else {
        SenderGroupOverview(
            senderGroups = senderGroups,
            onSenderClick = onSenderClick,
            modifier = modifier,
        )
    }
}

@Composable
private fun SenderGroupOverview(
    senderGroups: List<SenderMessageGroup>,
    onSenderClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var searchQuery by remember { mutableStateOf("") }
    val filteredSenderGroups = filterSenderGroups(senderGroups, searchQuery)

    Column(modifier = modifier.fillMaxSize()) {
        ContactsSearchHeader(
            searchQuery = searchQuery,
            onSearchQueryChange = { searchQuery = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(MainTheme.spacings.default),
        )

        if (filteredSenderGroups.isEmpty()) {
            ContactsEmptyState(
                hasSearchQuery = searchQuery.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(MainTheme.spacings.default),
            )
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(
                    items = filteredSenderGroups,
                    key = { senderGroup -> senderGroup.senderKey },
                ) { senderGroup ->
                    SenderGroupItem(
                        senderGroup = senderGroup,
                        onClick = { onSenderClick(senderGroup.senderKey) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                horizontal = MainTheme.spacings.default,
                                vertical = MainTheme.spacings.half,
                            ),
                    )
                }
            }
        }
    }
}

@Composable
private fun ContactsSearchHeader(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(MainTheme.spacings.half),
        modifier = modifier,
    ) {
        TextFieldOutlined(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            label = stringResource(id = R.string.message_list_contacts_search_hint),
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun ContactsEmptyState(
    hasSearchQuery: Boolean,
    modifier: Modifier = Modifier,
) {
    CardFilled(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MainTheme.colors.surfaceContainerLow),
        shape = MainTheme.shapes.large,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(MainTheme.spacings.half),
            modifier = Modifier
                .fillMaxWidth()
                .padding(MainTheme.spacings.double),
        ) {
            TextTitleSmall(
                text = stringResource(
                    id = if (hasSearchQuery) {
                        R.string.message_list_contacts_empty_search_title
                    } else {
                        R.string.message_list_contacts_empty_title
                    },
                ),
                textAlign = TextAlign.Center,
            )
            TextBodySmall(
                text = stringResource(id = R.string.message_list_contacts_empty_description),
                color = MainTheme.colors.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun SelectedSenderHeader(
    senderGroup: SenderMessageGroup,
    onBackToContacts: () -> Unit,
    modifier: Modifier = Modifier,
) {
    CardFilled(
        onClick = onBackToContacts,
        modifier = modifier
            .fillMaxWidth()
            .padding(MainTheme.spacings.default),
        colors = CardDefaults.cardColors(containerColor = MainTheme.colors.primaryContainer.copy(alpha = 0.18f)),
        shape = MainTheme.shapes.large,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(MainTheme.spacings.default),
            modifier = Modifier.padding(MainTheme.spacings.default),
        ) {
            SenderAvatar(senderGroup = senderGroup)
            Column(modifier = Modifier.weight(1f)) {
                TextLabelSmall(
                    text = stringResource(id = R.string.message_list_contacts_back),
                    color = MainTheme.colors.primary,
                )
                TextTitleMedium(
                    text = senderGroup.displayName,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                )
                TextBodySmall(
                    text = stringResource(id = R.string.message_list_contacts_message_count, senderGroup.messageCount),
                    color = MainTheme.colors.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun SenderGroupItem(
    senderGroup: SenderMessageGroup,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    CardFilled(
        onClick = onClick,
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MainTheme.colors.surfaceContainerLowest.copy(alpha = 0.72f)),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = MainTheme.elevations.level1),
        shape = MainTheme.shapes.large,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(MainTheme.spacings.default),
            modifier = Modifier.padding(MainTheme.spacings.default),
        ) {
            SenderAvatar(senderGroup = senderGroup)
            Column(modifier = Modifier.weight(1f)) {
                TextTitleMedium(
                    text = senderGroup.displayName,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                )
                TextBodyMedium(
                    text = senderGroup.latestMessage.subject,
                    color = MainTheme.colors.onSurfaceVariant,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                )
                TextBodySmall(
                    text = stringResource(id = R.string.message_list_contacts_message_count, senderGroup.messageCount),
                    color = MainTheme.colors.onSurfaceVariant,
                )
            }
            if (senderGroup.unreadCount > 0) {
                TextLabelSmall(
                    text = senderGroup.unreadCount.toString(),
                    color = MainTheme.colors.primary,
                    modifier = Modifier.padding(horizontal = MainTheme.spacings.half),
                )
            }
        }
    }
}

@Composable
private fun SenderAvatar(
    senderGroup: SenderMessageGroup,
    modifier: Modifier = Modifier,
) {
    val avatar = senderGroup.avatar ?: Avatar.Monogram(senderGroup.displayName.toMonogram())
    Box(modifier = modifier.size(48.dp)) {
        MessageItemAvatar(
            avatar = avatar,
            showMessageAvatar = true,
            onAvatarClick = {},
            modifier = Modifier.fillMaxSize(),
        )
    }
}

private fun String.toMonogram(): String {
    return trim()
        .split(Regex("\\s+"))
        .filter { it.isNotBlank() }
        .take(2)
        .joinToString(separator = "") { it.first().uppercase() }
        .ifBlank { "?" }
}
