package com.eaglive.actserver.monitor;

import com.eaglive.actserver.config.ConfigData;
import com.eaglive.actserver.db.DBManager;
import com.eaglive.actserver.lib.HttpClient;
import com.eaglive.actserver.lib.JsonInfo;
import com.eaglive.actserver.manager.UserManager;
import com.eaglive.actserver.message.response.NotificationMessage;
import com.eaglive.actserver.util.BaseUtil;
import com.eaglive.actserver.util.ServerWriter;
import io.netty.channel.Channel;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

/**
 * Created by admin on 2015/11/13.
 */
public class ExternalMonitor extends JedisPubSub implements Runnable{
    private final static String ADD_NOTIFICATION = "addNotification";
    private final static String ADD_PUSH = "addPush";
    private final Jedis jedis;
    public ExternalMonitor(Jedis jedis) {
        this.jedis = jedis;
    }

    public void run() {
        jedis.subscribe(this, ADD_NOTIFICATION, ADD_PUSH);
    }

    @Override
    public void onMessage(String channel, String message) {
        System.out.println("ExternalMonitor, channel:"+channel+", message:"+message);
        if(channel.equals(ADD_NOTIFICATION)) {
            handleAddNotification(message);
        } else if(channel.equals(ADD_PUSH)) {
            handleAddPush(message);
        }
    }

    private void handleAddNotification(String message) {
        System.out.println("handleAddNotification:" + message);
        JsonInfo notification = new JsonInfo(message);
        String acceptUser = notification.getString("accept_userhash");
        Channel channel = UserManager.instance.getChannel(acceptUser);
        NotificationMessage response = parseNotification(notification, acceptUser);
        if (channel != null) {
            ServerWriter.write(channel, response);
        }
    }

    private NotificationMessage parseNotification(JsonInfo notification, String acceptUser) {
        NotificationMessage response = new NotificationMessage();
        response.title = notification.getString("title");
        response.type = notification.getString("type");
        response.content = notification.getString("content");
        response.extra = notification.getJsonInfo("extra").toString();
        response.id = addToDb(response, acceptUser);
        return response;
    }

    private long addToDb(NotificationMessage message, String acceptUser) {
        DBManager dbManager = DBManager.eagLiveDB();
        java.sql.Connection connection = dbManager.getConnection();
        String sql = "insert into message_node(type,accept_userhash,title,content,is_see,addtime,extra)" +
                " values(?,?,?,?,?,?,?)";
        Object []params = new Object[]{message.type, acceptUser, message.title,
                message.content, 0, BaseUtil.getReadTime(), message.extra};
        dbManager.executeCommand(sql, params, connection);
        return dbManager.getLastInsertId(connection);
    }

    private void handleAddPush(String message) {
        System.out.println("handleAddPush:" + message);
        HttpClient httpClient = new HttpClient(ConfigData.apiUrl);
        httpClient.setCmd("pushaliasmsg");
        httpClient.GET();
    }
}
