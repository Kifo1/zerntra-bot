package de.kifo.commands.music;

import com.google.inject.Inject;
import de.kifo.JavaBot;
import de.kifo.commands.handle.CommandBase;
import de.kifo.common.exceptions.CommandException;
import de.kifo.common.music.PlayerManager;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.managers.AudioManager;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.google.common.collect.ImmutableList.of;
import static de.kifo.common.enums.exception.ExceptionType.NOT_IN_SPEECH_CHANNEL;
import static java.util.Objects.isNull;

@CommandBase.BotCommand(name = "playlist", description = "Wähle eine Playlist, die abgespielt werden soll.", hasOptions = true)
public class PlaylistCommand extends CommandBase {

    @Inject
    private JavaBot javaBot;

    public PlaylistCommand(@NotNull BotCommand command) {
        super(command);
    }

    @Override
    public void execute(Member member, TextChannel textChannel, List<OptionMapping> options, SlashCommandInteractionEvent event) throws CommandException {
        GuildVoiceState guildVoiceState = member.getVoiceState();

        if (isNull(guildVoiceState) || isNull(guildVoiceState.getChannel())) {
            throw new CommandException(NOT_IN_SPEECH_CHANNEL, event);
        }

        VoiceChannel voiceChannel = guildVoiceState.getChannel().asVoiceChannel();
        PlayerManager playerManager = javaBot.getPlayerManager();
        AudioManager audioManager = voiceChannel.getGuild().getAudioManager();
        audioManager.openAudioConnection(voiceChannel);

        //TODO Fetch Playlist from API
        //TODO Check for options => Random queue?
        //TODO Add each song to the current song list => Check if url is present => Use name query if there is no url
    }

    @Override
    public void autoComplete(String optionName, CommandAutoCompleteInteractionEvent event) {

    }

    @Override
    public List<OptionData> getOptions() {
        return of();
    }
}
