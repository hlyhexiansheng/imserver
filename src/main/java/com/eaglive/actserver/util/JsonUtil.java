package com.eaglive.actserver.util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 2015/11/27.
 */
public class JsonUtil {
    public static List<String> to(String json) {
        List<String> result = new ArrayList<String>();
        Gson gson = new Gson();
        try {
            result = gson.fromJson(json, new TypeToken<List<String>>(){}.getType());
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
        return result;
    }
}
