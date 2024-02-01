package de.kifo.database.files;

import org.bson.Document;

import static de.kifo.Main.getInstance;

public class CreateUserFile {

    public static void createUserFile(Long guildId,Long userId) {
        Document userDocument = new Document("guildId", guildId)
                .append("userId", userId)
                .append("messages", 0)
                .append("commandQuantity", 0)
                .append("errorQuantity", 0)
                .append("errors", new Document("wrongUsage", 0)
                        .append("notCommand",0)
                        .append("wrongChannel", 0))
                .append("commands", new Document("invite", 0)
                        .append("play", 0)
                        .append("skip", 0)
                        .append("stop", 0)
                        .append("info", 0));

        getInstance().getDataConnection().getCollection().insertOne(userDocument);

    }
    /*public static void update() {
        Document update = new Document("notChannel", 0);
        Document updateOperation = new Document("$set", update);
        Document filter = new Document("", null);
        Main.getInstance().getDataConnection().getCollection().updateMany(filter, updateOperation);
    }*/
}
