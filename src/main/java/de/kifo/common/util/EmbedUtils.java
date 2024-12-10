package de.kifo.common.util;

import de.kifo.JavaBot;
import de.kifo.common.enums.message.Message;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import javax.inject.Inject;

public class EmbedUtils {

    @Inject
    private JavaBot javaBot;

    public MessageEmbed getEmbedMessageByText(String text, Message.MessageType messageType) {
        return new EmbedBuilder()
                .setColor(messageType.getEmbedColor())
                .setDescription(text)
                .build();
    }
}
