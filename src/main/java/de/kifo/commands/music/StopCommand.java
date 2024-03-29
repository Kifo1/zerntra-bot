package de.kifo.commands.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
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
import net.dv8tion.jda.api.managers.AudioManager;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import java.util.List;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@CommandBase.Command(name = "stop", description = "Beende das Abspielen von Musik.")
public class StopCommand extends CommandBase {

    @Inject
    private JavaBot javaBot;

    public StopCommand(@NotNull Command command) {
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
        AudioManager audioManager = voiceChannel.getGuild().getAudioManager();
        AudioPlayer audioPlayer = controller.getPlayer();

        if (audioManager.isConnected()) {
            if (nonNull(audioPlayer.getPlayingTrack())) {
                AudioLoadResult.map.get(textChannel.getGuild()).clear();
                audioPlayer.stopTrack();
            }
            audioManager.closeAudioConnection();
            event.reply("Die Musik wurde beendet.").queue(); //TODO Replace with embed (EmbedBuilder builder = new EmbedBuilder())
        } else {
            event.reply("Es läuft im Moment kein Lied.").queue(); //TODO Replace with embed (EmbedBuilder builder = new EmbedBuilder())
        }
    }

    @Override
    public void autoComplete(String optionName, CommandAutoCompleteInteractionEvent event) {}
}
