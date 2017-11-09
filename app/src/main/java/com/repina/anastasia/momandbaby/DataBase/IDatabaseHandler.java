package com.repina.anastasia.momandbaby.DataBase;

import java.util.List;

interface IDatabaseHandler {
    void add(Object ob);
    Object getObject(int id);
    List<Object> getAll();
    int getSize(String table_name);
    int update(int id, Object ob);
    void delete(int id);
    void deleteAll(String table_name);
}