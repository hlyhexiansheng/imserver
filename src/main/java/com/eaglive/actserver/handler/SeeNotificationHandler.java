package com.eaglive.actserver.handler;

import com.eaglive.actserver.db.DBManager;
import com.eaglive.actserver.util.BaseUtil;

/**
 * Created by admin on 2016/1/18.
 */
public class SeeNotificationHandler extends BaseHandler {
    @Override
    protected void run() {
        String userHash = this.getStringParam("userhash");
        long id = this.getLongParam("id");
        seeNotification(userHash, id);
    }

    private void seeNotification(String userHash, long id) {
        String sql = "update message_node set seetime=?,is_see=1 where id=? AND accept_userhash=?";
        Object []params = new Object[]{BaseUtil.getReadTime(), id, userHash};
        DBManager.eagLiveDB().executeCommand(sql, params);
    }
}
