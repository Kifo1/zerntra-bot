package de.kifo.database.utils;

import de.kifo.JavaBot;
import org.bson.Document;

import javax.inject.Inject;

public class UserErrorsDataUtils {

    @Inject
    private JavaBot javaBot;

    public int getWrongUsageErrors(Long guildId, Long userId) {
        Document document = javaBot.getDataConnection().getCollection().find(getFilter(guildId, userId)).first();
        Document object = (Document) document.get("errors");

        return object.getInteger("wrongUsage");
    }

    public int getNotCommandErrors(Long guildId, Long userId) {
        Document document = javaBot.getDataConnection().getCollection().find(getFilter(guildId, userId)).first();
        Document object = (Document) document.get("errors");

        return object.getInteger("notCommand");
    }

    public int getWrongChannelErrors(Long guildId, Long userId) {
        Document document = javaBot.getDataConnection().getCollection().find(getFilter(guildId, userId)).first();
        Document object = (Document) document.get("errors");

        return object.getInteger("wrongChannel");
    }



    public void addWrongUsageError(Long guildId, Long userId, int amount) {
        UserDataUtils userDataUtils = javaBot.getInjector().getInstance(UserDataUtils.class);
        userDataUtils.addErrorQuantity(guildId, userId, amount);
        Document foundDocument = javaBot.getDataConnection().getCollection().find(getFilter(guildId, userId)).first();

        if(foundDocument != null) {
            Document updateValue = new Document("errors.wrongUsage", (getWrongUsageErrors(guildId, userId) + amount));
            Document updateOperation = new Document("$set", updateValue);

            javaBot.getDataConnection().getCollection().updateOne(foundDocument, updateOperation);
        }
    }

    public void addNotCommandError(Long guildId, Long userId, int amount) {
        UserDataUtils userDataUtils = javaBot.getInjector().getInstance(UserDataUtils.class);
        userDataUtils.addErrorQuantity(guildId, userId, amount);
        Document foundDocument = javaBot.getDataConnection().getCollection().find(getFilter(guildId, userId)).first();

        if(foundDocument != null) {
            Document updateValue = new Document("errors.notCommand", (getNotCommandErrors(guildId, userId) + amount));
            Document updateOperation = new Document("$set", updateValue);

            javaBot.getDataConnection().getCollection().updateOne(foundDocument, updateOperation);
        }
    }

    public void addWrongChannelError(Long guildId, Long userId, int amount) {
        UserDataUtils userDataUtils = javaBot.getInjector().getInstance(UserDataUtils.class);
        userDataUtils.addErrorQuantity(guildId, userId, amount);
        Document foundDocument = javaBot.getDataConnection().getCollection().find(getFilter(guildId, userId)).first();

        if(foundDocument != null) {
            Document updateValue = new Document("errors.wrongChannel", (getWrongChannelErrors(guildId, userId) + amount));
            Document updateOperation = new Document("$set", updateValue);

            javaBot.getDataConnection().getCollection().updateOne(foundDocument, updateOperation);
        }
    }

    public Document getFilter(Long guildId, Long userId) {
        return new Document("guildId", guildId).append("userId", userId);
    }
}
