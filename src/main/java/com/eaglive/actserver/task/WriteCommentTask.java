package com.eaglive.actserver.task;

import com.eaglive.actserver.db.DBManager;
import com.eaglive.actserver.message.response.ChatMessage;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by admin on 2015/12/1.
 */
public class WriteCommentTask implements Runnable {

    private final ChatMessage chatMessage;
    public WriteCommentTask(ChatMessage chatMessage) {
        this.chatMessage = chatMessage;
    }
    public void run() {
        String sql = "insert into channel_comment(msg_id,comment,channelhash,nick_name,head_photo,userhash,addtime) values(?,?,?,?,?,?,?)";
        Object []params = new Object[]{chatMessage.msgId, chatMessage.data, chatMessage.channel, chatMessage.nickname,
        chatMessage.headPhoto,chatMessage.userHash,chatMessage.readtime};

        DBManager dbManager = DBManager.eagLiveDB();
        Connection connection = dbManager.getConnection();
        dbManager.executeCommand("set names utf8mb4;", new Object[]{}, connection);
        dbManager.executeCommand(sql, params, connection);
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
