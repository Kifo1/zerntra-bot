package de.kifo.commands.oldcommands;

import de.kifo.JavaBot;
import de.kifo.commands.oldcommands.types.ServerCommand;
import de.kifo.database.utils.UserCommandsDataUtils;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import javax.inject.Inject;

public class InviteCmd implements ServerCommand {

    @Inject
    private JavaBot javaBot;

    @Override
    public void executeCommand(Member member, TextChannel channel, Message message) {
        UserCommandsDataUtils userCommandsDataUtils = javaBot.getInjector().getInstance(UserCommandsDataUtils.class);
        userCommandsDataUtils.addInviteCommand(member.getGuild().getIdLong(), member.getUser().getIdLong(), 1);
        String[] args = message.getContentRaw().split(" ");

        if(args[0].equalsIgnoreCase("!invite")) {
            channel.sendMessage("Verschicke diesen Link, um jemanden zum Server einzuladen: ").queue();
            channel.sendMessage(channel.createInvite().setMaxAge(3600).complete().getUrl()).queue();
            channel.sendMessage("Du kannst den Link für eine Stunde nutzen.").queue();
        }
    }
}