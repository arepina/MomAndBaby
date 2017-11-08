package com.repina.anastasia.momandbaby.DataBase;

import java.util.List;

public interface IDatabaseHandler {
    public void add(String table_name, Object ob);
    public Object getObject(String table_name, int id);
    public List<Object> getAll(String table_name);
    public int getSize(String table_name);
    public int update(String table_name, int id, String key, String new_value);
    public void delete(Object contact);
    public void deleteAll(String table_name);
}