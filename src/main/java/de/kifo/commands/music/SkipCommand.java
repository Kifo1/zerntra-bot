package de.kifo.commands.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import de.kifo.JavaBot;
import de.kifo.commands.handle.CommandBase;
import de.kifo.common.exceptions.CommandException;
import de.kifo.common.music.GuildMusicManager;
import de.kifo.common.music.PlayerManager;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import static com.google.common.collect.ImmutableList.of;
import static de.kifo.common.enums.exception.CommandExceptionType.NOT_IN_SPEECH_CHANNEL;
import static de.kifo.common.enums.exception.CommandExceptionType.SKIP_INDEX_NOT_FOUND;
import static de.kifo.common.util.EmbedUtils.MessageType.MESSAGE;
import static java.lang.Math.min;
import static java.lang.String.valueOf;
import static java.util.Objects.isNull;
import static java.util.stream.IntStream.rangeClosed;
import static net.dv8tion.jda.api.interactions.commands.OptionType.INTEGER;

@CommandBase.BotCommand(name = "skip", description = "Überspringe ein Lied aus der aktuellen Playlist.", hasOptions = true)
public class SkipCommand extends CommandBase {

    @Inject
    private JavaBot javaBot;

    public SkipCommand(@NotNull BotCommand command) {
        super(command);
    }

    @Override
    public void execute(Member member, TextChannel textChannel, List<OptionMapping> options, SlashCommandInteractionEvent event) throws CommandException {
        GuildVoiceState guildVoiceState = member.getVoiceState();

        if (isNull(guildVoiceState) || isNull(guildVoiceState.getChannel()) || isNull(guildVoiceState.getChannel().asVoiceChannel())) {
            throw new CommandException(NOT_IN_SPEECH_CHANNEL, event, javaBot);
        }

        PlayerManager playerManager = javaBot.getPlayerManager();
        GuildMusicManager guildMusicManager = playerManager.getGuildMusicManager(event.getGuild());
        AudioPlayer audioPlayer = guildMusicManager.getTrackScheduler().getAudioPlayer();

        if (options.isEmpty() || isNull(options.get(0))) {
            audioPlayer.stopTrack();
            event.replyEmbeds(javaBot.getEmbedUtils().getEmbedMessageByText("Das Lied wird übersprungen...", MESSAGE)).queue();
        } else {
            BlockingQueue<AudioTrack> tracks = guildMusicManager.getTrackScheduler().getQueue();
            int number = options.get(0).getAsInt();

            if (tracks.size() >= number) {
                AudioTrack track = (AudioTrack) tracks.toArray()[number - 1];
                guildMusicManager.getTrackScheduler().getQueue().remove(track);
                event.replyEmbeds(javaBot.getEmbedUtils().getEmbedMessageByText(
                        track.getInfo().title + " von " + track.getInfo().author +
                        " wurde aus der Playlist entfernt.", MESSAGE)).queue();
            } else {
                throw new CommandException(SKIP_INDEX_NOT_FOUND, event, javaBot);
            }
        }
    }

    @Override
    public void autoComplete(String optionName, CommandAutoCompleteInteractionEvent event) {
        List<Command.Choice> options = new ArrayList<>();
        GuildMusicManager guildMusicManager = javaBot.getPlayerManager().getGuildMusicManager(event.getGuild());

        if (optionName.equalsIgnoreCase("index")) {
            rangeClosed(1, min(guildMusicManager.getTrackScheduler().getQueue().size(), 25))
                    .forEach(number -> options.add(new net.dv8tion.jda.api.interactions.commands.Command.Choice(valueOf(number), number)));
            event.replyChoices(options).queue();
        }
    }

    @Override
    public List<OptionData> getOptions() {
        return of(new OptionData(INTEGER, "index", "Index des Liedes, welches übersprungen werden soll", false, true));
    }
}
