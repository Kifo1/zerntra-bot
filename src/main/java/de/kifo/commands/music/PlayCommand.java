package de.kifo.commands.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import de.kifo.JavaBot;
import de.kifo.commands.handle.CommandBase;
import de.kifo.music.AudioLoadResult;
import de.kifo.music.MusicController;
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

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;

import static com.google.common.collect.ImmutableList.of;
import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toList;
import static net.dv8tion.jda.api.interactions.commands.OptionType.STRING;

@CommandBase.Command(name = "play", description = "Wähle ein Lied, das abgespielt werden soll.", hasOptions = true)
public class PlayCommand extends CommandBase {

    @Inject
    private JavaBot javaBot;

    public static HashMap<Long, TextChannel> map = new HashMap<>();

    public PlayCommand(@NotNull Command command) {
        super(command);
    }

    @Override
    public void execute(Member member, TextChannel textChannel, List<OptionMapping> options, SlashCommandInteractionEvent event) {
        GuildVoiceState guildVoiceState = member.getVoiceState();

        if (isNull(guildVoiceState) || isNull(guildVoiceState.getChannel()) || isNull(guildVoiceState.getChannel().asVoiceChannel())) {
            event.reply("Du musst in einem Sprachkanal sein.").queue(); //TODO Replace with embed (EmbedBuilder builder = new EmbedBuilder())
            return;
        }

        VoiceChannel voiceChannel = guildVoiceState.getChannel().asVoiceChannel();
        MusicController controller = javaBot.getPlayerManager().getController(voiceChannel.getGuild().getIdLong());
        AudioPlayerManager audioPlayerManager = javaBot.getAudioPlayerManager();
        AudioManager manager = voiceChannel.getGuild().getAudioManager();
        manager.openAudioConnection(voiceChannel);

        String title = options.get(0).getAsString();
        if (!title.startsWith("http")) {
            title = "ytsearch:" + title + " audio";
        }
        event.reply("Suche nach dem Titel...").queue(); //TODO Replace with embed (EmbedBuilder builder = new EmbedBuilder())

        audioPlayerManager.loadItem(title, new AudioLoadResult(controller, title, controller.getGuild()));
        map.put(voiceChannel.getGuild().getIdLong(), textChannel);
    }

    @Override
    public void autoComplete(String optionName, CommandAutoCompleteInteractionEvent event) {
        List<String> options = of("Achterbahn wise guys", "Ich trink uso was trinkst denn du so", "for the night pop smoke"); //TODO add logic to get famous songs

        if (optionName.equalsIgnoreCase("song")) {
            List<net.dv8tion.jda.api.interactions.commands.Command.Choice> returnChoices = options.stream()
                    .filter(option -> option.toLowerCase().startsWith(event.getFocusedOption().getValue().toLowerCase()))
                    .map(option -> new net.dv8tion.jda.api.interactions.commands.Command.Choice(option, option))
                    .collect(toList());
            event.replyChoices(returnChoices).queue();
        }
    }

    @Override
    public List<OptionData> getOptions() {
        return of(new OptionData(STRING, "song", "Titel oder URL vom Lied", true, true));
    }
}
