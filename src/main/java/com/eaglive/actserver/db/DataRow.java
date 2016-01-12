package com.eaglive.actserver.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataRow{
	
    public static final int Type_List = 0;
    public static final int Type_Map = 1;
    private int dataRowType;
    private ArrayList<Object> arrayList;
    private HashMap<String, Object> hashMap;


    public DataRow(){
        this(0);
    }

    public DataRow(int dataRowType)
    {
        arrayList = null;
        hashMap = null;
        this.dataRowType = dataRowType;
        if(dataRowType == 0)
            arrayList = new ArrayList<Object>();
        else
        if(dataRowType == 1)
            hashMap = new HashMap<String, Object>();
    }

    public int getDataRowType()
    {
        return dataRowType;
    }

    public void addItem(Object o){
        if(arrayList != null)
            arrayList.add(o);
    }

    public void addItem(String key, Object o)
    {
        if(hashMap != null)
            hashMap.put(key, o);
    }

    public Object getItem(int key)
    {
        try
        {
            if(arrayList != null)
                return (Object)arrayList.get(key);
        }
        catch(IndexOutOfBoundsException indexoutofboundsexception)
        {
            return null;
        }
        return null;
    }

    public Object getItem(String key){
        if(hashMap != null)
            return (Object)hashMap.get(key);
        else
            return null;
    }

    public List<Object> getDataAsList(){
        return arrayList;
    }

    public Map<String, Object> getDataAsMap(){
        return hashMap;
    }

    public Object getObject(){
        if(dataRowType == 0)
            return getDataAsList();
        else
            return getDataAsMap();
    }

    
}

