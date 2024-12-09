package de.kifo.listener;

import de.kifo.JavaBot;
import de.kifo.common.api.model.HistoryEntry;
import de.kifo.common.api.model.User;
import lombok.NonNull;
import net.dv8tion.jda.api.events.user.update.UserUpdateOnlineStatusEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.inject.Inject;

import static de.kifo.JavaBot.ZONE_ID;
import static de.kifo.common.api.model.HistoryEntry.Type.ONLINE_STATUS_CHANGE;
import static java.lang.System.currentTimeMillis;
import static java.time.ZonedDateTime.now;

public class UserUpdateOnlineStatusListener extends ListenerAdapter {

    @Inject
    private JavaBot javaBot;

    @Override
    public void onUserUpdateOnlineStatus(@NonNull UserUpdateOnlineStatusEvent event) {
        Long userId = event.getUser().getIdLong();

        javaBot.getApi().updateUserOrCreate(new User(userId, event.getUser().getName(), null, currentTimeMillis()));
        javaBot.getApi().createHistoryEntry(new HistoryEntry(0L, userId, ONLINE_STATUS_CHANGE, now(ZONE_ID),
                event.getOldOnlineStatus().name() + " -> " + event.getNewOnlineStatus().name()));
    }
}
