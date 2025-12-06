package de.kifo.common.registration;

import com.google.common.reflect.ClassPath;
import com.google.inject.Injector;
import de.kifo.commands.handle.CommandBase;
import de.kifo.common.button.handle.ButtonBase;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static com.google.common.collect.ImmutableSet.of;
import static com.google.common.reflect.ClassPath.from;
import static java.util.stream.Collectors.toSet;

@AllArgsConstructor
public class Registry {

    private final JDA jda;
    private final ClassLoader classLoader;
    private final Injector injector;

    @Getter
    private final Set<CommandBase> commandBases = new HashSet<>();
    @Getter
    private final Set<CommandBase.BotCommand> commands = new HashSet<>();
    @Getter
    private final Map<ButtonBase.ButtonType, ButtonBase> buttonBases = new HashMap<>();
    @Getter
    private final Set<ButtonBase.BotButton> buttons = new HashSet<>();

    public void registerAllCommands() {
        AtomicInteger successCases = new AtomicInteger();
        List<Class<?>> commandClasses = getAllClassesFromPackage("de.kifo.commands").stream()
                .filter(CommandBase.class::isAssignableFrom)
                .filter(commandClass -> commandClass.isAnnotationPresent(CommandBase.BotCommand.class))
                .toList();

        commandClasses.forEach(commandClass -> {
            try {
                CommandBase.BotCommand command = commandClass.getAnnotation(CommandBase.BotCommand.class);
                this.commands.add(command);
                CommandBase commandBase = ((Class<CommandBase>) commandClass).getConstructor(CommandBase.BotCommand.class).newInstance(command);
                this.injector.injectMembers(commandBase);
                commandBases.add(commandBase);

                if (command.hasOptions()) {
                    jda.upsertCommand(commandBase.getName(), commandBase.getDescription()).addOptions(commandBase.getOptions()).queue();
                } else {
                    jda.upsertCommand(commandBase.getName(), commandBase.getDescription()).queue();
                }
                successCases.getAndIncrement();
            } catch (Exception e) {
                System.out.println("Failed to register command: " + commandClass.getSimpleName());
                e.printStackTrace();
            }
        });
        System.out.printf("Registered Commands: %d/%d%n", successCases.get(), commandClasses.size());
    }

    public void registerAllListeners() {
        AtomicInteger successCases = new AtomicInteger();
        List<Class<?>> listenerClasses = getAllClassesFromPackage("de.kifo.listener").stream()
                .filter(ListenerAdapter.class::isAssignableFrom)
                .toList();

        listenerClasses.forEach(listenerClass -> {
                    jda.addEventListener(this.injector.getInstance(listenerClass));
                    successCases.getAndIncrement();
                });
        System.out.printf("Registered Listeners: %d/%d%n", successCases.get(), listenerClasses.size());
    }

    public void registerAllButtons() {
        AtomicInteger successCases = new AtomicInteger();
        List<Class<?>> buttonClasses = getAllClassesFromPackage("de.kifo.common.button").stream()
                .filter(ButtonBase.class::isAssignableFrom)
                .filter(buttonClass -> buttonClass.isAnnotationPresent(ButtonBase.BotButton.class))
                .toList();

        buttonClasses.forEach(buttonClass -> {
            try {
                ButtonBase.BotButton button = buttonClass.getAnnotation(ButtonBase.BotButton.class);
                this.buttons.add(button);
                ButtonBase buttonBase = ((Class<ButtonBase>) buttonClass).getConstructor(ButtonBase.BotButton.class).newInstance(button);
                this.injector.injectMembers(buttonBase);
                buttonBases.put(button.buttonType(), buttonBase);

                successCases.getAndIncrement();
            } catch (Exception e) {
                System.out.println("Failed to register button: " + buttonClass.getSimpleName());
                e.printStackTrace();
            }
        });
        System.out.printf("Registered Buttons: %d/%d%n", successCases.get(), buttonClasses.size());
    }

    private Set<Class<?>> getAllClassesFromPackage(String packageName) {
        try {
            return from(this.classLoader)
                    .getAllClasses()
                    .stream()
                    .filter(clazz -> clazz.getPackageName().startsWith(packageName))
                    .map(ClassPath.ClassInfo::load)
                    .collect(toSet());
        } catch (IOException e) {
            System.out.println(e);
        }
        return of();
    }
}
