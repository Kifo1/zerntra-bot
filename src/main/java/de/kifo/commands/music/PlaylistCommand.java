package de.kifo.commands.music;

import com.google.inject.Inject;
import de.kifo.JavaBot;
import de.kifo.commands.handle.CommandBase;
import de.kifo.common.api.model.HistoryEntry;
import de.kifo.common.api.model.Playlist;
import de.kifo.common.exceptions.CommandException;
import de.kifo.common.music.PlayerManager;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.managers.AudioManager;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.collect.ImmutableList.of;
import static de.kifo.common.api.model.HistoryEntry.Type.PLAYLIST_PLAY;
import static de.kifo.common.enums.exception.ExceptionType.NOT_IN_SPEECH_CHANNEL;
import static de.kifo.common.enums.message.Message.MessageType.MESSAGE;
import static de.kifo.common.music.TrackScheduler.map;
import static de.kifo.common.util.EmbedUtils.getEmbedMessageByText;
import static java.lang.System.currentTimeMillis;
import static java.util.Collections.shuffle;
import static java.util.Objects.isNull;
import static net.dv8tion.jda.api.interactions.commands.OptionType.BOOLEAN;
import static net.dv8tion.jda.api.interactions.commands.OptionType.STRING;

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

        Long playlistId = options.get(0).getAsLong();
        boolean shuffle = options.size() > 1 && options.get(1).getAsBoolean();
        Playlist playlist = javaBot.getApi().getPlaylist(playlistId);

        List<String> queryUrls = new ArrayList<>(playlist.getPlaylistSongs().stream()
                .map(playlistSong -> {
                    String url = playlistSong.getUrl();
                    if (isNull(url) || !url.startsWith("http")) {
                        url = "ytsearch:" + playlistSong.getName() + " audio";
                    }
                    return url;
                })
                .toList());

        if (shuffle) {
            shuffle(queryUrls);
        }

        event.replyEmbeds(getEmbedMessageByText("Starte Playlist: " + playlist.getName(), MESSAGE)).queue();
        map.put(voiceChannel.getGuild().getIdLong(), textChannel);

        queryUrls.forEach(queryUrl -> playerManager.play(event.getGuild(), queryUrl, member.getIdLong()));

        javaBot.getApi().createHistoryEntry(new HistoryEntry(null, member.getIdLong(), PLAYLIST_PLAY, currentTimeMillis(),
                playlistId + ", " + textChannel.getName()));
    }

    @Override
    public void autoComplete(String optionName, CommandAutoCompleteInteractionEvent event) {
        if (optionName.equalsIgnoreCase("playlist")) {
            List<Command.Choice> replyChoices = javaBot.getApi().getAllPlaylistsByUser(event.getUser().getIdLong()).stream()
                    .filter(playlist -> getPlaylistOptionString(playlist).toLowerCase()
                            .contains(event.getFocusedOption().getValue().toLowerCase()))
                    .map(playlist -> new Command.Choice(getPlaylistOptionString(playlist), playlist.getId()))
                    .limit(25)
                    .toList();

            event.replyChoices(replyChoices).queue();
        }
    }

    @Override
    public List<OptionData> getOptions() {
        return of(new OptionData(STRING, "playlist", "Name der Playlist", true, true),
                  new OptionData(BOOLEAN, "shuffle", "Erstellt eine zufällige Reihenfolge", false, false));
    }

    private String getPlaylistOptionString(Playlist playlist) {
        return playlist.getName() + " - " + playlist.getOwner().getUserName();
    }
}
