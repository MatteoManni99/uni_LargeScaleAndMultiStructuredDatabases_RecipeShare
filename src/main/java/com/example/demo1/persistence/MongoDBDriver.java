package com.example.demo1.persistence;

import com.example.demo1.Configuration;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import java.util.Collections;

public class MongoDBDriver {

    private static final MongoDBDriver driver = new MongoDBDriver();
    private MongoClient mongoClient;
    private MongoDatabase database;

    private MongoDBDriver() {
        mongoClient = MongoClients.create(Configuration.MONGODB_URL);
        database = mongoClient.getDatabase(Configuration.MONGODB_DB);
    }

    public MongoCollection getCollection(String collection) {
        return database.getCollection(collection);
    }

    public static MongoDBDriver getDriver() {return driver;}

    public void closeConnection() {mongoClient.close();}
}