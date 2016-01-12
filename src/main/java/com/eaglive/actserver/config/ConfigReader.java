package com.eaglive.actserver.config;

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

            Element apiElement = root.element("api");
            ConfigData.apiUrl = apiElement.attributeValue("url");

            Element dataBaseElement = root.element("databaseUrl");
            ConfigData.DATABASE_URL = dataBaseElement.getStringValue();

            ConfigData.setLastMsgId(BaseUtil.lastMsgId());

        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }
}
