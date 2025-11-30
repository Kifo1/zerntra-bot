package de.kifo.listener;

import com.google.inject.Inject;
import de.kifo.JavaBot;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import static de.kifo.JavaBot.messageService;
import static net.dv8tion.jda.api.entities.channel.ChannelType.TEXT;

public class MessageReceivedListener extends ListenerAdapter {

    @Inject
    private JavaBot javaBot;

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        String message = event.getMessage().getContentRaw();
        MessageChannelUnion messageChannel = event.getChannel();

        if (messageChannel.getType().equals(TEXT) && isWrongCommandSyntax(message)) {
            messageChannel.asTextChannel().sendMessageEmbeds(
                    messageService.error("Use \"/\" instead of \"!\" to execute commands.")).queue();
        }
    }

    private boolean isWrongCommandSyntax(String message) {
        if (!message.startsWith("!")) return false;
        String commandSubString = message.split(" ")[0].replaceAll("!", "");

        return javaBot.getRegistry().getCommands().stream()
                .anyMatch(botCommand -> botCommand.name().equalsIgnoreCase(commandSubString));
    }
}
