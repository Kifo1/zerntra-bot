package de.kifo.common.button.song;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import de.kifo.common.button.handle.ButtonBase;
import de.kifo.common.music.GuildMusicManager;
import de.kifo.common.music.PlayerManager;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

import static de.kifo.JavaBot.buttonService;
import static de.kifo.common.enums.button.ButtonType.TOGGLE_PLAYING_TO_OFF;
import static java.util.Objects.isNull;

@ButtonBase.BotButton(buttonType = TOGGLE_PLAYING_TO_OFF)
public class ToggleSongPlayToOffButton extends ButtonBase {

    public ToggleSongPlayToOffButton(BotButton botButton) {
        super(botButton);
    }

    @Override
    public void onClick(ButtonInteractionEvent event) {
        Guild guild = event.getGuild();

        if (isNull(guild)) return;

        PlayerManager playerManager = javaBot.getPlayerManager();
        GuildMusicManager guildMusicManager = playerManager.getGuildMusicManager(guild);
        AudioPlayer audioPlayer = guildMusicManager.getTrackScheduler().getAudioPlayer();

        audioPlayer.setPaused(true);
        event.getMessage().editMessageComponents(buttonService.getSongActionRow(false)).queue();
        event.deferEdit().queue();
    }
}