package de.kifo;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import de.kifo.commands.types.CommandManager;
import de.kifo.database.DataConnection;
import de.kifo.listener.MessageListener;
import de.kifo.music.PlayerManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;

import static com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers.registerRemoteSources;
import static net.dv8tion.jda.api.JDABuilder.createDefault;

public class Main {

    private static Main instance;

    private static AudioPlayerManager audioPlayerManager;
    private static JDA jda;
    private PlayerManager playerManager;
    private DataConnection dataConnection;
    private CommandManager commandManager;

    public Main() {
        instance = this;

        this.commandManager = new CommandManager();
        this.playerManager = new PlayerManager();
        this.dataConnection = new DataConnection();
    }

    public static void main(String[] args) {
        audioPlayerManager = new DefaultAudioPlayerManager();

        jda = createDefault("MTA1OTQzMTY0NDA4MTE3MjQ4MQ.GFY5Eo.TcwNJzrB9aSYt_1fYTd6OgHb6BpWgrBFfsAqP4")
                .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                .enableIntents(GatewayIntent.GUILD_PRESENCES)
                .enableIntents(GatewayIntent.DIRECT_MESSAGES)
                .setActivity(Activity.playing("Musik für die Rasselbande"))
                .build();

        registerRemoteSources(audioPlayerManager);
        audioPlayerManager.getConfiguration().setFilterHotSwapEnabled(true);

        //Events
        jda.addEventListener(new MessageListener());

        new Main();
    }

    public static Main getInstance() {
        return instance;
    }

    public static AudioPlayerManager getAudioPlayerManager() {
        return audioPlayerManager;
    }

    public static JDA getJDA() {
        return jda;
    }

    public PlayerManager getPlayerManager() {
        return playerManager;
    }

    public DataConnection getDataConnection() {
        return dataConnection;
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }

}