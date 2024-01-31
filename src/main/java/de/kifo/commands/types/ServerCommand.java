package de.kifo.commands.types;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

public interface ServerCommand {

    public default void executeCommand(Member member, TextChannel channel, Message message) throws InterruptedException {

    }
}
