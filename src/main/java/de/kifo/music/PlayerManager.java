package de.kifo.music;

import de.kifo.JavaBot;

import javax.inject.Inject;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerManager {

    @Inject
    private JavaBot javaBot;

    public ConcurrentHashMap<Long, MusicController> controller;

    public PlayerManager() {
        this.controller = new ConcurrentHashMap<>();
    }

    public MusicController getController(long guildid) {
        MusicController mc = null;

        if(this.controller.containsKey(guildid)) {
            mc = this.controller.get(guildid);
        } else {
            mc = new MusicController(javaBot.getJda().getGuildById(guildid));
            this.controller.put(guildid, mc);
        }

        return mc;
    }

    public long getGuildByPlayerHash(int hash) {
        for(MusicController controller : this.controller.values()) {
            if(controller.getPlayer().hashCode() == hash) {
                return controller.getGuild().getIdLong();
            }
        }

        return -1;
    }
}