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

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 16-1-13.
 */
public class RedisExecManager {

    private Pool<Jedis> pool;

    private Gson gson = new Gson();

    private RedisExecManager() {
        init();
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
            this.close(jedis);
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
            this.close(jedis);
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
            this.close(jedis);
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
            this.close(jedis);
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
            this.close(jedis);
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
            this.close(jedis);
        }
        return result;
    }
    
    private void close(Jedis jedis){
        if(jedis == null){
            LOGGER.error("jedis is NUll...");
            return;
        }
        jedis.close();
    }

    public boolean exsit(String key){
        Jedis jedis = null;
        boolean result = false;
        try {
            jedis = this.getJedis();
            result = jedis.exists(key);
        }finally {
            this.close(jedis);
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
            this.close(jedis);
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
            this.close(jedis);
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
            this.close(jedis);
        }
        return status;
    }

    public long hAdd(String key, String hashKey) {
        return hIncrBy(key, hashKey, 1);
    }
    public long hIncrBy(String key, String hashKey, long val) {
        Jedis jedis = null;
        long result = 0;
        try {
            jedis = this.getJedis();
            result = jedis.hincrBy(key, hashKey, val);
        }finally {
            this.close(jedis);
        }
        return result;
    }

    public long hGet(String key, String hashKey) {
        return hIncrBy(key, hashKey, 0);
    }

    public Map<String,String> hGetAll(String key) {
        Jedis jedis = null;
        Map<String,String> result = new HashMap<String, String>();
        try {
            jedis = this.getJedis();
            result = jedis.hgetAll(key);
        }finally {
            this.close(jedis);
        }
        return result;
    }

    public boolean hExist(String key, String hashKey) {
        Jedis jedis = null;
        boolean exist = false;
        try {
            jedis = this.getJedis();
            exist = jedis.hexists(key, hashKey);
        }finally {
            this.close(jedis);
        }
        return exist;
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
        if(json == null || json.isEmpty()) {
            return null;
        }
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
            this.close(jedis);
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



    public static void main(String[] args){
        final String key = "";
        JedisExecCallBack jedisExecCallBack = new JedisExecCallBack<String>(){
            public String execute(Jedis jedis) {
                String value = jedis.get(key);
                return value;
            }
        };

        RedisExecManager.instance().execute(jedisExecCallBack);
    }

}

