package de.kifo.common.button.song;

import de.kifo.common.button.handle.ButtonBase;
import de.kifo.common.music.GuildMusicManager;
import de.kifo.common.music.PlayerManager;
import de.kifo.common.music.TrackScheduler;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

import static de.kifo.common.button.handle.ButtonBase.ButtonType.SHUFFLE_LIST;
import static java.util.Objects.isNull;

@ButtonBase.BotButton(buttonType = SHUFFLE_LIST)
public class ShuffleButton extends ButtonBase {

    public ShuffleButton(BotButton botButton) {
        super(botButton);
    }

    @Override
    public void onClick(ButtonInteractionEvent event) {
        Guild guild = event.getGuild();

        if (isNull(guild)) return;

        PlayerManager playerManager = javaBot.getPlayerManager();
        GuildMusicManager guildMusicManager = playerManager.getGuildMusicManager(guild);
        TrackScheduler trackScheduler = guildMusicManager.getTrackScheduler();

        trackScheduler.shuffleQueue();
        event.deferEdit().queue();
    }
}
