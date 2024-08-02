package de.kifo.common.util;

import de.kifo.JavaBot;
import lombok.AllArgsConstructor;
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
        return new EmbedBuilder()
                .setColor(messageType.embedColor)
                .setDescription(text)
                .build();
    }

    @Getter
    @AllArgsConstructor
    public enum MessageType {

        MESSAGE(MAGENTA),
        INFO(ORANGE),
        ERROR(RED);

        public final Color embedColor;
    }
}
