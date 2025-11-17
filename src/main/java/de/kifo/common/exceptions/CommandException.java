package de.kifo.common.exceptions;

import de.kifo.common.enums.exception.ExceptionType;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import static de.kifo.JavaBot.messageService;

public class CommandException extends Exception {

    public CommandException(ExceptionType commandExceptionType, SlashCommandInteractionEvent event) {
        super(commandExceptionType.name());
        event.replyEmbeds(messageService.error(commandExceptionType.getMessage())).setEphemeral(true).queue();
    }
}
