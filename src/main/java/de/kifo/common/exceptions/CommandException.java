package de.kifo.common.exceptions;

import de.kifo.common.enums.exception.ExceptionType;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import static de.kifo.common.enums.message.Message.MessageType.ERROR;
import static de.kifo.common.util.EmbedUtils.getEmbedMessageByText;

public class CommandException extends Exception {

    public CommandException(ExceptionType commandExceptionType, SlashCommandInteractionEvent event) {
        super(commandExceptionType.name());
        event.replyEmbeds(getEmbedMessageByText(commandExceptionType.getMessage(), ERROR)).setEphemeral(true).queue();
    }
}
