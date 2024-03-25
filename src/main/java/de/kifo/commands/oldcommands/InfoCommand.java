package de.kifo.commands.oldcommands;

import de.kifo.JavaBot;
import de.kifo.commands.oldcommands.types.ServerCommand;
import de.kifo.database.files.CreateUserFile;
import de.kifo.database.utils.UserCommandsDataUtils;
import de.kifo.database.utils.UserDataUtils;
import de.kifo.database.utils.UserErrorsDataUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import javax.inject.Inject;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import static java.awt.Color.MAGENTA;

public class InfoCommand implements ServerCommand {

    @Inject
    private JavaBot javaBot;

    @Override
    public void executeCommand(Member member, TextChannel channel, Message message) {
        UserErrorsDataUtils userErrorsDataUtils = javaBot.getInjector().getInstance(UserErrorsDataUtils.class);
        UserCommandsDataUtils userCommandsDataUtils = javaBot.getInjector().getInstance(UserCommandsDataUtils.class);
        UserDataUtils userDataUtils = javaBot.getInjector().getInstance(UserDataUtils.class);
        userCommandsDataUtils.addInfoCommand(member.getGuild().getIdLong(), member.getUser().getIdLong(), 1);
        CreateUserFile createUserFile = javaBot.getInjector().getInstance(CreateUserFile.class);

        String[] args = message.getContentRaw().split(" ");

        if (args.length == 2) {
            String userName = args[1];

            if (member.getGuild().getMembersByName(userName, true).isEmpty()) {
                EmbedBuilder builder = new EmbedBuilder();
                builder.setColor(MAGENTA);
                builder.setDescription("Der Nutzer konnte nicht gefunden werden. " + member.getAsMention());
                channel.sendMessageEmbeds(builder.build()).queue();
                userErrorsDataUtils.addWrongUsageError(member.getGuild().getIdLong(), member.getUser().getIdLong(), 1);
                return;
            }

            User user = member.getGuild().getMembersByName(userName, true).get(0).getUser();

            if (!userDataUtils.fileExist(member.getGuild().getIdLong(), user.getIdLong())) {
                createUserFile.createUserFile(member.getGuild().getIdLong(), user.getIdLong());
            }

            Calendar calendar = GregorianCalendar.getInstance();
            String date = new SimpleDateFormat("dd.MM.YYYY   kk:mm:ss").format(calendar.getTime()) + " Uhr";

            EmbedBuilder builder = new EmbedBuilder();
            builder.setColor(MAGENTA);

            builder.addField("Name", user.getName(), true);
            builder.addField("Status", member.getGuild().getMembersByName(userName, true).get(0).getOnlineStatus().toString(), true);
            builder.setThumbnail(user.getAvatarUrl());

            builder.addBlankField(true);
            builder.addBlankField(false);

            builder.addField("Gesendete                \nNachrichten", "-> " + userDataUtils.getMessages(member.getGuild().getIdLong(), user.getIdLong()), true);
            builder.addField("Gesendete                \nBefehle", "-> " + (userDataUtils.getCommandQuantity(member.getGuild().getIdLong(), user.getIdLong()) + 1), true);
            builder.addField("Verursachte              \nFehler", "-> " + userDataUtils.getErrorQuantity(member.getGuild().getIdLong(), user.getIdLong()), true);

            builder.addBlankField(false);
            builder.addBlankField(true);

            builder.addField("Befehle                  ", "!invite: " + userCommandsDataUtils.getInviteCommands(member.getGuild().getIdLong(), user.getIdLong()) + "\n"
                    + "!play: " + userCommandsDataUtils.getPlayCommands(member.getGuild().getIdLong(), user.getIdLong()) + "\n"
                    + "!skip: " + userCommandsDataUtils.getSkipCommands(member.getGuild().getIdLong(), user.getIdLong()) + "\n"
                    + "!stop: " + userCommandsDataUtils.getStopCommands(member.getGuild().getIdLong(), user.getIdLong()) + "\n"
                    + "!info: " + userCommandsDataUtils.getInfoCommands(member.getGuild().getIdLong(), user.getIdLong()), true);
            builder.addField("Fehler                   ", "Verwendung: " + userErrorsDataUtils.getWrongUsageErrors(member.getGuild().getIdLong(), user.getIdLong()) + "\n"
                    + "Kein Befehl: " + userErrorsDataUtils.getNotCommandErrors(member.getGuild().getIdLong(), user.getIdLong()) + "\n"
                    + "Falscher Kanal: " + userErrorsDataUtils.getWrongChannelErrors(member.getGuild().getIdLong(), user.getIdLong()), true);

            builder.addBlankField(false);

            builder.setFooter("Anfrage gestellt am: " + date);

            if(!member.getUser().equals(user)) {
                channel.sendMessage(member.getAsMention() + "   " + user.getAsMention()).queue();
            } else {
                channel.sendMessage(member.getAsMention()).queue();
            }
            channel.sendMessageEmbeds(builder.build()).queue();
        } else {
            EmbedBuilder builder = new EmbedBuilder();
            builder.setColor(MAGENTA);
            builder.setDescription("Verwende bitte \"!info <Name>\". " + member.getAsMention());
            channel.sendMessageEmbeds(builder.build()).queue();
            userErrorsDataUtils.addWrongUsageError(member.getGuild().getIdLong(), member.getUser().getIdLong(), 1);
        }
    }
}