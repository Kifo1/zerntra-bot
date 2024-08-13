package de.kifo.listener;

import de.kifo.JavaBot;
import de.kifo.common.api.model.User;
import lombok.NonNull;
import net.dv8tion.jda.api.events.user.update.UserUpdateOnlineStatusEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.inject.Inject;

import static java.lang.System.currentTimeMillis;

public class UserUpdateOnlineStatusListener extends ListenerAdapter {

    @Inject
    private JavaBot javaBot;

    @Override
    public void onUserUpdateOnlineStatus(@NonNull UserUpdateOnlineStatusEvent event) {
        javaBot.getApi().updateUserOrCreate(new User(event.getUser().getIdLong(), event.getUser().getName(), null, currentTimeMillis()));
    }
}
