package de.kifo.common.enums.message;

import de.kifo.JavaBot;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.awt.Color;

import static de.kifo.common.enums.message.Message.MessageType.ERROR;
import static de.kifo.common.util.EmbedUtils.getEmbedMessageByText;
import static java.awt.Color.MAGENTA;
import static java.awt.Color.ORANGE;
import static java.awt.Color.RED;

@Getter
@AllArgsConstructor
public enum Message {

    WRONG_COMMAND_SYNTAX_HELP(ERROR, "Nutze \"/\" anstelle von \"!\", um Befehle auszuführen.");

    private final MessageType messageType;
    private final String message;

    public void sendToTextChannel(TextChannel textChannel, JavaBot javaBot) {
        textChannel.sendMessageEmbeds(getEmbedMessageByText(this.message, this.messageType)).queue();
    }

    @Getter
    @AllArgsConstructor
    public enum MessageType {

        MESSAGE(MAGENTA),
        INFO(ORANGE),
        ERROR(RED);

        private final Color embedColor;
    }
}
