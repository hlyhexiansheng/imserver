package com.eaglive.actserver.util;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileLock;

/**
 * Created by admin on 2016/1/18.
 */
public class SingleServer {
    private FileOutputStream fout;
    public SingleServer() {
        try {
            this.fout = new FileOutputStream("server_single.lock");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public boolean trylock() {
        if (this.fout == null) {
            return false;
        }
        FileLock lock = null;
        try {
            lock = this.fout.getChannel().tryLock();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lock != null;
    }

    public void unlock() {
        try {
            this.fout.getChannel().lock().release();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
