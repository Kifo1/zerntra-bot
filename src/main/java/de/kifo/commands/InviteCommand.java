package de.kifo.commands;

import de.kifo.JavaBot;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;

import javax.inject.Inject;

public class InviteCommand extends CommandDataImpl {

    @Inject
    private JavaBot javaBot;

    public InviteCommand() {
        super("invite", "Create an invite link");
    }
}
