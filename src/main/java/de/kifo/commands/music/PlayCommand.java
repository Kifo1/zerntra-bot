package de.kifo.commands.music;

import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import de.kifo.JavaBot;
import de.kifo.commands.handle.CommandBase;
import de.kifo.common.api.model.HistoryEntry;
import de.kifo.common.api.model.Song;
import de.kifo.common.exceptions.CommandException;
import de.kifo.common.music.PlayerManager;
import de.kifo.common.music.TrackScheduler;
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

import static de.kifo.common.api.model.HistoryEntry.Type.SONG_PLAY;
import static de.kifo.common.enums.exception.ExceptionType.BOT_ALREADY_PLAYING_FOR_GUILD;
import static de.kifo.common.enums.exception.ExceptionType.NOT_IN_SPEECH_CHANNEL;
import static de.kifo.common.enums.message.Message.MessageType.MESSAGE;
import static de.kifo.common.util.EmbedUtils.getEmbedMessageByText;
import static java.lang.System.currentTimeMillis;
import static java.util.Objects.isNull;
import static net.dv8tion.jda.api.interactions.commands.OptionType.STRING;

@CommandBase.BotCommand(name = "play", description = "Wähle ein Lied, das abgespielt werden soll.", hasOptions = true)
public class PlayCommand extends CommandBase {

    @Inject
    private JavaBot javaBot;

    public static HashMap<Long, TextChannel> map = new HashMap<>();

    public PlayCommand(@NotNull BotCommand command) {
        super(command);
    }

    @Override
    public void execute(Member member, TextChannel textChannel, List<OptionMapping> options, SlashCommandInteractionEvent event) throws CommandException {
        GuildVoiceState guildVoiceState = member.getVoiceState();
        Guild guild = event.getGuild();
        TrackScheduler trackScheduler = javaBot.getPlayerManager().getGuildMusicManager(guild).getTrackScheduler();

        if (isNull(guildVoiceState) || isNull(guildVoiceState.getChannel())) {
            throw new CommandException(NOT_IN_SPEECH_CHANNEL, event);
        } else if (trackScheduler.isPlaying()) {
            throw new CommandException(BOT_ALREADY_PLAYING_FOR_GUILD, event);
        }

        VoiceChannel voiceChannel = guildVoiceState.getChannel().asVoiceChannel();
        PlayerManager playerManager = javaBot.getPlayerManager();
        AudioManager manager = voiceChannel.getGuild().getAudioManager();
        manager.openAudioConnection(voiceChannel);

        String url = options.get(0).getAsString();
        if (!url.startsWith("http")) {
            url = "ytsearch:" + url + " audio";
        }
        event.replyEmbeds(getEmbedMessageByText("Suche nach dem Titel...", MESSAGE)).queue();

        playerManager.play(event.getGuild(), url, member.getUser().getIdLong());
        map.put(voiceChannel.getGuild().getIdLong(), textChannel);

        javaBot.getApi().createHistoryEntry(new HistoryEntry(null, guild.getIdLong(), member.getIdLong(),
                SONG_PLAY, currentTimeMillis(),"Query " + url + ", " + textChannel.getName()));
    }

    @Override
    public void autoComplete(String optionName, CommandAutoCompleteInteractionEvent event) {
        if (optionName.equalsIgnoreCase("song")) {
            List<Command.Choice> replyChoices = javaBot.getApi().getRecommendedSongsByUserId(event.getUser().getIdLong()).stream()
                    .map(Song::getName)
                    .filter(songName -> songName.toLowerCase().contains(event.getFocusedOption().getValue().toLowerCase()))
                    .map(songName -> new Command.Choice(songName, songName))
                    .toList();

            event.replyChoices(replyChoices).queue();
        }
    }

    @Override
    public List<OptionData> getOptions() {
        return ImmutableList.of(new OptionData(STRING, "song", "Titel oder URL vom Lied", true, true));
    }
}
