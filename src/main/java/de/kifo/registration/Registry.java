package de.kifo.registration;

import com.google.common.reflect.ClassPath;
import com.google.inject.Injector;
import lombok.AllArgsConstructor;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import static com.google.common.collect.ImmutableSet.of;
import static com.google.common.reflect.ClassPath.from;
import static java.lang.String.format;
import static java.util.stream.Collectors.toSet;

@AllArgsConstructor
public class Registry {

    private final JDA jda;
    private final ClassLoader classLoader;
    private final Injector injector;

    public void registerAllCommands() {
        AtomicInteger successCases = new AtomicInteger();
        List<Class<?>> commandClasses = getAllClassesFromPackage("de.kifo.commands").stream()
                .filter(CommandDataImpl.class::isAssignableFrom)
                .toList();

        commandClasses.forEach(commandClass -> {
            try {
                CommandDataImpl commandDataImpl = (CommandDataImpl) this.injector.getInstance(commandClass);
                jda.upsertCommand(commandDataImpl.getName(), commandDataImpl.getDescription()).setGuildOnly(true).queue();
                successCases.getAndIncrement();
            } catch (Exception e) {
                System.out.println("Failed to register command: " + commandClass.getSimpleName());
            }
        });
        System.out.println(format("Registered Commands: %d/%d", successCases.get(), commandClasses.size()));
    }

    public void registerAllListeners() {
        AtomicInteger successCases = new AtomicInteger();
        List<Class<?>> listenerClasses = getAllClassesFromPackage("de.kifo.listener").stream()
                .filter(ListenerAdapter.class::isAssignableFrom)
                .toList();

        listenerClasses.stream()
                .filter(ListenerAdapter.class::isAssignableFrom)
                .forEach(listenerClass -> {
                    jda.addEventListener(this.injector.getInstance(listenerClass));
                    successCases.getAndIncrement();
                });
        System.out.println(format("Registered Listeners: %d/%d", successCases.get(), listenerClasses.size()));
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
            System.out.println("Couldn't get all classes in registry");
        }
        return of();
    }
}
