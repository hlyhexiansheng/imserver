package com.eaglive.actserver.manager;

import com.eaglive.actserver.domain.Activity;
import com.eaglive.actserver.domain.User;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by admin on 2015/11/12.
 */
public class ActivityManager {
    private final Map<String, Activity> activities = new ConcurrentHashMap<String, Activity>();
    private final Map<String, Activity> users = new ConcurrentHashMap<String, Activity>();
    public static final ActivityManager instance = new ActivityManager();
    public void joinActivity(Activity activity, User user) {
        activity.addUser(user);
        this.activities.put(activity.getHash(), activity);
        this.users.put(user.getUserHash(), activity);
    }

    public void leaveActivity(Activity activity, User user) {
        activity.removeUser(user);
        this.activities.remove(activity.getHash());
        this.users.remove(user.getUserHash());
    }

    public Activity getUserActivity(User user) {
        return this.users.get(user.getUserHash());
    }
    public Activity getActivityOrCreateNew(String hash, User user) {
        if (activities.containsKey(hash)) {
            Activity activity = activities.get(hash);
            activity.addUser(user);
            return activities.get(hash);
        }
        Activity activity = new Activity(hash);
        activity.addUser(user);
        return activity;
    }
}
