package de.kifo.commands;

import de.kifo.commands.types.ServerCommand;
import de.kifo.database.files.CreateUserFile;
import de.kifo.database.utils.UserCommandsDataUtils;
import de.kifo.database.utils.UserDataUtils;
import de.kifo.database.utils.UserErrorsDataUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class InfoCommand implements ServerCommand {

    @Override
    public void executeCommand(Member member, TextChannel channel, Message message) {
        UserCommandsDataUtils.addInfoCommand(member.getGuild().getIdLong(), member.getUser().getIdLong(), 1);

        String[] args = message.getContentRaw().split(" ");

        if (args.length == 2) {
            String userName = args[1];

            if (member.getGuild().getMembersByName(userName, true).isEmpty()) {
                EmbedBuilder builder = new EmbedBuilder();
                builder.setColor(Color.MAGENTA);
                builder.setDescription("Der Nutzer konnte nicht gefunden werden. " + member.getAsMention());
                channel.sendMessageEmbeds(builder.build()).queue();
                UserErrorsDataUtils.addWrongUsageError(member.getGuild().getIdLong(), member.getUser().getIdLong(), 1);
                return;
            }

            User user = member.getGuild().getMembersByName(userName, true).get(0).getUser();

            if (!UserDataUtils.fileExist(member.getGuild().getIdLong(), user.getIdLong())) {
                CreateUserFile.createUserFile(member.getGuild().getIdLong(), user.getIdLong());
            }

            Calendar calendar = GregorianCalendar.getInstance();
            String date = new SimpleDateFormat("dd.MM.YYYY   kk:mm:ss").format(calendar.getTime()) + " Uhr";

            EmbedBuilder builder = new EmbedBuilder();
            builder.setColor(Color.MAGENTA);

            builder.addField("Name", user.getName(), true);
            builder.addField("Status", member.getGuild().getMembersByName(userName, true).get(0).getOnlineStatus().toString(), true);
            builder.setThumbnail(user.getAvatarUrl());

            builder.addBlankField(true);
            builder.addBlankField(false);

            builder.addField("Gesendete                \nNachrichten", "-> " + UserDataUtils.getMessages(member.getGuild().getIdLong(), user.getIdLong()), true);
            builder.addField("Gesendete                \nBefehle", "-> " + (UserDataUtils.getCommandQuantity(member.getGuild().getIdLong(), user.getIdLong()) + 1), true);
            builder.addField("Verursachte              \nFehler", "-> " + UserDataUtils.getErrorQuantity(member.getGuild().getIdLong(), user.getIdLong()), true);

            builder.addBlankField(false);
            builder.addBlankField(true);

            builder.addField("Befehle                  ", "!invite: " + UserCommandsDataUtils.getInviteCommands(member.getGuild().getIdLong(), user.getIdLong()) + "\n"
                    + "!play: " + UserCommandsDataUtils.getPlayCommands(member.getGuild().getIdLong(), user.getIdLong()) + "\n"
                    + "!skip: " + UserCommandsDataUtils.getSkipCommands(member.getGuild().getIdLong(), user.getIdLong()) + "\n"
                    + "!stop: " + UserCommandsDataUtils.getStopCommands(member.getGuild().getIdLong(), user.getIdLong()) + "\n"
                    + "!info: " + UserCommandsDataUtils.getInfoCommands(member.getGuild().getIdLong(), user.getIdLong()), true);
            builder.addField("Fehler                   ", "Verwendung: " + UserErrorsDataUtils.getWrongUsageErrors(member.getGuild().getIdLong(), user.getIdLong()) + "\n"
                    + "Kein Befehl: " + UserErrorsDataUtils.getNotCommandErrors(member.getGuild().getIdLong(), user.getIdLong()) + "\n"
                    + "Falscher Kanal: " + UserErrorsDataUtils.getWrongChannelErrors(member.getGuild().getIdLong(), user.getIdLong()), true);

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
            builder.setColor(Color.MAGENTA);
            builder.setDescription("Verwende bitte \"!info <Name>\". " + member.getAsMention());
            channel.sendMessageEmbeds(builder.build()).queue();
            UserErrorsDataUtils.addWrongUsageError(member.getGuild().getIdLong(), member.getUser().getIdLong(), 1);
        }
    }
}