package de.kifo.listener;

import de.kifo.JavaBot;
import de.kifo.database.files.CreateUserFile;
import de.kifo.database.utils.UserCommandsDataUtils;
import de.kifo.database.utils.UserDataUtils;
import de.kifo.database.utils.UserErrorsDataUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.inject.Inject;

import static java.awt.Color.MAGENTA;

public class MessageListener extends ListenerAdapter {

    @Inject
    private JavaBot javaBot;

    public void onMessageReceived(MessageReceivedEvent event) {
        UserErrorsDataUtils userErrorsDataUtils = javaBot.getInjector().getInstance(UserErrorsDataUtils.class);
        UserCommandsDataUtils userCommandsDataUtils = javaBot.getInjector().getInstance(UserCommandsDataUtils.class);
        UserDataUtils userDataUtils = javaBot.getInjector().getInstance(UserDataUtils.class);
        CreateUserFile createUserFile = javaBot.getInjector().getInstance(CreateUserFile.class);
        long guildId = event.getGuild().getIdLong();
        long userId = event.getMember().getUser().getIdLong();
        String message = event.getMessage().getContentRaw();

        if(!userDataUtils.fileExist(event.getGuild().getIdLong(), event.getMember().getUser().getIdLong())) {
            createUserFile.createUserFile(event.getGuild().getIdLong(), event.getMember().getUser().getIdLong());
        }

        userDataUtils.addMessages(guildId, userId, 1);

        if(message.startsWith("!")) {
            String[] args = message.substring(1).split(" ");

            if(!event.getChannel().getType().equals(ChannelType.TEXT)) {
                EmbedBuilder builder = new EmbedBuilder();
                builder.setDescription("Du kannst nur in Textkanälen Befehle verwenden. " + event.getMember().getAsMention());
                builder.setColor(MAGENTA);
                event.getChannel().sendMessageEmbeds(builder.build()).queue();
                userErrorsDataUtils.addWrongChannelError(guildId, userId, 1);
                return;
            }

            if(args.length > 0) {
                if(!javaBot.getCommandManager().perform(args[0], event.getMember(), event.getChannel().asTextChannel(), event.getMessage())) {
                    EmbedBuilder builder = new EmbedBuilder();
                    builder.setDescription("Dieser Befehl existiert nicht. " + event.getMember().getAsMention());
                    builder.setColor(MAGENTA);
                    event.getChannel().sendMessageEmbeds(builder.build()).queue();
                    userErrorsDataUtils.addNotCommandError(guildId, userId, 1);
                } else {
                    userDataUtils.addCommandQuantity(guildId, userId, 1);
                }
            }
        }
    }
}