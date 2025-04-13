package de.kifo.common.util;

import de.kifo.common.enums.message.Message;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class EmbedUtils {

    public static MessageEmbed getEmbedMessageByText(String text, Message.MessageType messageType) {
        return new EmbedBuilder()
                .setColor(messageType.getEmbedColor())
                .setDescription(text)
                .build();
    }
}
