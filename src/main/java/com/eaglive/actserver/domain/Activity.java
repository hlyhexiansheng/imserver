package com.eaglive.actserver.domain;

import com.eaglive.actserver.redis.RedisExecManager;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by admin on 2015/11/13.
 */
public class Activity {
    private String hash;
    private User ownUser;
    private final AtomicLong loveNumber = new AtomicLong();
    private final Set<User> activiytUsers = new HashSet<User>();

    public User getOwnUser() {
        return ownUser;
    }

    public void setOwnUser(User ownUser) {
        this.ownUser = ownUser;
        addUser(ownUser);
    }
    public String getHash() {
        return hash;
    }

    public Activity(String hash) {
        this.hash = hash;
        initLoveNumber();
    }
    public void addUser(User user) {
        activiytUsers.add(user);
    }

    public long addLoveNumber(long num) {
        refreshRedis(num);
        return loveNumber.addAndGet(num);
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

    public List<String> users(){
        List<String> users = new ArrayList<String>();
        for (User user : this.activiytUsers) {
            users.add(user.getUserHash());
        }
        return users;
    }
    private void refreshRedis(long val) {
        String userKey = "webim_userLoveNumber";
        String channelKey = "webim_channelLoveNumber";
        RedisExecManager redis = RedisExecManager.instance();
        redis.hIncrBy(userKey, ownUser.getUserHash(), val);
        redis.hIncrBy(channelKey, this.hash, val);
    }

    private void initLoveNumber() {
        String channelKey = "webim_channelLoveNumber";
        long val = RedisExecManager.instance().hGet(channelKey, hash);
        this.loveNumber.set(val);
    }

    @Override
    public String toString() {
        return "Activity{" +
                "hash='" + hash + '\'' +
                ", activiytUsers=" + activiytUsers +
                '}';
    }
}
