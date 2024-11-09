package com.java.sample.mongo.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.java.sample.mongo.entity.Response;
import com.java.sample.mongo.entity.User;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.client.MongoCursor;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.ArrayList;

@Service
public class DbServiceImpl implements DbService {

    Logger log = LoggerFactory.getLogger(DbServiceImpl.class);

    @Value("${mongodb.url}")
    private String DB_URL;

    public String insert(User user) {
        log.info("Request to insert user: " + user);
        try (MongoClient mongoClient = MongoClients.create(DB_URL)) {
            MongoDatabase database = mongoClient.getDatabase("demoDB");
            MongoCollection<Document> collection = database.getCollection("users");
            Document userDoc = new Document("name", user.getName())
                    .append("age", user.getAge())
                    .append("email", user.getEmail());
            collection.insertOne(userDoc);
            log.info("Inserted: " + user);
        }
        return "Inserted: " + user;
    }


        public String insert(List<User> users) {
            log.info("Request to insert user: " + users);
            List<Document> userList = new ArrayList<>();
            try (MongoClient mongoClient = MongoClients.create(DB_URL)) {
                MongoDatabase database = mongoClient.getDatabase("demoDB");
                MongoCollection<Document> collection = database.getCollection("users");
                for(User user : users) {
                    Document userDoc = new Document("name", user.getName())
                            .append("age", user.getAge())
                            .append("email", user.getEmail());
                    userList.add(userDoc);
                }
                collection.insertMany(userList);
               log.info("Inserted " + userList.size() + " users.");
        }
        return "Inserted " + userList.size() + " users.";
    }

    public String readData() {
        log.info("Request to read users");
        return getUserData();
    }

    public String updateData(User user) {
        log.info("Request to update user: " + user);
        try (MongoClient mongoClient = MongoClients.create(DB_URL)) {
            MongoDatabase database = mongoClient.getDatabase("demoDB");
            MongoCollection<Document> collection = database.getCollection("users");
            collection.updateOne(Filters.eq("name", user.getName()),
                    Updates.combine(Updates.set("age", user.getAge()), Updates.set("email", user.getEmail())));
        }
        log.info("Updated "+ user.getName() + " age to " + user.getAge() + ", email " + user.getEmail());
        return "Updated "+ user.getName() + " age to " + user.getAge() + ", email " + user.getEmail();
    }

    public String deleteData(User deleteUser){
        log.info("Request to update user: " + deleteUser);
        try (MongoClient mongoClient = MongoClients.create(DB_URL)) {
            MongoDatabase database = mongoClient.getDatabase("demoDB");
            MongoCollection<Document> collection = database.getCollection("users");
            collection.deleteOne(Filters.eq("name", deleteUser.getName()));
            log.info("Deleted " + deleteUser.getName());
        }
        catch (Exception ex){
            log.error(ex.getMessage());
        }
        return getUserData();
    }

    private String getUserData(){
        ObjectMapper objectMapper = new ObjectMapper();
        List<User> users = new ArrayList<>();
        String result = null;
        try (MongoClient mongoClient = MongoClients.create(DB_URL)) {
            MongoDatabase database = mongoClient.getDatabase("demoDB");
            MongoCollection<Document> collection = database.getCollection("users");
            try (MongoCursor<Document> cursor = collection.find().iterator()) {
                while (cursor.hasNext()) {
                    Document data = cursor.next();
                    User user = new User(data.getString("name"), data.getInteger("age"), data.getString("email"));
                    users.add(user);
                }
            }
        }
        try {
            Response response = new Response(users);
            result = objectMapper.writeValueAsString(response);
            log.info("Fetched data: " + result);
        }
        catch (JsonProcessingException exception){
            log.error(exception.getMessage());
        }
        return result;
    }

}
