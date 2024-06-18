package de.kifo.common.exceptions;

import de.kifo.JavaBot;
import de.kifo.common.enums.exception.CommandExceptionType;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import static de.kifo.common.util.EmbedUtils.MessageType.ERROR;

public class CommandException extends Exception {

    public CommandException(CommandExceptionType commandExceptionType, TextChannel textChannel, JavaBot javaBot) {
        super(commandExceptionType.name());

        textChannel.sendMessageEmbeds(javaBot.getEmbedUtils().getEmbedMessageByText(commandExceptionType.getMessage(), ERROR));
    }
}
