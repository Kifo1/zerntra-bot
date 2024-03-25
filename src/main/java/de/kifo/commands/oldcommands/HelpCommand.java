package de.kifo.commands.oldcommands;

import de.kifo.JavaBot;
import de.kifo.commands.oldcommands.types.ServerCommand;
import de.kifo.database.utils.UserCommandsDataUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import javax.inject.Inject;

import static java.awt.Color.MAGENTA;

public class HelpCommand implements ServerCommand {

    @Inject
    private JavaBot javaBot;

    @Override
    public void executeCommand(Member member, TextChannel textChannel, Message message) {
        UserCommandsDataUtils userCommandsDataUtils = javaBot.getInjector().getInstance(UserCommandsDataUtils.class);
        userCommandsDataUtils.addHelpCommand(member.getGuild().getIdLong(), member.getUser().getIdLong(), 1);

        //TODO Create Embed with all commands
        EmbedBuilder builder = new EmbedBuilder();
        builder.setColor(MAGENTA);
    }

}
