package com.eaglive.actserver.lib;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by admin on 2016/1/16.
 */
public class JsonInfo {
    public static Logger logger = LoggerFactory.getLogger(JsonInfo.class);
    private JsonElement element;

    public JsonInfo() {
        this.element = new JsonObject();
    }
    public JsonInfo(JsonElement element) {
        this.element = element;
    }

    public JsonInfo(String message) {
        JsonParser parser = new JsonParser();
        JsonElement element = parser.parse(message);
        this.element = element;
    }

    public int getInt(String key) {
        return getInt(key, 0);
    }

    public int getInt(String key, int defaultVal) {
        int value = defaultVal;
        try {
            if(element instanceof JsonObject) {
                JsonObject jsonObject = (JsonObject) element;
                JsonElement subElement = jsonObject.get(key);
                if(subElement != null) {
                    value = subElement.getAsInt();
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return value;
    }
    public String getString(String key) {
        return getString(key, "");
    }

    @Override
    public String toString() {
        if(this.element != null) {
            return this.element.toString();
        }
        return "";
    }

    public String getString(String key, String defaultVal) {
        String value = defaultVal;
        try {
            if(element instanceof JsonObject) {
                JsonObject jsonObject = (JsonObject) element;
                JsonElement subElement = jsonObject.get(key);
                System.out.println(subElement);
                if(subElement != null && !subElement.isJsonNull()) {
                    value = subElement.getAsString();
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return value;
    }

    public JsonInfo getJsonInfo(String key) {
        return getJsonInfo(key, null);
    }

    public JsonInfo getJsonInfo(String key, JsonInfo defaultVal) {
        JsonInfo value = defaultVal;
        try {
            if(element instanceof JsonObject) {
                JsonObject jsonObject = (JsonObject) element;
                JsonElement subElement = jsonObject.get(key);
                if(subElement != null) {
                    value = new JsonInfo(subElement);
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return value;
    }

    public List<String> getStringArray(String key) {
        return getStringArray(key, new ArrayList<String>());
    }
    public List<String> getStringArray(String key, List<String> defaultVal) {
        List<String> value = defaultVal;
        try {
            if(element instanceof JsonObject) {
                JsonObject jsonObject = (JsonObject) element;
                JsonElement subElement = jsonObject.get(key);
                if(subElement != null && subElement.isJsonArray()) {
                    JsonArray array = (JsonArray) subElement;

                    for(int i = 0; i < array.size(); i++) {
                        value.add(array.get(i).getAsString());
                    }
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return value;
    }
}
