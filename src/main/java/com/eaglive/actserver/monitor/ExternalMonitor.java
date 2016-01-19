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

import java.sql.SQLException;

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
        NotificationMessage response = addToDb(notification);
        if (channel != null) {
            ServerWriter.write(channel, response);
        }
    }

    private NotificationMessage addToDb(JsonInfo notification) {

        NotificationMessage message = new NotificationMessage();
        message.title = notification.getString("title");
        message.type = notification.getString("type");
        message.content = notification.getString("content");
        message.extra = notification.getJsonInfo("extra").toString();

        String acceptUser = notification.getString("accept_userhash");

        DBManager dbManager = DBManager.eagLiveDB();
        java.sql.Connection connection = dbManager.getConnection();
        String sql = "insert into message_node(type,accept_userhash,title,content,is_see,addtime,extra)" +
                " values(?,?,?,?,?,?,?)";
        Object []params = new Object[]{message.type, acceptUser, message.title,
                message.content, 0, BaseUtil.getReadTime(), message.extra};
        dbManager.executeCommand(sql, params, connection);
        message.id =  dbManager.getLastInsertId(connection);
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return message;
    }


    private void handleAddPush(String message) {
        System.out.println("handleAddPush:" + message);
        HttpClient httpClient = new HttpClient(ConfigData.apiUrl);
        httpClient.setCmd("pushaliasmsg");
        httpClient.GET();
    }
}
