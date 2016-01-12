package com.eaglive.actserver;

import com.eaglive.actserver.task.WorkTask;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Administrator on 16-1-12.
 */
public class EventWorkPoolFacade {
    
    public static final EventWorkPoolFacade instance = new EventWorkPoolFacade();
    
    public static final int WORK_THREAD_COUNT = 10;
    
    private ExecutorService service;
    
    public EventWorkPoolFacade(){
        init();
    }
    
    private void init(){
        this.service = Executors.newFixedThreadPool(WORK_THREAD_COUNT);
    }
    
    public void addWork(WorkTask workTask){
        if(workTask != null){
            this.service.submit(workTask);
        }
    }
}

