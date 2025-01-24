package de.kifo.commands.music.playlist;

import com.google.inject.Inject;
import de.kifo.JavaBot;
import de.kifo.commands.handle.CommandBase;
import de.kifo.common.api.model.Playlist;
import de.kifo.common.api.model.Song;
import de.kifo.common.enums.PlaylistAction;
import de.kifo.common.exceptions.CommandException;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static de.kifo.common.enums.exception.ExceptionType.NO_PERMISSION;
import static de.kifo.common.enums.exception.ExceptionType.PLAYLIST_INDEX_NOT_FOUND;
import static de.kifo.common.enums.exception.ExceptionType.PLAYLIST_NOT_FOUND;
import static de.kifo.common.enums.exception.ExceptionType.PLAYLIST_SONG_NOT_FOUND;
import static de.kifo.common.enums.message.Message.MessageType.MESSAGE;
import static java.util.Comparator.comparing;
import static java.util.List.of;
import static java.util.Objects.isNull;
import static net.dv8tion.jda.api.interactions.commands.OptionType.INTEGER;
import static net.dv8tion.jda.api.interactions.commands.OptionType.STRING;

@CommandBase.BotCommand(name = "modifyplaylist", description = "Modifiziere eine Playlist.", hasOptions = true)
public class ModifyPlaylistCommand extends CommandBase {

    @Inject
    private JavaBot javaBot;

    public ModifyPlaylistCommand(@NotNull BotCommand command) {
        super(command);
    }

    @Override
    public void execute(Member member, TextChannel textChannel, List<OptionMapping> options, SlashCommandInteractionEvent event) throws CommandException {
        PlaylistAction action = Arrays.stream(PlaylistAction.values())
                .filter(playlistAction -> playlistAction.getActionName().equalsIgnoreCase(options.get(0).getAsString()))
                .findFirst()
                .get();
        String playlistName = options.get(1).getAsString();
        String songName = options.get(2).getAsString();
        Playlist playlist = javaBot.getApi().getPlaylistByName(playlistName);

        if (isNull(playlist)) {
            throw new CommandException(PLAYLIST_NOT_FOUND, event, javaBot);
        }
        if (!playlist.getPublicAccess() && playlist.getUserId() != event.getUser().getIdLong()) {
            throw new CommandException(NO_PERMISSION, event, javaBot);
        }
        int index = options.size() >= 4 ? (options.get(3).getAsInt() - 1) : playlist.getSongs().size();

        if (index < 0 || index > playlist.getSongs().size()) {
            throw new CommandException(PLAYLIST_INDEX_NOT_FOUND, event, javaBot);
        }

        switch (action) {
            case ADD_SONG -> {
                ((List<String>) playlist.getSongs()).add(index, songName);
                javaBot.getApi().updatePlaylist(playlist.getName(), playlist);
                event.replyEmbeds(javaBot.getEmbedUtils().getEmbedMessageByText("Du hast " + songName + " zu der Playlist " + playlist.getName() +  " an " + (index + 1) + ". Stelle hinzugefügt.", MESSAGE)).queue();
            }
            case REMOVE_SONG -> {
                if (!playlist.getSongs().stream().filter(song -> song.equalsIgnoreCase(songName)).findAny().isPresent()) {
                    throw new CommandException(PLAYLIST_SONG_NOT_FOUND, event, javaBot);
                }
                playlist.getSongs().removeIf(song -> song.equalsIgnoreCase(songName));
                javaBot.getApi().updatePlaylist(playlist.getName(), playlist);
                event.replyEmbeds(javaBot.getEmbedUtils().getEmbedMessageByText("Du hast " + songName + " aus der Playlist " + playlist.getName() +  " entfernt.", MESSAGE)).queue();
            }
        }
    }

    @Override
    public void autoComplete(String optionName, CommandAutoCompleteInteractionEvent event) {
        List<Command.Choice> replyChoices = new ArrayList<>();

        switch (optionName.toLowerCase()) {
            case "aktion" -> replyChoices = Arrays.stream(PlaylistAction.values())
                    .filter(PlaylistAction::isModificationAction)
                    .filter(action -> action.getActionName().toLowerCase().contains(event.getFocusedOption().getValue().toLowerCase()))
                    .map(action -> new Command.Choice(action.getActionName(), action.getActionName()))
                    .toList();
            case "playlist" -> replyChoices = javaBot.getApi().getPlaylistsListByUserId(event.getUser().getIdLong()).stream()
                    .filter(playlist -> playlist.getName().toLowerCase().contains(event.getFocusedOption().getValue().toLowerCase()))
                    .map(playlist -> new Command.Choice(playlist.getName(), playlist.getName()))
                    .limit(25)
                    .toList();
            case "song" -> replyChoices = javaBot.getApi().getSongListByUserId(event.getUser().getIdLong()).stream()
                    .filter(song -> song.getName().length() < 100)
                    .sorted(comparing(Song::getTimesPlayed).reversed())
                    .map(Song::getName)
                    .filter(songName -> songName.toLowerCase().contains(event.getFocusedOption().getValue().toLowerCase()))
                    .map(songName -> new Command.Choice(songName, songName))
                    .limit(25)
                    .toList();
        }

        event.replyChoices(replyChoices).queue();
    }

    @Override
    public List<OptionData> getOptions() {
        return of(new OptionData(STRING, "aktion", "Aktion, die ausgeführt werden soll.", true, true),
                  new OptionData(STRING, "playlist", "Die Playlist, auf die zugegriffen werden soll.", true, true),
                  new OptionData(STRING, "song", "Der Name des Liedes, auf welches die Aktion angewendet werden soll.", true, true),
                  new OptionData(INTEGER, "index", "Der Index, auf den das Lied verschoben werden soll", false, false));
    }
}
