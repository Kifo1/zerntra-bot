package de.kifo.oldcommands;

import de.kifo.oldcommands.types.ServerCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import static de.kifo.database.utils.UserCommandsDataUtils.addHelpCommand;
import static java.awt.Color.MAGENTA;

public class HelpCommand implements ServerCommand {

    @Override
    public void executeCommand(Member member, TextChannel textChannel, Message message) {
        addHelpCommand(member.getGuild().getIdLong(), member.getUser().getIdLong(), 1);

        //TODO Create Embed with all commands
        EmbedBuilder builder = new EmbedBuilder();
        builder.setColor(MAGENTA);
    }

}
