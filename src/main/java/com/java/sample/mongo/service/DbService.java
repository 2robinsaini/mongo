package com.java.sample.mongo.service;

import com.java.sample.mongo.entity.User;

import java.util.List;

public interface DbService {

    String insert(User user);

    String insert(List<User> users);

    String readData();

    String updateData(User user);

    String deleteData(User deleteUser);

}
