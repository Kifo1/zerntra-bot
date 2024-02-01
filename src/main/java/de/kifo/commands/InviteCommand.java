package de.kifo.commands;

import de.kifo.commands.types.ServerCommand;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import static de.kifo.database.utils.UserCommandsDataUtils.addInviteCommand;

public class InviteCommand implements ServerCommand {

    @Override
    public void executeCommand(Member member, TextChannel channel, Message message) {
        addInviteCommand(member.getGuild().getIdLong(), member.getUser().getIdLong(), 1);
        String[] args = message.getContentRaw().split(" ");

        if(args[0].equalsIgnoreCase("!invite")) {
            channel.sendMessage("Verschicke diesen Link, um jemanden zum Server einzuladen: ").queue();
            channel.sendMessage(channel.createInvite().setMaxAge(3600).complete().getUrl()).queue();
            channel.sendMessage("Du kannst den Link für eine Stunde nutzen.").queue();
        }
    }
}