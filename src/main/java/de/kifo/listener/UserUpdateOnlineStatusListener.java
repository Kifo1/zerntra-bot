package de.kifo.listener;

import com.google.inject.Inject;
import de.kifo.JavaBot;
import de.kifo.common.api.model.HistoryEntryDTO;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.user.update.UserUpdateOnlineStatusEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import static de.kifo.common.api.model.HistoryEntryDTO.Type.ONLINE_STATUS_CHANGE;
import static java.lang.System.currentTimeMillis;

public class UserUpdateOnlineStatusListener extends ListenerAdapter {

    @Inject
    private JavaBot javaBot;

    @Override
    public void onUserUpdateOnlineStatus(@NonNull UserUpdateOnlineStatusEvent event) {
        User user = event.getUser();
        Long userId = user.getIdLong();
        Guild guild = event.getGuild();

        javaBot.getApi().createUser(userId, user.getName());
        javaBot.getApi().createHistoryEntry(new HistoryEntryDTO(null, guild.getIdLong(), userId, ONLINE_STATUS_CHANGE,
                currentTimeMillis(), event.getOldOnlineStatus().name() + " -> " + event.getNewOnlineStatus().name()));
    }
}
