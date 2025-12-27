package de.kifo.commands.music;

import com.google.inject.Inject;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import de.kifo.JavaBot;
import de.kifo.commands.handle.CommandBase;
import de.kifo.common.exceptions.CommandException;
import de.kifo.common.music.GuildMusicManager;
import de.kifo.common.music.PlayerManager;
import de.kifo.common.music.TrackScheduler;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static de.kifo.JavaBot.buttonService;
import static de.kifo.common.enums.button.ButtonType.*;
import static de.kifo.common.enums.exception.ExceptionType.*;
import static java.awt.Color.MAGENTA;
import static java.lang.Math.ceil;
import static java.lang.Math.min;
import static java.lang.String.valueOf;
import static java.util.Objects.isNull;
import static java.util.stream.IntStream.rangeClosed;
import static net.dv8tion.jda.api.interactions.commands.OptionType.INTEGER;

@CommandBase.BotCommand(name = "songlist", description = "Displays the current song list.", hasOptions = true)
public class SongListCommand extends CommandBase {

    @Inject
    private static JavaBot javaBot;

    public SongListCommand(@NotNull BotCommand command) {
        super(command);
    }

    @Override
    public void execute(Member member, TextChannel textChannel, List<OptionMapping> options, SlashCommandInteractionEvent event) throws CommandException {
        GuildVoiceState guildVoiceState = member.getVoiceState();
        Guild guild = event.getGuild();

        if (isNull(guildVoiceState) || isNull(guildVoiceState.getChannel())) {
            throw new CommandException(NOT_IN_SPEECH_CHANNEL, event);
        }

        PlayerManager playerManager = javaBot.getPlayerManager();
        GuildMusicManager guildMusicManager = playerManager.getGuildMusicManager(guild);
        TrackScheduler trackScheduler = guildMusicManager.getTrackScheduler();
        AudioPlayer audioPlayer = trackScheduler.getAudioPlayer();

        if (isNull(audioPlayer.getPlayingTrack())) {
            throw new CommandException(NO_SONG_RUNNING, event);
        } else if (trackScheduler.isQueueEmpty()) {
            throw new CommandException(NO_SONG_IN_QUEUE, event);
        }

        int page = options.isEmpty() || isNull(options.get(0)) ? 1 : options.get(0).getAsInt();
        int pages = getCurrentPageAmount(guild);
        if (page > pages) {
            throw new CommandException(PAGE_NOT_AVAILABLE, event);
        }

        event.replyEmbeds(getSonglistPageEmbed(guild, page)).queue();
    }

    @Override
    public void autoComplete(String optionName, CommandAutoCompleteInteractionEvent event) {
        List<Command.Choice> options = new ArrayList<>();

        if (optionName.equalsIgnoreCase("page")) {
            int pages = getCurrentPageAmount(event.getGuild());

            if (pages != 0) {
                rangeClosed(1, min(pages, 25))
                        .forEach(number -> options.add(new Command.Choice(valueOf(number), number)));
            }

            event.replyChoices(options).queue();
        }
    }

    @Override
    public @NotNull List<OptionData> getOptions() {
        return List.of(new OptionData(INTEGER, "page", "The page of the songlist to be displayed", false, true));
    }

    public static MessageEmbed getSonglistPageEmbed(Guild guild, int page) {
        PlayerManager playerManager = javaBot.getPlayerManager();
        GuildMusicManager guildMusicManager = playerManager.getGuildMusicManager(guild);
        TrackScheduler trackScheduler = guildMusicManager.getTrackScheduler();

        List<AudioTrack> queuedSongList = new ArrayList<>(trackScheduler.getQueue());
        EmbedBuilder builder = new EmbedBuilder();
        builder.setColor(MAGENTA);

        builder.setTitle("Page " + page + ":");
        rangeClosed(((page - 1) * 10), (9 + (page - 1) * 10)).forEach(index -> {
            if (index <= queuedSongList.size() - 1) {
                int trackNumber = index + 1;
                AudioTrack song = queuedSongList.get(index);
                builder.appendDescription("Song " + trackNumber + ": " + song.getInfo().title + "\n");
            }
        });
        builder.setFooter("Use \"/skip <number>\", to remove the song from the songlist.");
        return builder.build();
    }

    public static ActionRow getSonglistPageActionRow(Guild guild, int page) {
        int pages = getCurrentPageAmount(guild);
        ActionRow actionRow;
        if (page == 1) {
            actionRow = ActionRow.of(
                    buttonService.getButtonByType(DELETE).getButton(),
                    buttonService.getButtonByType(NEXT_PAGE).getButton());
        } else if (page == pages) {
            actionRow = ActionRow.of(
                    buttonService.getButtonByType(PREVIOUS_PAGE).getButton(),
                    buttonService.getButtonByType(DELETE).getButton(),
                    buttonService.getButtonByType(NEXT_PAGE).getButton());
        } else {
            actionRow = ActionRow.of(
                    buttonService.getButtonByType(PREVIOUS_PAGE).getButton(),
                    buttonService.getButtonByType(DELETE).getButton());
        }
        return actionRow;
    }

    private static int getCurrentPageAmount(Guild guild) {
        return (int) ceil((double) javaBot.getPlayerManager().getGuildMusicManager(guild).getTrackScheduler().getQueue().size() / 10);
    }

    public static int extractPage(String text) {
        String pageStr = text.replace("Page ", "").replace(":", "").trim();
        return Integer.parseInt(pageStr);
    }
}
