package de.kifo.database.utils;

import de.kifo.Main;
import org.bson.Document;

import static de.kifo.Main.getInstance;

public class UserCommandsDataUtils {

    public static int getInviteCommands(Long guildId, Long userId) {
        Document document = getInstance().getDataConnection().getCollection().find(getFilter(guildId, userId)).first();
        Document object = (Document) document.get("commands");

        return object.getInteger("invite");
    }

    public static int getPlayCommands(Long guildId, Long userId) {
        Document document = getInstance().getDataConnection().getCollection().find(getFilter(guildId, userId)).first();
        Document object = (Document) document.get("commands");

        return object.getInteger("play");
    }

    public static int getSkipCommands(Long guildId, Long userId) {
        Document document = getInstance().getDataConnection().getCollection().find(getFilter(guildId, userId)).first();
        Document object = (Document) document.get("commands");

        return object.getInteger("skip");
    }

    public static int getStopCommands(Long guildId, Long userId) {
        Document document = getInstance().getDataConnection().getCollection().find(getFilter(guildId, userId)).first();
        Document object = (Document) document.get("commands");

        return object.getInteger("stop");
    }

    public static int getInfoCommands(Long guildId, Long userId) {
        Document document = getInstance().getDataConnection().getCollection().find(getFilter(guildId, userId)).first();
        Document object = (Document) document.get("commands");

        return object.getInteger("info");
    }



    public static void addInviteCommand(Long guildId, Long userId, int amount) {
        Document foundDocument = getInstance().getDataConnection().getCollection().find(getFilter(guildId, userId)).first();

        if(foundDocument != null) {
            Document updateValue = new Document("commands.invite", (getInviteCommands(guildId, userId) + amount));
            Document updateOperation = new Document("$set", updateValue);

            getInstance().getDataConnection().getCollection().updateOne(foundDocument, updateOperation);
        }
    }

    public static void addPlayCommand(Long guildId, Long userId, int amount) {
        Document foundDocument = getInstance().getDataConnection().getCollection().find(getFilter(guildId, userId)).first();

        if(foundDocument != null) {
            Document updateValue = new Document("commands.play", (getPlayCommands(guildId, userId) + amount));
            Document updateOperation = new Document("$set", updateValue);

            getInstance().getDataConnection().getCollection().updateOne(foundDocument, updateOperation);
        }
    }

    public static void addSkipCommand(Long guildId, Long userId, int amount) {
        Document foundDocument = getInstance().getDataConnection().getCollection().find(getFilter(guildId, userId)).first();

        if(foundDocument != null) {
            Document updateValue = new Document("commands.skip", (getSkipCommands(guildId, userId) + amount));
            Document updateOperation = new Document("$set", updateValue);

            getInstance().getDataConnection().getCollection().updateOne(foundDocument, updateOperation);
        }
    }

    public static void addStopCommand(Long guildId, Long userId, int amount) {
        Document foundDocument = getInstance().getDataConnection().getCollection().find(getFilter(guildId, userId)).first();

        if(foundDocument != null) {
            Document updateValue = new Document("commands.stop", (getStopCommands(guildId, userId) + amount));
            Document updateOperation = new Document("$set", updateValue);

            getInstance().getDataConnection().getCollection().updateOne(foundDocument, updateOperation);
        }
    }

    public static void addInfoCommand(Long guildId, Long userId, int amount) {
        Document foundDocument = getInstance().getDataConnection().getCollection().find(getFilter(guildId, userId)).first();

        if(foundDocument != null) {
            Document updateValue = new Document("commands.info", (getInfoCommands(guildId, userId) + amount));
            Document updateOperation = new Document("$set", updateValue);

            getInstance().getDataConnection().getCollection().updateOne(foundDocument, updateOperation);
        }
    }

    public static void addHelpCommand(Long guildId, Long userId, int amount) {
        Document foundDocument = getInstance().getDataConnection().getCollection().find(getFilter(guildId, userId)).first();
        //TODO Add help command usage to database
    }

    public static Document getFilter(Long guildId, Long userId) {
        return new Document("guildId", guildId).append("userId", userId);
    }
}
