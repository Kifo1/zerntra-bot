package de.kifo.database.utils;

import de.kifo.JavaBot;
import org.bson.Document;

import javax.inject.Inject;

public class UserCommandsDataUtils {

    @Inject
    private JavaBot javaBot;

    public int getInviteCommands(Long guildId, Long userId) {
        Document document = javaBot.getDataConnection().getCollection().find(getFilter(guildId, userId)).first();
        Document object = (Document) document.get("commands");

        return object.getInteger("invite");
    }

    public int getPlayCommands(Long guildId, Long userId) {
        Document document = javaBot.getDataConnection().getCollection().find(getFilter(guildId, userId)).first();
        Document object = (Document) document.get("commands");

        return object.getInteger("play");
    }

    public int getSkipCommands(Long guildId, Long userId) {
        Document document = javaBot.getDataConnection().getCollection().find(getFilter(guildId, userId)).first();
        Document object = (Document) document.get("commands");

        return object.getInteger("skip");
    }

    public int getStopCommands(Long guildId, Long userId) {
        Document document = javaBot.getDataConnection().getCollection().find(getFilter(guildId, userId)).first();
        Document object = (Document) document.get("commands");

        return object.getInteger("stop");
    }

    public int getInfoCommands(Long guildId, Long userId) {
        Document document = javaBot.getDataConnection().getCollection().find(getFilter(guildId, userId)).first();
        Document object = (Document) document.get("commands");

        return object.getInteger("info");
    }



    public void addInviteCommand(Long guildId, Long userId, int amount) {
        Document foundDocument = javaBot.getDataConnection().getCollection().find(getFilter(guildId, userId)).first();

        if(foundDocument != null) {
            Document updateValue = new Document("commands.invite", (getInviteCommands(guildId, userId) + amount));
            Document updateOperation = new Document("$set", updateValue);

            javaBot.getDataConnection().getCollection().updateOne(foundDocument, updateOperation);
        }
    }

    public void addPlayCommand(Long guildId, Long userId, int amount) {
        Document foundDocument = javaBot.getDataConnection().getCollection().find(getFilter(guildId, userId)).first();

        if(foundDocument != null) {
            Document updateValue = new Document("commands.play", (getPlayCommands(guildId, userId) + amount));
            Document updateOperation = new Document("$set", updateValue);

            javaBot.getDataConnection().getCollection().updateOne(foundDocument, updateOperation);
        }
    }

    public void addSkipCommand(Long guildId, Long userId, int amount) {
        Document foundDocument = javaBot.getDataConnection().getCollection().find(getFilter(guildId, userId)).first();

        if(foundDocument != null) {
            Document updateValue = new Document("commands.skip", (getSkipCommands(guildId, userId) + amount));
            Document updateOperation = new Document("$set", updateValue);

            javaBot.getDataConnection().getCollection().updateOne(foundDocument, updateOperation);
        }
    }

    public void addStopCommand(Long guildId, Long userId, int amount) {
        Document foundDocument = javaBot.getDataConnection().getCollection().find(getFilter(guildId, userId)).first();

        if(foundDocument != null) {
            Document updateValue = new Document("commands.stop", (getStopCommands(guildId, userId) + amount));
            Document updateOperation = new Document("$set", updateValue);

            javaBot.getDataConnection().getCollection().updateOne(foundDocument, updateOperation);
        }
    }

    public void addInfoCommand(Long guildId, Long userId, int amount) {
        Document foundDocument = javaBot.getDataConnection().getCollection().find(getFilter(guildId, userId)).first();

        if(foundDocument != null) {
            Document updateValue = new Document("commands.info", (getInfoCommands(guildId, userId) + amount));
            Document updateOperation = new Document("$set", updateValue);

            javaBot.getDataConnection().getCollection().updateOne(foundDocument, updateOperation);
        }
    }

    public void addHelpCommand(Long guildId, Long userId, int amount) {
        Document foundDocument = javaBot.getDataConnection().getCollection().find(getFilter(guildId, userId)).first();
        //TODO Add help command usage to database
    }

    public Document getFilter(Long guildId, Long userId) {
        return new Document("guildId", guildId).append("userId", userId);
    }
}
