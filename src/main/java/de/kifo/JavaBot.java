package de.kifo;

import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import de.kifo.music.PlayerManager;
import de.kifo.registration.Registry;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import net.dv8tion.jda.api.JDA;

import static com.google.inject.Guice.createInjector;
import static com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers.registerRemoteSources;
import static net.dv8tion.jda.api.JDABuilder.createDefault;
import static net.dv8tion.jda.api.entities.Activity.playing;
import static net.dv8tion.jda.api.requests.GatewayIntent.DIRECT_MESSAGES;
import static net.dv8tion.jda.api.requests.GatewayIntent.GUILD_PRESENCES;
import static net.dv8tion.jda.api.requests.GatewayIntent.MESSAGE_CONTENT;

@Getter
public class JavaBot {

    private Injector injector;

    private JDA jda;
    private AudioPlayerManager audioPlayerManager;
    private PlayerManager playerManager ;
    private Registry registry;

    public JavaBot() {
        injector = createInjector(new RegistrationModule(this));

        setUpBot();
        handleRegistrations();
    }

    private void setUpBot() {
        jda = createDefault("MTA1OTQzMTY0NDA4MTE3MjQ4MQ.GFY5Eo.TcwNJzrB9aSYt_1fYTd6OgHb6BpWgrBFfsAqP4")
                .enableIntents(MESSAGE_CONTENT)
                .enableIntents(GUILD_PRESENCES)
                .enableIntents(DIRECT_MESSAGES)
                .setActivity(playing("Musik für die Rasselbande"))
                .build();
    }

    private void handleRegistrations() {
        this.audioPlayerManager = new DefaultAudioPlayerManager();
        registerRemoteSources(audioPlayerManager);
        audioPlayerManager.getConfiguration().setFilterHotSwapEnabled(true);

        this.playerManager = new PlayerManager(this);

        this.registry = new Registry(this.jda, this.getClass().getClassLoader(), this.injector);
        this.registry.registerAllCommands();
        this.registry.registerAllListeners();
    }

    @Data
    @AllArgsConstructor
    private class RegistrationModule extends AbstractModule {

        private JavaBot javaBot;

        @Override
        protected void configure() {
            bind(JavaBot.class).toInstance(this.javaBot);
        }
    }
}
