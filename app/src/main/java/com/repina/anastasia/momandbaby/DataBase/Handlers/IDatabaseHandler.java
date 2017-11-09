package com.repina.anastasia.momandbaby.DataBase.Handlers;

import java.util.List;

interface IDatabaseHandler {
    long add(Object ob);
    Object getObject(int id);
    List<Object> getAll();
    int getSize(String table_name);
    int update(int id, Object ob);
    void delete(String table_name, int id);
    void deleteAll(String table_name);
}