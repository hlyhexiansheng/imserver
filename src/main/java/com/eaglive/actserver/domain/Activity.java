package com.eaglive.actserver.domain;

import java.util.*;

/**
 * Created by admin on 2015/11/13.
 */
public class Activity {
    private String hash;
    private User ownUser;
    private final Set<User> activiytUsers = new HashSet<User>();

    public User getOwnUser() {
        return ownUser;
    }

    public void setOwnUser(User ownUser) {
        this.ownUser = ownUser;
    }
    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public Activity(String hash) {
        this.hash = hash;
    }
    public void addUser(User user) {
        activiytUsers.add(user);
    }

    public List<User> getUsers() {
        List<User> result = new ArrayList<User>();
        Iterator<User> iterator = this.activiytUsers.iterator();
        while (iterator.hasNext()) {
            User user = iterator.next();
            result.add(user);
        }
        return result;
    }
    public void removeUser(User user) {
        activiytUsers.remove(user);
    }

    public boolean containUser(User user) {
        return activiytUsers.contains(user);
    }

    @Override
    public String toString() {
        return "Activity{" +
                "hash='" + hash + '\'' +
                ", activiytUsers=" + activiytUsers +
                '}';
    }
}
