package de.kifo;

import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import de.kifo.common.api.API;
import de.kifo.common.music.PlayerManager;
import de.kifo.common.registration.Registry;
import de.kifo.common.util.EmbedUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import net.dv8tion.jda.api.JDA;

import static com.google.inject.Guice.createInjector;
import static net.dv8tion.jda.api.JDABuilder.createDefault;
import static net.dv8tion.jda.api.entities.Activity.playing;
import static net.dv8tion.jda.api.requests.GatewayIntent.DIRECT_MESSAGES;
import static net.dv8tion.jda.api.requests.GatewayIntent.GUILD_MEMBERS;
import static net.dv8tion.jda.api.requests.GatewayIntent.GUILD_PRESENCES;
import static net.dv8tion.jda.api.requests.GatewayIntent.MESSAGE_CONTENT;
import static net.dv8tion.jda.api.utils.MemberCachePolicy.ALL;
import static net.dv8tion.jda.api.utils.cache.CacheFlag.ONLINE_STATUS;

@Getter
public class JavaBot {

    private Injector injector;

    private API api;
    private JDA jda;
    private PlayerManager playerManager;
    private Registry registry;
    private EmbedUtils embedUtils;

    public JavaBot() {
        injector = createInjector(new RegistrationModule(this));

        setUpBot();
        handleRegistrations();
    }

    private void setUpBot() {
        api = injector.getInstance(API.class);
        embedUtils = injector.getInstance(EmbedUtils.class);
        jda = createDefault("MTA1OTQzMTY0NDA4MTE3MjQ4MQ.GFY5Eo.TcwNJzrB9aSYt_1fYTd6OgHb6BpWgrBFfsAqP4")
                .setMemberCachePolicy(ALL)
                .enableCache(ONLINE_STATUS)
                .enableIntents(MESSAGE_CONTENT)
                .enableIntents(GUILD_PRESENCES)
                .enableIntents(DIRECT_MESSAGES)
                .enableIntents(GUILD_MEMBERS)
                .setActivity(playing("Musik für die Rasselbande"))
                .build();
    }

    public void handleRegistrations() {
        this.playerManager = injector.getInstance(PlayerManager.class);

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
