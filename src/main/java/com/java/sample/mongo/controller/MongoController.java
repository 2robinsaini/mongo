package com.java.sample.mongo.controller;

import com.java.sample.mongo.entity.User;
import com.java.sample.mongo.service.DbService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/mongo")
public class MongoController {

    Logger log = LoggerFactory.getLogger(MongoController.class);

    @Autowired
    private DbService dbService;

    @GetMapping("/data")
    public String getUser(){
        log.info("Request for user data");
       return dbService.readData();
    }

    @PostMapping("/insertUser")
    public String insertUser(@RequestBody User user){
        log.info("Request for insert user " + user);
        return dbService.insert(user);
    }

    @PostMapping("/insertUsers")
    public String insertUsers(@RequestBody List<User> users){
        log.info("Request for insert users " + users);
        return dbService.insert(users);
    }

    @PutMapping("/updateUser")
    public String updateUser(@RequestBody User user){
        log.info("Request for update user " + user);
        return dbService.updateData(user);
    }

    @DeleteMapping("/removeUser")
    public String deleteUser(@RequestBody User user){
        log.info("Request to delete user " + user);
        return dbService.deleteData(user);
    }

}
