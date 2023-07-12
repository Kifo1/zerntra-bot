package de.kifo.listener;

import de.kifo.Main;
import de.kifo.database.files.CreateUserFile;
import de.kifo.database.utils.UserDataUtils;
import de.kifo.database.utils.UserErrorsDataUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;

public class MessageListener extends ListenerAdapter {

    public void onMessageReceived(MessageReceivedEvent event) {
        long guildId = event.getGuild().getIdLong();
        long userId = event.getMember().getUser().getIdLong();
        String message = event.getMessage().getContentRaw();

        if(!UserDataUtils.fileExist(event.getGuild().getIdLong(), event.getMember().getUser().getIdLong())) {
            CreateUserFile.createUserFile(event.getGuild().getIdLong(), event.getMember().getUser().getIdLong());
        }

        UserDataUtils.addMessages(guildId, userId, 1);

        if(message.startsWith("!")) {
            String[] args = message.substring(1).split(" ");

            if(!event.getChannel().getType().equals(ChannelType.TEXT)) {
                EmbedBuilder builder = new EmbedBuilder();
                builder.setDescription("Du kannst nur in Textkanälen Befehle verwenden. " + event.getMember().getAsMention());
                builder.setColor(Color.MAGENTA);
                event.getChannel().sendMessageEmbeds(builder.build()).queue();
                UserErrorsDataUtils.addWrongChannelError(guildId, userId, 1);
                return;
            }

            if(args.length > 0) {
                if(!Main.getInstance().getCommandManager().perform(args[0], event.getMember(), event.getChannel().asTextChannel(), event.getMessage())) {
                    EmbedBuilder builder = new EmbedBuilder();
                    builder.setDescription("Dieser Befehl existiert nicht. " + event.getMember().getAsMention());
                    builder.setColor(Color.MAGENTA);
                    event.getChannel().sendMessageEmbeds(builder.build()).queue();
                    UserErrorsDataUtils.addNotCommandError(guildId, userId, 1);
                } else {
                    UserDataUtils.addCommandQuantity(guildId, userId, 1);
                }
            }
        }
    }
}