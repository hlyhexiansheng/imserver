package com.eaglive.actserver.lib;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by admin on 2015/11/27.
 */
public class HttpClient {
    private final OkHttpClient okHttpClient = new OkHttpClient();

    private Map<String, String> params = new HashMap<String, String>();
    private final String url;
    private Request request;
    private JsonObject result;

    public HttpClient(String url) {
        this.url = url;
        this.result = null;
    }

    public void GET() {
        String fullUrl = getFullUrl();
        this.request = new Request.Builder().url(fullUrl).build();
        try {
            Response response = okHttpClient.newCall(request).execute();
            JsonParser parser = new JsonParser();
            this.result = (JsonObject) parser.parse(response.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    public void POST() {

    }

    public HttpClient addQueryParam(String key, String value) {
        this.params.put(key, value);
        return this;
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
