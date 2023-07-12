package de.kifo.commands.types;

import de.kifo.commands.InfoCommand;
import de.kifo.commands.InviteCommand;
import de.kifo.music.commands.PlayCommand;
import de.kifo.music.commands.SkipCommand;
import de.kifo.music.commands.StopCommand;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.util.concurrent.ConcurrentHashMap;

public class CommandManager {

    public ConcurrentHashMap<String, ServerCommand> commands;

    public CommandManager() {
        this.commands = new ConcurrentHashMap<>();

        this.commands.put("invite", new InviteCommand());
        this.commands.put("play", new PlayCommand());
        this.commands.put("skip", new SkipCommand());
        this.commands.put("stop", new StopCommand());
        this.commands.put("info", new InfoCommand());
    }

    public boolean perform(String command, Member member, TextChannel channel, Message message) {
        ServerCommand cmd;
        if((cmd = this.commands.get(command.toLowerCase())) != null) {
            try {
                cmd.executeCommand(member, channel, message);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return true;
        }

        return false;
    }
}