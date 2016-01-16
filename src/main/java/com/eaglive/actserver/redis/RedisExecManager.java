package com.eaglive.actserver.redis;

import com.eaglive.actserver.config.ConfigData;
import com.eaglive.actserver.lib.JsonInfo;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.util.Pool;

/**
 * Created by Administrator on 16-1-13.
 */
public class RedisExecManager {

    private Pool<Jedis> pool;

    private Gson gson = new Gson();

    private RedisExecManager() {
        this.pool = new JedisPool(ConfigData.redisHost, ConfigData.redisPort);
    }

    /**
     * 包装执行jedis过程，再也不用担心jedis忘记释放的问题了.
     * @param callBack
     * @param <T>
     * @return 回调的返回值业务自己转
     */
    public <T> T execute(JedisExecCallBack<T> callBack){
        Jedis jedis = null;
        T value = null;
        try {
            jedis = this.getJedis();
            value = callBack.execute(jedis);
        }catch (Exception e){
            LOGGER.error("execute JeidsCall error.." + e.getMessage());
        } finally {
            jedis.close();
        }
        return value;
    }


    public <T> T getInfoByKey(String key ,Class<T> tClass){
        Jedis jedis = null;
        String value = null;
        try {
            jedis = this.getJedis();
            value = jedis.get(key);
        }finally {
            jedis.close();
        }
        return this.fromJson(value,tClass);
    }

    public String getInfoByKey(String key){
        Jedis jedis = null;
        String value = null;
        try {
            jedis = this.getJedis();
            value = jedis.get(key);
        }finally {
            jedis.close();
        }
        return value;
    }

    public JsonInfo getJsonInfo(String key) {
        Jedis jedis = null;
        JsonInfo jsonInfo = null;
        try {
            jedis = this.getJedis();
            String value = jedis.get(key);
            jsonInfo = fromJson(value);
        }finally {
            jedis.close();
        }
        return jsonInfo;
    }

    /**
     * 指定过期时间
     * @param key
     * @param unixTime
     * @return
     */
    public long expireAt(String key,long unixTime){
        Jedis jedis = null;
        long result = 0;
        try {
            jedis = this.getJedis();
            result = jedis.expireAt(key, unixTime);
        }finally {
            jedis.close();
        }
        return result;
    }

    /**
     * 指定在多少秒之后过期
     * @param key
     * @param seconds
     * @return
     */
    public long expire(String key,int seconds){
        Jedis jedis = null;
        long result = 0;
        try {
            jedis = this.getJedis();
            result = jedis.expire(key, seconds);
        }finally {
            jedis.close();
        }
        return result;
    }

    public boolean exsit(String key){
        Jedis jedis = null;
        boolean result = false;
        try {
            jedis = this.getJedis();
            result = jedis.exists(key);
        }finally {
            jedis.close();
        }
        return result;
    }

    public String set(String key,Object object){
        Jedis jedis = null;
        String status = null;
        try {
            jedis = this.getJedis();
            status = jedis.set(key, this.toJson(object));
        }finally {
            jedis.close();
        }
        return status;
    }

    public String setEx(String key,Object object,int seconds){
        Jedis jedis = null;
        String status = null;
        try {
            jedis = this.getJedis();
            status = jedis.setex(key, seconds, this.toJson(object));
        }finally {
            jedis.close();
        }
        return status;
    }

    public long delete(String... key){
        Jedis jedis = null;
        long status;
        try {
            jedis = this.getJedis();
            status = jedis.del(key);
        }finally {
            jedis.close();
        }
        return status;
    }

    private <T> T fromJson(String jsonStr,Class<T> tClass){
        T t = null;
        try {
            t =  gson.fromJson(jsonStr, tClass);
        }catch (Exception ex){
            LOGGER.error("GSON convert error --> " + jsonStr + " convert to "+ tClass);
        }
        return t;
    }

    private String toJson(Object object){
        String jsonStr = null;
        try {
            jsonStr = gson.toJson(object);
        }catch (Exception ex){
            LOGGER.error("Gson convert error -->" + object);
        }
        return jsonStr;
    }

    private JsonInfo fromJson(String json) {
        JsonParser parser = new JsonParser();
        JsonElement element = parser.parse(json);
        return new JsonInfo(element);
    }
    public void init(){
        GenericObjectPoolConfig config = new GenericObjectPoolConfig();
        config.setMaxIdle(ConfigData.redisMaxIdle);
        config.setMaxWaitMillis(ConfigData.redisMaxWaitMills);
        this.pool = new JedisPool(config, ConfigData.redisHost,ConfigData.redisPort);
    }

    public boolean sContain(String key, String value) {
        Jedis jedis = null;
        boolean isMember = false;
        try {
            jedis = this.getJedis();
            isMember = jedis.sismember(key, value);
        }finally {
            jedis.close();
        }
        return isMember;
    }
    public Jedis getJedis(){
        return this.pool.getResource();
    }

    public static RedisExecManager instance() {
        return _instance;
    }

    private static final RedisExecManager _instance = new RedisExecManager();

    private static final Logger LOGGER = LoggerFactory.getLogger(RedisExecManager.class);

}

