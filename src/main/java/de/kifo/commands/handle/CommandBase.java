package de.kifo.commands.handle;

import com.google.inject.Inject;
import de.kifo.JavaBot;
import de.kifo.common.exceptions.CommandException;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;
import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.List;

import static de.kifo.common.services.MessageService.MessageType.ERROR;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static java.util.Objects.nonNull;

public abstract class CommandBase extends CommandDataImpl {

    @Inject
    private JavaBot javaBot;

    public CommandBase(@NotNull BotCommand command) {
        super(command.name(), command.description());
    }

    public void executeInitialization(Member member, TextChannel textChannel, List<OptionMapping> options, SlashCommandInteractionEvent event) {
        try {
            this.execute(member, textChannel, options, event);
        } catch (Exception e) {
            if (!(e instanceof CommandException)) {
                String reason = nonNull(e.getMessage()) ? e.getMessage() : "Unbekannter Fehler";

                event.replyEmbeds(new EmbedBuilder()
                        .setColor(ERROR.getEmbedColor())
                        .setTitle("Beim Ausführen des Commands kam es zu einem unerwarteten Fehler.")
                        .addField("Fehler:", reason, false)
                        .build()).queue();
            }
            throw new RuntimeException(e);
        }
    }

    public abstract void execute(Member member, TextChannel textChannel, List<OptionMapping> options, SlashCommandInteractionEvent event) throws CommandException;

    public abstract void autoComplete(String optionName, CommandAutoCompleteInteractionEvent event);

    @Override
    public @NotNull List<OptionData> getOptions() {
        return List.of();
    }

    @Target(TYPE)
    @Retention(RUNTIME)
    public @interface BotCommand {

        String name();

        String description() default "";

        boolean hasOptions() default false;
    }
}
