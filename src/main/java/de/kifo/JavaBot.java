package de.kifo;

import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import de.kifo.common.api.API;
import de.kifo.common.music.PlayerManager;
import de.kifo.common.registration.Registry;
import de.kifo.common.services.ButtonService;
import de.kifo.common.services.MessageService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.dv8tion.jda.api.JDA;

import static com.google.inject.Guice.createInjector;
import static java.lang.System.getenv;
import static net.dv8tion.jda.api.JDABuilder.createDefault;
import static net.dv8tion.jda.api.entities.Activity.watching;
import static net.dv8tion.jda.api.requests.GatewayIntent.DIRECT_MESSAGES;
import static net.dv8tion.jda.api.requests.GatewayIntent.GUILD_MEMBERS;
import static net.dv8tion.jda.api.requests.GatewayIntent.GUILD_PRESENCES;
import static net.dv8tion.jda.api.requests.GatewayIntent.MESSAGE_CONTENT;
import static net.dv8tion.jda.api.utils.MemberCachePolicy.ALL;
import static net.dv8tion.jda.api.utils.cache.CacheFlag.ONLINE_STATUS;

@Getter
public class JavaBot {

    public static final String VERSION = "1.0.0-dev";

    private final Injector injector;

    private API api;
    private JDA jda;
    private PlayerManager playerManager;
    private Registry registry;

    public static final String BOT_API_KEY = getenv("BOT_API_TOKEN");
    public static String BOT_DC_KEY;

    public static ButtonService buttonService;
    public static MessageService messageService;

    public JavaBot(boolean production) {
        BOT_DC_KEY = production ? getenv("BOT_API_TOKEN") : getenv("BOT_TEST_API_KEY");

        injector = createInjector(new RegistrationModule(this));

        setUpBot();
        handleRegistrations();

        buttonService = new ButtonService(this);
        messageService = new MessageService(this);
    }

    private void setUpBot() {
        api = injector.getInstance(API.class);
        jda = createDefault(BOT_DC_KEY)
                .setMemberCachePolicy(ALL)
                .enableCache(ONLINE_STATUS)
                .enableIntents(MESSAGE_CONTENT)
                .enableIntents(GUILD_PRESENCES)
                .enableIntents(DIRECT_MESSAGES)
                .enableIntents(GUILD_MEMBERS)
                .setActivity(watching("zerntra.org"))
                .build();
    }

    public void handleRegistrations() {
        this.playerManager = injector.getInstance(PlayerManager.class);

        this.registry = new Registry(this.jda, this.getClass().getClassLoader(), this.injector);
        this.registry.registerAllCommands();
        this.registry.registerAllListeners();
        this.registry.registerAllButtons();
    }

    @AllArgsConstructor
    private static class RegistrationModule extends AbstractModule {

        private JavaBot javaBot;

        @Override
        protected void configure() {
            bind(JavaBot.class).toInstance(this.javaBot);
        }
    }
}
