package net.thunderbird.feature.mail.message.list.internal.ui.component

import net.thunderbird.feature.mail.message.list.ui.state.Avatar
import net.thunderbird.feature.mail.message.list.ui.state.MessageItemUi

internal data class SenderMessageGroup(
    val senderKey: String,
    val displayName: String,
    val avatar: Avatar?,
    val messages: List<MessageItemUi>,
) {
    val messageCount: Int = messages.size
    val unreadCount: Int = messages.count { message ->
        message.state == MessageItemUi.State.Unread || message.state == MessageItemUi.State.New
    }
    val latestMessage: MessageItemUi = messages.first()
}

internal fun groupMessagesBySender(messages: List<MessageItemUi>): List<SenderMessageGroup> {
    return messages
        .groupBy { message -> message.senderGroupKey() }
        .map { (senderKey, groupedMessages) ->
            val latestMessage = groupedMessages.first()
            SenderMessageGroup(
                senderKey = senderKey,
                displayName = latestMessage.senders.displayName.ifBlank { senderKey },
                avatar = latestMessage.senders.avatar,
                messages = groupedMessages,
            )
        }
        .sortedWith(
            compareByDescending<SenderMessageGroup> { it.unreadCount > 0 }
                .thenBy { it.displayName.lowercase() },
        )
}

private fun MessageItemUi.senderGroupKey(): String {
    return senders.displayName.trim().ifBlank { id }.lowercase()
}
