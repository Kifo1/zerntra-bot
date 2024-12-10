package de.kifo.common.exceptions;

import de.kifo.JavaBot;
import de.kifo.common.enums.exception.ExceptionType;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import static de.kifo.common.enums.message.Message.MessageType.ERROR;

public class CommandException extends Exception {

    public CommandException(ExceptionType commandExceptionType, SlashCommandInteractionEvent event, JavaBot javaBot) {
        super(commandExceptionType.name());
        event.replyEmbeds(javaBot.getEmbedUtils().getEmbedMessageByText(commandExceptionType.getMessage(), ERROR)).setEphemeral(true).queue();
    }
}
