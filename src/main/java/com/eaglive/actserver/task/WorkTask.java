package com.eaglive.actserver.task;

import com.eaglive.actserver.handler.BaseHandler;

/**
 * Created by Administrator on 16-1-12.
 */
public class WorkTask implements Runnable {

    private BaseHandler handler;

    public WorkTask(BaseHandler handler){
        this.handler = handler;
    }

    public void run() {
        handler.execute();
    }
}

