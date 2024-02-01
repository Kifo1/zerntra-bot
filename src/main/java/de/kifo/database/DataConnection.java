package de.kifo.database;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import lombok.Data;
import org.bson.Document;

import static com.mongodb.client.MongoClients.create;

@Data
public class DataConnection {

    private final MongoClient mongoClient;
    private final MongoDatabase database;
    private final MongoCollection<Document> collection;

    public DataConnection() {
        mongoClient = create("mongodb+srv://Kifo:qqTmDXgNT6jqwvmI@chestlock.dkztakd.mongodb.net/?retryWrites=true&w=majority");
        database = mongoClient.getDatabase("JavaBot");
        collection = database.getCollection("UserInformation");
    }
}
