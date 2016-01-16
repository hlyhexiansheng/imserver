package com.eaglive.actserver.lib;

import com.eaglive.actserver.config.ConfigData;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.squareup.okhttp.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;


/**
 * Created by admin on 2015/11/27.
 */
public class HttpClient {
    private final static Logger logger = LoggerFactory.getLogger(HttpClient.class);
    private final OkHttpClient okHttpClient = new OkHttpClient();

    private Map<String, String> params = new HashMap<String, String>();
    private final String url;
    private JsonObject result;
    private String bodyString;
    public HttpClient(String url) {
        this.url = url;
        this.result = null;
    }

    public HttpClient() {
        this(ConfigData.apiUrl);
    }
    public HttpClient GET() {
        String fullUrl = getFullUrl();
        Request request = new Request.Builder().url(fullUrl).build();
        execute(request);
        return this;
    }

    public boolean isSuccess() {
        if (this.result == null) {
            return false;
        }

        JsonElement msgElem = this.result.get("msg");
        if (msgElem == null) {
            return false;
        }
        return msgElem.getAsInt() == 0;
    }

    public JsonObject getData() {
        JsonObject data = new JsonObject();
        JsonElement dataElement = this.result.get("data");
        if (dataElement != null) {
            data = dataElement.getAsJsonObject();
        }
        return data;
    }
    public List<String> getDataArray() {
        JsonArray array = (JsonArray) this.result.get("data");

        List<String> result = new ArrayList<String>();
        for(int i = 0; i < array.size(); ++i) {
            String activity = array.get(i).getAsString();
            result.add(activity);
        }
        return result;
    }
    public HttpClient setBody(String body) {
        this.bodyString = body;
        return this;
    }

    public HttpClient POST() {
        String fullUrl = getFullUrl();
        System.out.println(fullUrl);
        MediaType mediaType = MediaType.parse("text/json; charset=utf-8");
        RequestBody body = RequestBody.create(mediaType, bodyString);
        Request request = new Request.Builder().url(fullUrl).post(body).build();
        execute(request);
        return this;
    }

    private void execute(Request request) {
        try {
            Response response = okHttpClient.newCall(request).execute();
            JsonParser parser = new JsonParser();
            String text = response.body().string();
            this.result = (JsonObject) parser.parse(text);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            //logger.error(e.getMessage());
        }
    }
    public HttpClient addQueryParam(String key, String value) {
        this.params.put(key, value);
        return this;
    }

    public HttpClient setCmd(String cmd) {
        return addQueryParam("cmd", cmd);
    }

    public String getFullUrl() {
        StringBuilder fullUrl = new StringBuilder(this.url);
        Set<String> keys = this.params.keySet();

        boolean first = true;
        for (String key : keys) {
            String value = this.params.get(key);
            if (first) {
                fullUrl.append("?");
                fullUrl.append(key);
                fullUrl.append("=");
                fullUrl.append(value);
                first = false;
            } else {
                fullUrl.append("&");
                fullUrl.append(key);
                fullUrl.append("=");
                fullUrl.append(value);
            }
        }
        return fullUrl.toString();
    }
}
