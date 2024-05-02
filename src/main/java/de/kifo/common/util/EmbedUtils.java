package de.kifo.common.util;

import de.kifo.JavaBot;
import lombok.Getter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import javax.inject.Inject;

import java.awt.Color;

import static java.awt.Color.MAGENTA;
import static java.awt.Color.ORANGE;
import static java.awt.Color.RED;

public class EmbedUtils {

    @Inject
    private JavaBot javaBot;

    public MessageEmbed getEmbedMessageByText(String text, MessageType messageType) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(messageType.embedColor);
        embedBuilder.setDescription(text);
        return embedBuilder.build();
    }

    @Getter
    public enum MessageType {

        MESSAGE(MAGENTA),
        INFO(ORANGE),
        ERROR(RED);

        public final Color embedColor;

        MessageType(Color embedColor) {
           this.embedColor = embedColor;
        }
    }
}
