package com.eaglive.actserver.util;

import com.eaglive.actserver.config.ConfigData;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * Created by admin on 2016/1/15.
 */
public class Badword {
    public static Badword instance = new Badword();
    private Set<String> badwords = new HashSet<String>();

    public void init() {
        try {
            doIinit();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void doIinit() throws IOException {
        System.out.println(ConfigData.badwordFileName);
        BufferedReader br = new BufferedReader(new FileReader(ConfigData.badwordFileName));
        StringBuilder badString = new StringBuilder();
        String line = "";
        while((line = br.readLine()) != null){
            badString.append(line);
        }
        br.close();
        parse(badString.toString());
    }

    private void parse(String bad) {
        StringTokenizer token = new StringTokenizer(bad, " ");
        while (token.hasMoreElements()) {
            badwords.add(token.nextToken());
        }
    }
    public String filter(String word) {
        if(word.length() == 0) {
            return word;
        }
        int length = word.length();
        Set<String> keys = new HashSet<String>();
        for(int i = 0; i < length; i++) {
            for (int j = i+1; j < length; j++) {
                String key = word.substring(i, j);
                if(badwords.contains(key)) {
                    keys.add(key);
                }
            }
        }
        for (String key : keys) {
            word = word.replace(key, "*");
        }
        return word;
    }
}
