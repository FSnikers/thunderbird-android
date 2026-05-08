package net.thunderbird.feature.mail.message.list.internal.ui.component

import androidx.compose.ui.graphics.Color
import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.isEqualTo
import net.thunderbird.feature.account.AccountIdFactory
import net.thunderbird.feature.mail.message.list.ui.state.Account
import net.thunderbird.feature.mail.message.list.ui.state.ComposedAddressUi
import net.thunderbird.feature.mail.message.list.ui.state.MessageItemUi
import org.junit.Test

class SenderMessageGroupTest {

    private val account = Account(
        id = AccountIdFactory.create(),
        color = Color.Blue,
    )

    @Test
    fun `GIVEN same sender with different casing WHEN grouping by sender THEN messages are grouped together`() {
        // Arrange
        val messages = listOf(
            createMessage(id = "1", sender = "Alice", state = MessageItemUi.State.Unread),
            createMessage(id = "2", sender = "alice", state = MessageItemUi.State.Read),
            createMessage(id = "3", sender = "Bob", state = MessageItemUi.State.Read),
        )

        // Act
        val result = groupMessagesBySender(messages)

        // Assert
        assertThat(result.map { it.displayName }).containsExactly("Alice", "Bob")
        assertThat(result.first().messageCount).isEqualTo(2)
        assertThat(result.first().unreadCount).isEqualTo(1)
    }

    @Test
    fun `GIVEN unread and read sender groups WHEN grouping by sender THEN unread groups are shown first`() {
        // Arrange
        val messages = listOf(
            createMessage(id = "1", sender = "Bob", state = MessageItemUi.State.Read),
            createMessage(id = "2", sender = "Alice", state = MessageItemUi.State.Unread),
            createMessage(id = "3", sender = "Charlie", state = MessageItemUi.State.New),
        )

        // Act
        val result = groupMessagesBySender(messages)

        // Assert
        assertThat(result.map { it.displayName }).containsExactly("Alice", "Charlie", "Bob")
    }

    private fun createMessage(
        id: String,
        sender: String,
        state: MessageItemUi.State,
    ): MessageItemUi {
        return MessageItemUi(
            state = state,
            id = id,
            account = account,
            senders = ComposedAddressUi(displayName = sender),
            subject = "Subject $id",
            excerpt = "Excerpt $id",
            formattedReceivedAt = "Now",
            hasAttachments = false,
            starred = false,
            encrypted = false,
            answered = false,
            forwarded = false,
            selected = false,
        )
    }
}
