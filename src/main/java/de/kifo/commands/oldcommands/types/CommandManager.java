package de.kifo.commands.oldcommands.types;

import de.kifo.JavaBot;
import de.kifo.commands.oldcommands.InfoCommand;
import de.kifo.commands.oldcommands.InviteCmd;
import de.kifo.music.commands.PlayCommand;
import de.kifo.music.commands.SkipCommand;
import de.kifo.music.commands.PlayListCommand;
import de.kifo.music.commands.StopCommand;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.util.concurrent.ConcurrentHashMap;

public class CommandManager {

    private JavaBot javaBot;
    public ConcurrentHashMap<String, ServerCommand> commands;

    public CommandManager(JavaBot javaBot) {
        this.javaBot = javaBot;
        this.commands = new ConcurrentHashMap<>();

        this.commands.put("invite", javaBot.getInjector().getInstance(InviteCmd.class));
        this.commands.put("play", javaBot.getInjector().getInstance(PlayCommand.class));
        this.commands.put("skip", javaBot.getInjector().getInstance(SkipCommand.class));
        this.commands.put("stop", javaBot.getInjector().getInstance(StopCommand.class));
        this.commands.put("info", javaBot.getInjector().getInstance(InfoCommand.class));
        this.commands.put("playlist",javaBot.getInjector().getInstance(PlayListCommand.class));
        //TODO Add command to bot
        //this.commands.put("help", new HelpCommand());
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