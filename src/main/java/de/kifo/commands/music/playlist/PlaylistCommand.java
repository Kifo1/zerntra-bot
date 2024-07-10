package de.kifo.commands.music.playlist;

import de.kifo.JavaBot;
import de.kifo.commands.handle.CommandBase;
import de.kifo.commands.music.PlayCommand;
import de.kifo.common.api.model.Playlist;
import de.kifo.common.enums.PlaylistAction;
import de.kifo.common.exceptions.CommandException;
import de.kifo.common.music.PlayerManager;
import net.dv8tion.jda.api.EmbedBuilder;
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

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static com.google.common.collect.ImmutableList.of;
import static de.kifo.common.enums.exception.CommandExceptionType.NOT_IN_SPEECH_CHANNEL;
import static de.kifo.common.enums.exception.CommandExceptionType.NO_PERMISSION;
import static de.kifo.common.enums.exception.CommandExceptionType.PLAYLIST_ACCESS_MODIFIER_NEEDED;
import static de.kifo.common.enums.exception.CommandExceptionType.PLAYLIST_ALREADY_EXISTS;
import static de.kifo.common.enums.exception.CommandExceptionType.PLAYLIST_NOT_FOUND;
import static de.kifo.common.util.EmbedUtils.MessageType.MESSAGE;
import static java.awt.Color.MAGENTA;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.Optional.empty;
import static net.dv8tion.jda.api.interactions.commands.OptionType.STRING;

@CommandBase.BotCommand(name = "playlist", description = "Erstelle und spiele Playlists ab.", hasOptions = true)
public class PlaylistCommand extends CommandBase {

    @Inject
    private JavaBot javaBot;

    public PlaylistCommand(@NotNull BotCommand command) {
        super(command);
    }

    @Override
    public void execute(Member member, TextChannel textChannel, List<OptionMapping> options, SlashCommandInteractionEvent event) throws CommandException {
        PlaylistAction action = Arrays.stream(PlaylistAction.values())
                .filter(playlistAction -> playlistAction.getActionName().equalsIgnoreCase(options.get(0).getAsString()))
                .findFirst()
                .get();
        String playlistName = options.get(1).getAsString();
        Optional<Boolean> publicAccess = options.size() >= 3 ? Optional.of(options.get(2).getAsString().equalsIgnoreCase("Öffentlich")) : empty();

        Playlist playlist = javaBot.getApi().getPlaylistByName(playlistName);
        if (nonNull(playlist) && playlist.getUserId() != member.getUser().getIdLong() && !playlist.getPublicAccess()) {
            throw new CommandException(NO_PERMISSION, event, javaBot);
        }

        switch (action) {
            case CREATE_PLAYLIST -> {
                if (isNull(javaBot.getApi().getPlaylistByName(playlistName))) {
                    if (publicAccess.isEmpty()) {
                        throw new CommandException(PLAYLIST_ACCESS_MODIFIER_NEEDED, event, javaBot);
                    }
                    javaBot.getApi().updatePlaylist(playlistName, new Playlist(0L, event.getUser().getIdLong(), playlistName, publicAccess.get(), of()));
                    event.replyEmbeds(javaBot.getEmbedUtils().getEmbedMessageByText("Du hast die Playlist \"" + playlistName + "\" erfolgreich erstellt.", MESSAGE)).queue();
                } else {
                    throw new CommandException(PLAYLIST_ALREADY_EXISTS, event, javaBot);
                }
            }
            case DELETE_PLAYLIST -> {
                if (nonNull(playlist)) {
                    if (playlist.getUserId() != event.getUser().getIdLong()) {
                        throw new CommandException(NO_PERMISSION, event, javaBot);
                    }
                    javaBot.getApi().deletePlaylist(playlistName);
                    event.replyEmbeds(javaBot.getEmbedUtils().getEmbedMessageByText("Du hast die Playlist \"" + playlistName + "\" gelöscht.", MESSAGE)).queue();
                } else {
                    throw new CommandException(PLAYLIST_NOT_FOUND, event, javaBot);
                }
            }
            case INFO -> {
                if (isNull(playlist)) {
                    throw new CommandException(PLAYLIST_NOT_FOUND, event, javaBot);
                }
                EmbedBuilder builder = new EmbedBuilder();
                builder.setColor(MAGENTA);
                AtomicInteger songNumber = new AtomicInteger(1);

                playlist.getSongs().forEach(songName -> {
                    builder.addField("Lied " + songNumber + ": ", songName, false);
                    songNumber.getAndIncrement();
                });
                event.replyEmbeds(builder.build()).queue();
            }
            case PLAY -> {
                if (isNull(playlist)) {
                    throw new CommandException(PLAYLIST_NOT_FOUND, event, javaBot);
                }
                GuildVoiceState guildVoiceState = member.getVoiceState();

                if (isNull(guildVoiceState) || isNull(guildVoiceState.getChannel()) || isNull(guildVoiceState.getChannel().asVoiceChannel())) {
                    throw new CommandException(NOT_IN_SPEECH_CHANNEL, event, javaBot);
                }

                VoiceChannel voiceChannel = guildVoiceState.getChannel().asVoiceChannel();
                PlayerManager playerManager = javaBot.getPlayerManager();
                AudioManager manager = voiceChannel.getGuild().getAudioManager();
                manager.openAudioConnection(voiceChannel);

                playlist.getSongs().forEach(song -> {
                    playerManager.play(event.getGuild(), "ytsearch:" + song + " audio", event.getUser().getIdLong());
                });
                event.replyEmbeds(javaBot.getEmbedUtils().getEmbedMessageByText(playlist.getName() + " wurde gestartet. Du kannst du aktuelle Liste mit /songlist ansehen.", MESSAGE)).queue();
                PlayCommand.map.put(voiceChannel.getGuild().getIdLong(), textChannel);
            }
        }
    }

    @Override
    public void autoComplete(String optionName, CommandAutoCompleteInteractionEvent event) {
        List<Command.Choice> replyChoices = new ArrayList<>();

        switch (optionName.toLowerCase()) {
            case "aktion" -> replyChoices = Arrays.stream(PlaylistAction.values())
                    .filter(action -> !action.isModificationAction())
                    .filter(action -> action.getActionName().toLowerCase().contains(event.getFocusedOption().getValue().toLowerCase()))
                    .map(action -> new Command.Choice(action.getActionName(), action.getActionName()))
                    .toList();
            case "playlist" -> replyChoices = javaBot.getApi().getPlaylistsListByUserId(event.getUser().getIdLong()).stream()
                    .filter(playlist -> playlist.getName().toLowerCase().contains(event.getFocusedOption().getValue().toLowerCase()))
                    .map(playlist -> new Command.Choice(playlist.getName(), playlist.getName()))
                    .limit(25)
                    .toList();
            case "sichtbarkeit" -> replyChoices = of(new Command.Choice("Öffentlich", "Öffentlich"),
                                                     new Command.Choice("Privat", "Privat"));
        }

        event.replyChoices(replyChoices).queue();
    }

    @Override
    public List<OptionData> getOptions() {
        return of(new OptionData(STRING, "aktion", "Aktion, die ausgeführt werden soll.", true, true),
                  new OptionData(STRING, "playlist", "Die Playlist, auf die zugegriffen werden soll.", true, true),
                  new OptionData(STRING, "sichtbarkeit", "Wähle, ob die Playlist öffentlich oder privat sein soll.", false, true));
    }
}
