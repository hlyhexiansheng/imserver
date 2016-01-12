package com.eaglive.actserver;

import com.eaglive.actserver.config.ConfigData;
import com.eaglive.actserver.config.ConfigReader;
import com.eaglive.actserver.monitor.ExternalMonitor;
import com.eaglive.actserver.task.ScanActivityTask;
import com.eaglive.actserver.task.ScanTsTask;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Timer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * Created by admin on 2015/11/6.
 */
public class ActServer {

    public static final ActServer server = new ActServer();
    private JedisPool jedisPool;
    private ExecutorService baseExcutorService;
    private ExternalMonitor externalMonitor;
    private Timer timer;
    public void init() {
        ConfigReader configReader = new ConfigReader();
        configReader.loadConfig("config.xml");
        this.jedisPool = new JedisPool(ConfigData.redisHost, ConfigData.redisPort);
        this.baseExcutorService = Executors.newCachedThreadPool();
    }
    public static void main(String []args) {
        server.init();
        server.startMonitor();
        server.startTask();
        try {
            server.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
		System.out.println("main");

    }

    public void startMonitor() {
        this.externalMonitor = new ExternalMonitor(jedisPool.getResource());
        this.baseExcutorService.submit(this.externalMonitor);
    }

    public void startTask() {
        this.timer = new Timer();
        timer.schedule(new ScanTsTask(), 0, 5000);
        timer.schedule(new ScanActivityTask(), 0, 5000);
    }
    public void run() throws Exception {
        EventLoopGroup mainGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(mainGroup, workerGroup);
            serverBootstrap.channel(NioServerSocketChannel.class);
            serverBootstrap.childHandler(new ActChannelInitializer());
            ChannelFuture future = serverBootstrap.bind(ConfigData.serverPort).sync();
            future.channel().closeFuture().sync();
        } finally {
            mainGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
    public synchronized Jedis getJedis() {
        Jedis jedis = new Jedis(ConfigData.redisHost, ConfigData.redisPort);
        return jedis;
    }
}
