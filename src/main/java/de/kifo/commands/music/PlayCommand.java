package de.kifo.commands.music;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.kifo.JavaBot;
import de.kifo.commands.handle.CommandBase;
import de.kifo.common.api.model.HistoryEntryDTO;
import de.kifo.common.exceptions.CommandException;
import de.kifo.common.music.PlayerManager;
import de.kifo.common.music.TrackScheduler;
import de.kifo.common.services.MessageService;
import net.dv8tion.jda.api.entities.Guild;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static de.kifo.JavaBot.messageService;
import static de.kifo.common.api.model.HistoryEntryDTO.Type.SONG_PLAY;
import static de.kifo.common.enums.exception.ExceptionType.BOT_ALREADY_PLAYING_FOR_GUILD;
import static de.kifo.common.enums.exception.ExceptionType.NOT_IN_SPEECH_CHANNEL;
import static java.lang.System.currentTimeMillis;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static net.dv8tion.jda.api.audio.hooks.ConnectionStatus.CONNECTED;
import static net.dv8tion.jda.api.interactions.commands.OptionType.STRING;

@Singleton
@CommandBase.BotCommand(name = "play", description = "Choose a song to play.", hasOptions = true)
public class PlayCommand extends CommandBase {

    @Inject
    private JavaBot javaBot;

    public static final Map<Long, MessageService.UpdatableMessage> map = new HashMap<>();

    public PlayCommand(@NotNull BotCommand command) {
        super(command);
    }

    @Override
    public void execute(Member member, TextChannel textChannel, List<OptionMapping> options, SlashCommandInteractionEvent event) throws CommandException {
        GuildVoiceState guildVoiceState = member.getVoiceState();
        Guild guild = event.getGuild();

        if (isNull(guildVoiceState) || isNull(guildVoiceState.getChannel())) {
            throw new CommandException(NOT_IN_SPEECH_CHANNEL, event);
        }

        VoiceChannel voiceChannel = guildVoiceState.getChannel().asVoiceChannel();
        PlayerManager playerManager = javaBot.getPlayerManager();
        TrackScheduler trackScheduler = playerManager.getGuildMusicManager(guild).getTrackScheduler();
        AudioManager manager = voiceChannel.getGuild().getAudioManager();

        if (trackScheduler.isPlaying() &&
                !manager.getConnectedChannel().equals(guildVoiceState.getChannel()) &&
                manager.getConnectionStatus() == CONNECTED) {
            throw new CommandException(BOT_ALREADY_PLAYING_FOR_GUILD, event);
        }

        manager.openAudioConnection(voiceChannel);

        String[] url = {options.getFirst().getAsString()};
        if (!url[0].startsWith("http")) {
            url[0] = "ytsearch:" + url[0] + " audio";
        }

        event.deferReply().queue();

        MessageService.UpdatableMessage updatableMessage = new MessageService.UpdatableMessage(
                event.getHook().sendMessageEmbeds(messageService.info("Searching for the song...")).complete()
        );

        map.putIfAbsent(guild.getIdLong(), updatableMessage);

        playerManager.play(guild, url[0], event.getUser().getIdLong())
                .thenCompose(updatedMessage -> {
                    String description = updatedMessage.getDescription();
                    if (nonNull(description) && description.contains("starts now.")) {
                        return updatableMessage.update(updatedMessage);
                    }
                    if (!trackScheduler.isPlaying()) { // Not playing = Load failed => Error message should not be the updatable message
                        map.remove(guild.getIdLong());
                    }
                    return updatableMessage.updateAndDestroyAfter(updatedMessage, 60);
                })
                .exceptionally(e -> {
                    updatableMessage.fail();
                    map.remove(guild.getIdLong());
                    return null;
                });

        javaBot.getApi().createHistoryEntry(new HistoryEntryDTO(null, guild.getIdLong(), member.getIdLong(),
                SONG_PLAY, currentTimeMillis(),"Query " + url[0] + ", " + textChannel.getName()));
    }

    @Override
    public void autoComplete(String optionName, CommandAutoCompleteInteractionEvent event) {
        if (optionName.equalsIgnoreCase("song")) {
            List<Command.Choice> replyChoices = javaBot.getApi().getRecommendedSongsByUserId(event.getUser().getIdLong()).stream()
                    .filter(song -> song.getName().toLowerCase().contains(event.getFocusedOption().getValue().toLowerCase()))
                    .map(song -> new Command.Choice(song.getName(), song.getUri()))
                    .toList();

            event.replyChoices(replyChoices).queue();
        }
    }

    @Override
    public @NotNull List<OptionData> getOptions() {
        return List.of(new OptionData(STRING, "song", "Song title or URL", true, true));
    }
}
