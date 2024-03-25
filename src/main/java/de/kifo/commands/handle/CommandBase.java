package de.kifo.commands.handle;

import de.kifo.JavaBot;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.List;

import static com.google.common.collect.ImmutableList.of;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

public abstract class CommandBase extends CommandDataImpl {

    @Inject
    private JavaBot javaBot;

    public CommandBase(@NotNull Command command) {
        super(command.name(), command.description());
    }

    public abstract void execute(Member member, TextChannel textChannel, List<OptionMapping> options, SlashCommandInteractionEvent event);

    public List<String> getTabCompletion() {
        return of("");
    }

    @Target(TYPE)
    @Retention(RUNTIME)
    public @interface Command {

        String name();

        String description() default "";

        String usage() default "";
    }
}
