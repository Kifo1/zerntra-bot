package de.kifo.common.button.song;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import de.kifo.common.button.handle.ButtonBase;
import de.kifo.common.music.GuildMusicManager;
import de.kifo.common.music.PlayerManager;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

import static de.kifo.common.button.handle.ButtonBase.ButtonType.TOGGLE_SONG;
import static java.util.Objects.isNull;

@ButtonBase.BotButton(buttonType = TOGGLE_SONG)
public class ToggleSongPlayButton extends ButtonBase {

    public ToggleSongPlayButton(BotButton botButton) {
        super(botButton);
    }

    @Override
    public void onClick(ButtonInteractionEvent event) {
        Guild guild = event.getGuild();

        if (isNull(guild)) return;

        PlayerManager playerManager = javaBot.getPlayerManager();
        GuildMusicManager guildMusicManager = playerManager.getGuildMusicManager(guild);
        AudioPlayer audioPlayer = guildMusicManager.getTrackScheduler().getAudioPlayer();

        audioPlayer.setPaused(!audioPlayer.isPaused());
        event.deferEdit().queue();
    }
}
