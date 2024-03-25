package de.kifo.listener;

import de.kifo.JavaBot;
import de.kifo.database.utils.UserErrorsDataUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.inject.Inject;

import static de.kifo.database.files.CreateUserFile.createUserFile;
import static de.kifo.database.utils.UserDataUtils.addCommandQuantity;
import static de.kifo.database.utils.UserDataUtils.addMessages;
import static de.kifo.database.utils.UserDataUtils.fileExist;
import static de.kifo.database.utils.UserErrorsDataUtils.addNotCommandError;
import static java.awt.Color.MAGENTA;

public class MessageListener extends ListenerAdapter {

    @Inject
    private JavaBot javaBot;

    public void onMessageReceived(MessageReceivedEvent event) {
        long guildId = event.getGuild().getIdLong();
        long userId = event.getMember().getUser().getIdLong();
        String message = event.getMessage().getContentRaw();

        if(!fileExist(event.getGuild().getIdLong(), event.getMember().getUser().getIdLong())) {
            createUserFile(event.getGuild().getIdLong(), event.getMember().getUser().getIdLong());
        }

        addMessages(guildId, userId, 1);

        if(message.startsWith("!")) {
            String[] args = message.substring(1).split(" ");

            if(!event.getChannel().getType().equals(ChannelType.TEXT)) {
                EmbedBuilder builder = new EmbedBuilder();
                builder.setDescription("Du kannst nur in Textkanälen Befehle verwenden. " + event.getMember().getAsMention());
                builder.setColor(MAGENTA);
                event.getChannel().sendMessageEmbeds(builder.build()).queue();
                UserErrorsDataUtils.addWrongChannelError(guildId, userId, 1);
                return;
            }

            if(args.length > 0) {
                if(!javaBot.getCommandManager().perform(args[0], event.getMember(), event.getChannel().asTextChannel(), event.getMessage())) {
                    EmbedBuilder builder = new EmbedBuilder();
                    builder.setDescription("Dieser Befehl existiert nicht. " + event.getMember().getAsMention());
                    builder.setColor(MAGENTA);
                    event.getChannel().sendMessageEmbeds(builder.build()).queue();
                    addNotCommandError(guildId, userId, 1);
                } else {
                    addCommandQuantity(guildId, userId, 1);
                }
            }
        }
    }
}