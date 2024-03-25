package de.kifo.commands;

import net.dv8tion.jda.internal.interactions.CommandDataImpl;
import org.jetbrains.annotations.NotNull;

public class TestCommand extends CommandDataImpl {

    public TestCommand(@NotNull String name, @NotNull String description) {
        super("Test", "Command zum testen...");
    }
}
