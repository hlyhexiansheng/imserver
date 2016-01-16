package com.eaglive.actserver.config;

import com.eaglive.actserver.db.DBConnectionInfo;
import com.eaglive.actserver.util.BaseUtil;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;

/**
 * Created by admin on 2015/11/9.
 */
public class ConfigReader {
    public void loadConfig(String configPath) {
        SAXReader reader = new SAXReader();
        try {
            Document document = reader.read(new File(configPath));
            Element root = document.getRootElement();

            Element serverElement = root.element("server");
            ConfigData.serverPort = Integer.parseInt(serverElement.attributeValue("port"));


            Element redisElement = root.element("redis");
            ConfigData.redisHost = redisElement.attributeValue("host");
            ConfigData.redisPort = Integer.parseInt(redisElement.attributeValue("port"));
            ConfigData.redisMaxIdle = Integer.parseInt(redisElement.attributeValue("maxIdle"));
            ConfigData.redisMaxWaitMills = Integer.parseInt(redisElement.attributeValue("maxWaitMillis"));

            Element apiElement = root.element("api");
            ConfigData.apiUrl = apiElement.attributeValue("url");

            Element badElement = root.element("bad");
            ConfigData.badwordFileName = badElement.attributeValue("filename");


            Element eagLiveElement = root.element("eagliveDB");
            loadDBConnectionInfo(eagLiveElement, ConfigData.eagLiveInfo);


            Element cloudLiveElement = root.element("cloudLiveDB");
            loadDBConnectionInfo(cloudLiveElement, ConfigData.cloudLiveInfo);

            ConfigData.setLastMsgId(BaseUtil.lastMsgId());

        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }


    private void loadDBConnectionInfo(Element dataBaseElement, DBConnectionInfo connectionInfo) {
        connectionInfo.host = dataBaseElement.attributeValue("ip");
        connectionInfo.dbName = dataBaseElement.attributeValue("database");
        connectionInfo.user = dataBaseElement.attributeValue("user");
        connectionInfo.password = dataBaseElement.attributeValue("password");
    }
}
