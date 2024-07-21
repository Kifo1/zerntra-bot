package de.kifo.commands.music;

import de.kifo.JavaBot;
import de.kifo.commands.handle.CommandBase;
import de.kifo.common.api.model.Song;
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

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;

import static com.google.common.collect.ImmutableList.of;
import static de.kifo.common.enums.exception.CommandExceptionType.NOT_IN_SPEECH_CHANNEL;
import static de.kifo.common.util.EmbedUtils.MessageType.MESSAGE;
import static java.util.Comparator.comparing;
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

        if (isNull(guildVoiceState) || isNull(guildVoiceState.getChannel()) || isNull(guildVoiceState.getChannel().asVoiceChannel())) {
            throw new CommandException(NOT_IN_SPEECH_CHANNEL, event, javaBot);
        }

        VoiceChannel voiceChannel = guildVoiceState.getChannel().asVoiceChannel();
        PlayerManager playerManager = javaBot.getPlayerManager();
        AudioManager manager = voiceChannel.getGuild().getAudioManager();
        manager.openAudioConnection(voiceChannel);

        String url = options.get(0).getAsString();
        if (!url.startsWith("http")) {
            url = "scsearch:" + url + " audio";
        }
        event.replyEmbeds(javaBot.getEmbedUtils().getEmbedMessageByText("Suche nach dem Titel...", MESSAGE)).queue();

        playerManager.play(event.getGuild(), url, member.getUser().getIdLong());
        map.put(voiceChannel.getGuild().getIdLong(), textChannel);
    }

    @Override
    public void autoComplete(String optionName, CommandAutoCompleteInteractionEvent event) {
        List<String> options = javaBot.getApi().getSongListByUserId(event.getUser().getIdLong()).stream()
                .filter(song -> song.getName().length() < 100)
                .sorted(comparing(Song::getTimesPlayed).reversed())
                .map(Song::getName)
                .toList();

        if (optionName.equalsIgnoreCase("song")) {
            List<Command.Choice> replyChoices = options.stream()
                    .filter(option -> option.toLowerCase().contains(event.getFocusedOption().getValue().toLowerCase()))
                    .map(option -> new Command.Choice(option, option))
                    .limit(25)
                    .toList();

            event.replyChoices(replyChoices).queue();
        }
    }

    @Override
    public List<OptionData> getOptions() {
        return of(new OptionData(STRING, "song", "Titel oder URL vom Lied", true, true));
    }
}
