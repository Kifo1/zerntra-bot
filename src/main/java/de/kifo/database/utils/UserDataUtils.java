package de.kifo.database.utils;

import de.kifo.JavaBot;
import org.bson.Document;

import javax.inject.Inject;

public class UserDataUtils {

    @Inject
    private JavaBot javaBot;

    public int getMessages(Long guildId, Long userId) {
        return javaBot.getDataConnection().getCollection().find(getFilter(guildId, userId)).first().getInteger("messages");
    }

    public int getCommandQuantity(Long guildId, Long userId) {
        return javaBot.getDataConnection().getCollection().find(getFilter(guildId, userId)).first().getInteger("commandQuantity");
    }

    public int getErrorQuantity(Long guildId, Long userId) {
        return javaBot.getDataConnection().getCollection().find(getFilter(guildId, userId)).first().getInteger("errorQuantity");
    }

    public void addMessages(Long guildId, Long userId, int messageAmount) {
        Document foundDocument = javaBot.getDataConnection().getCollection().find(getFilter(guildId, userId)).first();

        if(foundDocument != null) {
            Document updateValue = new Document("messages", (getMessages(guildId, userId) + messageAmount));
            Document updateOperation = new Document("$set", updateValue);

            javaBot.getDataConnection().getCollection().updateOne(foundDocument, updateOperation);
        }
    }

    public void addCommandQuantity(Long guildId, Long userId, int commandAmount) {
        Document foundDocument = javaBot.getDataConnection().getCollection().find(getFilter(guildId, userId)).first();

        if(foundDocument != null) {
            Document updateValue = new Document("commandQuantity", ((getCommandQuantity(guildId, userId)) + commandAmount));
            Document updateOperation = new Document("$set", updateValue);

            javaBot.getDataConnection().getCollection().updateOne(foundDocument, updateOperation);
        }
    }

    public void addErrorQuantity(Long guildId, Long userId, int amount) {
        Document foundDocument = javaBot.getDataConnection().getCollection().find(getFilter(guildId, userId)).first();

        if(foundDocument != null) {
            Document updateValue = new Document("errorQuantity", ((getErrorQuantity(guildId, userId)) + amount));
            Document updateOperation = new Document("$set", updateValue);

            javaBot.getDataConnection().getCollection().updateOne(foundDocument, updateOperation);
        }
    }

    public boolean fileExist(Long guildId, Long userId) {
        Document foundDocument = javaBot.getDataConnection().getCollection().find(getFilter(guildId, userId)).first();

        if(foundDocument != null) return true;

        return false;
    }

    private Document getFilter(Long guildId, Long userId) {
        return new Document("guildId", guildId).append("userId", userId);
    }
}
