package de.kifo.common.services;

import de.kifo.JavaBot;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.*;
import java.util.concurrent.CompletableFuture;

import static de.kifo.JavaBot.messageService;
import static de.kifo.common.services.MessageService.MessageType.*;
import static java.awt.Color.*;

@Data
public class MessageService {

    private final JavaBot javaBot;

    public MessageEmbed message(String text) {
        return getEmbedMessageByText(text, MESSAGE);
    }

    public MessageEmbed info(String text) {
        return getEmbedMessageByText(text, INFO);
    }

    public MessageEmbed error(String text) {
        return getEmbedMessageByText(text, ERROR);
    }

    private MessageEmbed getEmbedMessageByText(String text, MessageType messageType) {
        return new EmbedBuilder()
                .setColor(messageType.getEmbedColor())
                .setDescription(text)
                .build();
    }

    @Getter
    @AllArgsConstructor
    public static class UpdatableMessage {
        private Message message;

        public CompletableFuture<Message> update(MessageEmbed embed) {
            return message.editMessageEmbeds(embed).submit()
                    .thenApply(updated -> {
                        this.message = updated;
                        return updated;
                    });
        }

        public CompletableFuture<Message> fail() {
            return update(messageService.error("Etwas ist schief gelaufen."));
        }
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
