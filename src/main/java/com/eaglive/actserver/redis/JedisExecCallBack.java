package com.eaglive.actserver.redis;

import redis.clients.jedis.Jedis;

/**
 * Created by Administrator on 16/1/13.
 */

public interface JedisExecCallBack<V>{

    V execute(Jedis jedis);

}