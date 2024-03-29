package de.kifo.music;

import de.kifo.JavaBot;

import java.util.concurrent.ConcurrentHashMap;

public class PlayerManager {

    private JavaBot javaBot;

    public ConcurrentHashMap<Long, MusicController> controller;

    public PlayerManager(JavaBot javaBot) {
        this.javaBot = javaBot;
        this.controller = new ConcurrentHashMap<>();
    }

    public MusicController getController(long guildId) {
        MusicController mc;

        if(this.controller.containsKey(guildId)) {
            mc = this.controller.get(guildId);
        } else {
            mc = new MusicController(javaBot.getJda().getGuildById(guildId), javaBot);
            this.controller.put(guildId, mc);
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