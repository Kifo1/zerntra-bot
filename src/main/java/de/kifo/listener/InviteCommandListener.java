package de.kifo.listener;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class InviteCommandListener extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        Member member = event.getMember();
        MessageChannel messageChannel = event.getMessageChannel();

        if (event.getCommandString().equalsIgnoreCase("/invite")) {
            event.getHook().sendMessage("Verschicke diesen Link, um jemanden zum Server einzuladen: ").queue();
            TextChannel textChannel = event.getMember().getGuild().getTextChannelById(event.getChannelId());
            textChannel.sendMessage(textChannel.createInvite().setMaxAge(3600).complete().getUrl()).queue();
            event.reply("Du kannst den Link für eine Stunde nutzen.").queue();
        }
    }
}
