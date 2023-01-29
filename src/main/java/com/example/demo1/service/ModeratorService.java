package com.example.demo1.service;

import com.example.demo1.Configuration;
import com.example.demo1.dao.mongo.ModeratorMongoDAO;
import com.example.demo1.model.Moderator;
import com.example.demo1.persistence.MongoDBDriver;
import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import org.bson.Document;
import org.bson.types.ObjectId;

import static com.mongodb.client.model.Filters.eq;

public class ModeratorService {

    public static boolean checkModeratorName(String name) throws MongoException {
        return ModeratorMongoDAO.checkModeratorName(name);
    }

    public static boolean tryLogin(String name, String password) throws MongoException{
        return ModeratorMongoDAO.tryLogin(name, password);
    }

    public static boolean checkRegistration(String name, String password) throws MongoException{
        return ModeratorMongoDAO.checkRegistration(name, password);
    }

}